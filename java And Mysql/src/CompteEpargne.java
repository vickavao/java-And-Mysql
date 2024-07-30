public class CompteEpargne extends CompteBancaire {
    private double taux;

    public CompteEpargne(String numeroCompte, String nomTitulaire, double solde) {
        super(numeroCompte, nomTitulaire, solde);
    }

    public CompteEpargne(String numeroCompte, String nomTitulaire, double solde, double taux) {
        super(numeroCompte, nomTitulaire, solde);
        this.taux = taux;
    }

    public double getTaux() {
        return taux;
    }

    public void setTaux(double taux) {
        this.taux = taux;
    }

    @Override
    public void afficherDetails() {
        System.out.println("Compte Épargne - Numéro: " + getNumeroCompte() + ", Titulaire: " + getNomTitulaire() + ", Solde: " + getSolde() + ", Taux: " + taux);
    }
}