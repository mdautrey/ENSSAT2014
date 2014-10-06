package model;

/**
 * Created by mdautrey on 29/09/14.
 */
public class Individu {
    private long id;// ajoute au cas ou il y aurait besoin d une cle primaire pour travailler avec JdbcTemplate
    private String prenom;
    private String nom;
    private String civilite;

    public Individu(){
        this.prenom = "Sam";
        this.nom = "Sick";
        this.civilite = "Mister";
    }

    public Individu(long id, String prenom, String nom) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
    }

    // getters
    public long getId(){ return id;}
    public String getPrenom() {
        return prenom;
    }
    public String getNom(){
        return nom;
    }
    public String getCivilite(){
        return civilite;
    }

    // setters
    public void setId(long id){
        this.id = id;
    }
    public void setPrenom(String prenom){
        this.prenom = prenom;
    }
    public void setNom(String nom){
        this.nom = nom;
    }
    public void setCivilite(String civilite){
        this.civilite = civilite;
    }
}
