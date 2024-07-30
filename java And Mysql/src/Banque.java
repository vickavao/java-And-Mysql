import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Banque {
    private Map<String, CompteBancaire> comptes = new HashMap<>();

    public void ajouterCompte(CompteBancaire compte) {
        comptes.put(compte.getNumeroCompte(), compte);
        String sql = compte instanceof CompteCourant ?
                "INSERT INTO comptecourant (numeroCompte, nom, solde_initial, decouverte) VALUES (?, ?, ?, ?)" :
                "INSERT INTO compteEpargne (numeroCompte, nom, solde_initial, taux) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, compte.getNumeroCompte());
            pstmt.setString(2, compte.getNomTitulaire());
            pstmt.setDouble(3, compte.getSolde());
            if (compte instanceof CompteCourant) {
                pstmt.setDouble(4, ((CompteCourant) compte).getDecouvert());
            } else {
                pstmt.setDouble(4, ((CompteEpargne) compte).getTaux());
            }
            pstmt.executeUpdate();
            System.out.println("Compte ajouté à la base de données.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du compte à la base de données: " + e.getMessage());
        }
    }

    public void supprimerCompte(String numeroCompte) throws CompteNonTrouveException {
        CompteBancaire compte = comptes.remove(numeroCompte);
        if (compte == null) {
            throw new CompteNonTrouveException("Compte non trouvé.");
        }
        String sql = compte instanceof CompteCourant ?
                "DELETE FROM comptecourant WHERE numeroCompte = ?" :
                "DELETE FROM compteEpargne WHERE numeroCompte = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, numeroCompte);
            pstmt.executeUpdate();
            System.out.println("Compte supprimé de la base de données.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du compte de la base de données: " + e.getMessage());
        }
    }

    public void mettreAJourTitulaire(String numeroCompte, String nouveauNom) {
        CompteBancaire compte = comptes.get(numeroCompte);
        if (compte != null) {
            compte.setNomTitulaire(nouveauNom);
            String sql = compte instanceof CompteCourant ?
                    "UPDATE comptecourant SET nom = ? WHERE numeroCompte = ?" :
                    "UPDATE compteEpargne SET nom = ? WHERE numeroCompte = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, nouveauNom);
                pstmt.setString(2, numeroCompte);
                pstmt.executeUpdate();
                System.out.println("Nom du titulaire mis à jour dans la base de données.");
            } catch (SQLException e) {
                System.out.println("Erreur lors de la mise à jour du nom du titulaire dans la base de données: " + e.getMessage());
            }
        } else {
            System.out.println("Compte non trouvé.");
        }
    }

    public CompteBancaire rechercherCompteParNom(String nomTitulaire) throws CompteNonTrouveException {
        for (CompteBancaire compte : comptes.values()) {
            if (compte.getNomTitulaire().equalsIgnoreCase(nomTitulaire)) {
                return compte;
            }
        }
        throw new CompteNonTrouveException("Compte non trouvé.");
    }

    public void afficherComptesParLettre(char lettre) {
        for (CompteBancaire compte : comptes.values()) {
            if (compte.getNomTitulaire().charAt(0) == lettre) {
                compte.afficherDetails();
            }
        }
    }

    public void afficherNombreComptesParType() {
        int nbCourant = 0, nbEpargne = 0;
        for (CompteBancaire compte : comptes.values()) {
            if (compte instanceof CompteCourant) {
                nbCourant++;
            } else if (compte instanceof CompteEpargne) {
                nbEpargne++;
            }
        }
        System.out.println("Nombre de Comptes Courants: " + nbCourant);
        System.out.println("Nombre de Comptes Épargne: " + nbEpargne);
    }

    public void afficherComptesParType(Class<?> type) {
        for (CompteBancaire compte : comptes.values()) {
            if (type.isInstance(compte)) {
                compte.afficherDetails();
            }
        }
    }

    public void afficherDetailsCompte(String numeroCompte) {
        CompteBancaire compte = comptes.get(numeroCompte);
        if (compte != null) {
            compte.afficherDetails();
        } else {
            System.out.println("Compte non trouvé.");
        }
    }

    public void transfererFonds(String numeroCompteSource, String numeroCompteDestination, double montant) {
        CompteBancaire source = comptes.get(numeroCompteSource);
        CompteBancaire destination = comptes.get(numeroCompteDestination);

        if (source != null && destination != null && source.getSolde() >= montant) {
            source.setSolde(source.getSolde() - montant);
            destination.setSolde(destination.getSolde() + montant);
            String sqlSource = source instanceof CompteCourant ?
                    "UPDATE comptecourant SET solde_initial = ? WHERE numeroCompte = ?" :
                    "UPDATE compteEpargne SET solde_initial = ? WHERE numeroCompte = ?";
            String sqlDestination = destination instanceof CompteCourant ?
                    "UPDATE comptecourant SET solde_initial = ? WHERE numeroCompte = ?" :
                    "UPDATE compteEpargne SET solde_initial = ? WHERE numeroCompte = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement pstmtSource = connection.prepareStatement(sqlSource);
                 PreparedStatement pstmtDestination = connection.prepareStatement(sqlDestination)) {
                pstmtSource.setDouble(1, source.getSolde());
                pstmtSource.setString(2, numeroCompteSource);
                pstmtSource.executeUpdate();

                pstmtDestination.setDouble(1, destination.getSolde());
                pstmtDestination.setString(2, numeroCompteDestination);
                pstmtDestination.executeUpdate();

                System.out.println("Transfert réussi de " + montant + " de " + numeroCompteSource + " à " + numeroCompteDestination);
            } catch (SQLException e) {
                System.out.println("Erreur lors du transfert de fonds: " + e.getMessage());
            }
        } else {
            System.out.println("Transfert échoué.");
        }
    }
}