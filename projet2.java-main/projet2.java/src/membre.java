package com.bibliotheque.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe représentant un membre de la bibliothèque.
 * Gère les informations d'inscription des membres.
 * 
 * @author Votre Nom
 * @version 1.0
 */
public class Membre {
    // Attributs privés (Encapsulation)
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private Date adhesionDate;
    
    // Format pour l'affichage des dates
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Constructeur pour créer un nouveau membre (sans ID).
     * La date d'adhésion est automatiquement définie à la date actuelle.
     */
    public Membre(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adhesionDate = new Date(); // Date actuelle
    }
    
    /**
     * Constructeur complet avec ID et date (pour récupération depuis la BD).
     */
    public Membre(int id, String nom, String prenom, String email, Date adhesionDate) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adhesionDate = adhesionDate;
    }
    
    // Getters et Setters (Encapsulation)
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public String getNom() { 
        return nom; 
    }
    
    public void setNom(String nom) { 
        this.nom = nom; 
    }
    
    public String getPrenom() { 
        return prenom; 
    }
    
    public void setPrenom(String prenom) { 
        this.prenom = prenom; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public Date getAdhesionDate() { 
        return adhesionDate; 
    }
    
    public void setAdhesionDate(Date adhesionDate) { 
        this.adhesionDate = adhesionDate; 
    }
    
    /**
     * Retourne le nom complet du membre.
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    /**
     * Affiche les détails du membre de manière formatée.
     * Implémentation du Polymorphisme.
     */
    public void afficherDetails() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          DÉTAILS DU MEMBRE             ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ ID               : " + String.format("%-19d", id) + "║");
        System.out.println("║ Nom              : " + String.format("%-19s", nom) + "║");
        System.out.println("║ Prénom           : " + String.format("%-19s", prenom) + "║");
        System.out.println("║ Email            : " + String.format("%-19s", email.length() > 19 ? email.substring(0, 16) + "..." : email) + "║");
        System.out.println("║ Date d'adhésion  : " + String.format("%-19s", dateFormat.format(adhesionDate)) + "║");
        System.out.println("╚════════════════════════════════════════╝");
    }
    
    @Override
    public String toString() {
        return "Membre{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", adhesionDate=" + dateFormat.format(adhesionDate) +
                '}';
    }
}