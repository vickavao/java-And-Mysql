public class CompteCourant extends CompteBancaire {
    private double decouvert;

    public CompteCourant(String numeroCompte, String nomTitulaire, double solde) {
        super(numeroCompte, nomTitulaire, solde);
    }

    public CompteCourant(String numeroCompte, String nomTitulaire, double solde, double decouvert) {
        super(numeroCompte, nomTitulaire, solde);
        this.decouvert = decouvert;
    }

    public double getDecouvert() {
        return decouvert;
    }

    public void setDecouvert(double decouvert) {
        this.decouvert = decouvert;
    }

    @Override
    public void afficherDetails() {
        System.out.println("Compte Courant - Numéro: " + getNumeroCompte() + ", Titulaire: " + getNomTitulaire() + ", Solde: " + getSolde() + ", Découvert: " + decouvert);
    }
}