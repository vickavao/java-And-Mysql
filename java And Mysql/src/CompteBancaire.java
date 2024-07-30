public abstract class CompteBancaire {
    private String numeroCompte;
    private String nomTitulaire;
    private double solde;

    public CompteBancaire(String numeroCompte, String nomTitulaire, double solde) {
        this.numeroCompte = numeroCompte;
        this.nomTitulaire = nomTitulaire;
        this.solde = solde;
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }

    public String getNomTitulaire() {
        return nomTitulaire;
    }

    public void setNomTitulaire(String nomTitulaire) {
        this.nomTitulaire = nomTitulaire;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public abstract void afficherDetails();
}