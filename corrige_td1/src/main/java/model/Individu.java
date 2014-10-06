package model;

/**
 * Created by mdautrey on 29/09/14.
 */
public class Individu {
    private String prenom;
    private String nom;
    private String civilite;
    private int age;

    public Individu(){
        this.prenom = "Sam";
        this.nom = "Sick";
        this.civilite = "Mister";
        this.age = 34;
    }

    // getters
    public String getPrenom() {
        return prenom;
    }
    public String getNom(){
        return nom;
    }
    public String getCivilite(){
        return civilite;
    }
    public int getAge(){
        return age;
    }

}
