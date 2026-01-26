package com.bibliotheque.model;

/**
 * Classe représentant un livre dans la bibliothèque.
 * Contient toutes les informations relatives à un livre.
 * 
 * @author Votre Nom
 * @version 1.0
 */
public class Livre {
    // Attributs privés (Encapsulation)
    private int id;
    private String titre;
    private String auteur;
    private String categorie;
    private int nombreExemplaires;
    
    /**
     * Constructeur pour créer un nouveau livre (sans ID).
     * L'ID sera généré automatiquement par la base de données.
     */
    public Livre(String titre, String auteur, String categorie, int nombreExemplaires) {
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.nombreExemplaires = nombreExemplaires;
    }
    
    /**
     * Constructeur complet avec ID (pour récupération depuis la base de données).
     */
    public Livre(int id, String titre, String auteur, String categorie, int nombreExemplaires) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.nombreExemplaires = nombreExemplaires;
    }
    
    // Getters et Setters (Encapsulation)
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public String getTitre() { 
        return titre; 
    }
    
    public void setTitre(String titre) { 
        this.titre = titre; 
    }
    
    public String getAuteur() { 
        return auteur; 
    }
    
    public void setAuteur(String auteur) { 
        this.auteur = auteur; 
    }
    
    public String getCategorie() { 
        return categorie; 
    }
    
    public void setCategorie(String categorie) { 
        this.categorie = categorie; 
    }
    
    public int getNombreExemplaires() { 
        return nombreExemplaires; 
    }
    
    public void setNombreExemplaires(int nombreExemplaires) { 
        this.nombreExemplaires = nombreExemplaires; 
    }
    
    /**
     * Affiche les détails du livre de manière formatée.
     * Implémentation du Polymorphisme - chaque classe a sa propre version.
     */
    public void afficherDetails() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           DÉTAILS DU LIVRE             ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ ID               : " + String.format("%-19d", id) + "║");
        System.out.println("║ Titre            : " + String.format("%-19s", titre.length() > 19 ? titre.substring(0, 16) + "..." : titre) + "║");
        System.out.println("║ Auteur           : " + String.format("%-19s", auteur.length() > 19 ? auteur.substring(0, 16) + "..." : auteur) + "║");
        System.out.println("║ Catégorie        : " + String.format("%-19s", categorie) + "║");
        System.out.println("║ Exemplaires      : " + String.format("%-19d", nombreExemplaires) + "║");
        System.out.println("╚════════════════════════════════════════╝");
    }
    
    /**
     * Représentation textuelle de l'objet Livre.
     */
    @Override
    public String toString() {
        return "Livre{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", auteur='" + auteur + '\'' +
                ", categorie='" + categorie + '\'' +
                ", nombreExemplaires=" + nombreExemplaires +
                '}';
    }
    
    /**
     * Vérifie si le livre est disponible pour emprunt.
     */
    public boolean estDisponible() {
        return nombreExemplaires > 0;
    }
}