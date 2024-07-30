import java.io.*;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {
    private static Banque banque = new Banque();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        chargerDonnees();
        afficherMenu();
    }

    private static void afficherMenu() {
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Ajouter un compte bancaire");
            System.out.println("2. Supprimer un compte bancaire");
            System.out.println("3. Modifier un compte bancaire par son identifiant");
            System.out.println("4. Rechercher un compte bancaire par nom de titulaire");
            System.out.println("5. Lister les comptes bancaires en saisissant une lettre alphabétique");
            System.out.println("6. Afficher le nombre de comptes bancaires par type");
            System.out.println("7. Afficher les comptes par type");
            System.out.println("8. Afficher les détails d'un compte par son identifiant");
            System.out.println("9. Transférer des fonds entre comptes");
            System.out.println("10. Mettre à jour les informations du titulaire");
            System.out.println("11. Quitter");

            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer la nouvelle ligne

            switch (choix) {
                case 1:
                    ajouterCompte();
                    break;
                case 2:
                    supprimerCompte();
                    break;
                case 3:
                    modifierCompte();
                    break;
                case 4:
                    rechercherCompteParNom();
                    break;
                case 5:
                    listerComptesParLettre();
                    break;
                case 6:
                    banque.afficherNombreComptesParType();
                    break;
                case 7:
                    afficherComptesParType();
                    break;
                case 8:
                    afficherDetailsCompte();
                    break;
                case 9:
                    transfererFonds();
                    break;
                case 10:
                    mettreAJourTitulaire();
                    break;
                case 11:
                    sauvegarderDonnees();
                    System.out.println("Au revoir !");
                    return;
                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
            }
        }
    }

    private static void ajouterCompte() {
        System.out.println("Type de compte (1: Courant, 2: Épargne): ");
        int type = scanner.nextInt();
        scanner.nextLine(); // Consommer la nouvelle ligne

        System.out.println("Numéro de compte: ");
        String numero = scanner.nextLine();

        System.out.println("Nom du titulaire: ");
        String nom = scanner.nextLine();

        double solde = 0;
        while (true) {
            System.out.println("Solde initial: ");
            try {
                solde = scanner.nextDouble();
                scanner.nextLine(); // Consommer la nouvelle ligne
                break;
            } catch (InputMismatchException e) {
                System.out.println("Entrée invalide. Veuillez entrer un nombre.");
                scanner.nextLine(); // Consommer la ligne invalide
            }
        }

        if (type == 1) {
            double decouvert = 0;
            while (true) {
                System.out.println("Découvert autorisé: ");
                try {
                    decouvert = scanner.nextDouble();
                    scanner.nextLine(); // Consommer la nouvelle ligne
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Entrée invalide. Veuillez entrer un nombre.");
                    scanner.nextLine(); // Consommer la ligne invalide
                }
            }
            banque.ajouterCompte(new CompteCourant(numero, nom, solde, decouvert));
        } else if (type == 2) {
            double taux = 0;
            while (true) {
                System.out.println("Taux d'intérêt: ");
                try {
                    taux = scanner.nextDouble();
                    scanner.nextLine(); // Consommer la nouvelle ligne
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Entrée invalide. Veuillez entrer un nombre.");
                    scanner.nextLine(); // Consommer la ligne invalide
                }
            }
            banque.ajouterCompte(new CompteEpargne(numero, nom, solde, taux));
        } else {
            System.out.println("Type de compte invalide.");
        }
    }
    private static void supprimerCompte() {
        System.out.println("Numéro de compte à supprimer: ");
        String numero = scanner.nextLine();
        try {
            banque.supprimerCompte(numero);
        } catch (CompteNonTrouveException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void modifierCompte() {
        System.out.println("Numéro de compte à modifier: ");
        String numero = scanner.nextLine();
        System.out.println("Nouveau nom du titulaire: ");
        String nouveauNom = scanner.nextLine();
        banque.mettreAJourTitulaire(numero, nouveauNom);
    }

    private static void rechercherCompteParNom() {
        System.out.println("Nom du titulaire: ");
        String nom = scanner.nextLine();
        try {
            CompteBancaire compte = banque.rechercherCompteParNom(nom);
            compte.afficherDetails();
        } catch (CompteNonTrouveException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void listerComptesParLettre() {
        System.out.println("Lettre alphabétique: ");
        char lettre = scanner.nextLine().charAt(0);
        banque.afficherComptesParLettre(lettre);
    }

    private static void afficherComptesParType() {
        System.out.println("Type de compte (1: Courant, 2: Épargne): ");
        int type = scanner.nextInt();
        scanner.nextLine(); // Consommer la nouvelle ligne

        if (type == 1) {
            banque.afficherComptesParType(CompteCourant.class);
        } else if (type == 2) {
            banque.afficherComptesParType(CompteEpargne.class);
        } else {
            System.out.println("Type de compte invalide.");
        }
    }

    private static void afficherDetailsCompte() {
        System.out.println("Numéro de compte: ");
        String numero = scanner.nextLine();
        banque.afficherDetailsCompte(numero);
    }

    private static void transfererFonds() {
        System.out.println("Numéro de compte source: ");
        String source = scanner.nextLine();
        System.out.println("Numéro de compte destination: ");
        String destination = scanner.nextLine();
        System.out.println("Montant à transférer: ");
        double montant = scanner.nextDouble();
        scanner.nextLine(); // Consommer la nouvelle ligne
        banque.transfererFonds(source, destination, montant);
    }

    private static void mettreAJourTitulaire() {
        System.out.println("Numéro de compte: ");
        String numero = scanner.nextLine();
        System.out.println("Nouveau nom du titulaire: ");
        String nouveauNom = scanner.nextLine();
        banque.mettreAJourTitulaire(numero, nouveauNom);
    }

    private static void sauvegarderDonnees() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("banque.txt"))) {
            oos.writeObject(banque);
            System.out.println("Données sauvegardées avec succès.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde des données: " + e.getMessage());
        }
    }

    private static void chargerDonnees() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("banque.txt"))) {
            banque = (Banque) ois.readObject();
            System.out.println("Données chargées avec succès.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur lors du chargement des données: " + e.getMessage());
        }
    }
}