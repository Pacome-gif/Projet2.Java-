package com.bibliotheque.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Classe représentant un emprunt de livre par un membre.
 * Gère la logique d'emprunt, de retour et de calcul des pénalités.
 * 
 * @author Votre Nom
 * @version 1.0
 */
public class Emprunt {
    // Attributs privés (Encapsulation)
    private int idEmprunt;
    private int membreId;
    private int livreId;
    private Date dateEmprunt;
    private Date dateRetourPrevue;
    private Date dateRetourEffective;
    
    // Constante pour le calcul des pénalités
    private static final double PENALITE_PAR_JOUR = 100.0; // 100 F CFA par jour
    
    // Format pour l'affichage des dates
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Constructeur pour créer un nouvel emprunt (sans ID ni date de retour effective).
     */
    public Emprunt(int membreId, int livreId, Date dateEmprunt, Date dateRetourPrevue) {
        this.membreId = membreId;
        this.livreId = livreId;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = null; // Pas encore retourné
    }
    
    /**
     * Constructeur complet (pour récupération depuis la base de données).
     */
    public Emprunt(int idEmprunt, int membreId, int livreId, Date dateEmprunt, 
                   Date dateRetourPrevue, Date dateRetourEffective) {
        this.idEmprunt = idEmprunt;
        this.membreId = membreId;
        this.livreId = livreId;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = dateRetourEffective;
    }
    
    // Getters et Setters (Encapsulation)
    public int getIdEmprunt() { 
        return idEmprunt; 
    }
    
    public void setIdEmprunt(int idEmprunt) { 
        this.idEmprunt = idEmprunt; 
    }
    
    public int getMembreId() { 
        return membreId; 
    }
    
    public void setMembreId(int membreId) { 
        this.membreId = membreId; 
    }
    
    public int getLivreId() { 
        return livreId; 
    }
    
    public void setLivreId(int livreId) { 
        this.livreId = livreId; 
    }
    
    public Date getDateEmprunt() { 
        return dateEmprunt; 
    }
    
    public void setDateEmprunt(Date dateEmprunt) { 
        this.dateEmprunt = dateEmprunt; 
    }
    
    public Date getDateRetourPrevue() { 
        return dateRetourPrevue; 
    }
    
    public void setDateRetourPrevue(Date dateRetourPrevue) { 
        this.dateRetourPrevue = dateRetourPrevue; 
    }
    
    public Date getDateRetourEffective() { 
        return dateRetourEffective; 
    }
    
    public void setDateRetourEffective(Date dateRetourEffective) { 
        this.dateRetourEffective = dateRetourEffective; 
    }
    
    /**
     * Calcule la pénalité en cas de retard.
     * Formule: nombre_jours_retard × PENALITE_PAR_JOUR
     * 
     * @return montant de la pénalité en F CFA
     */
    public double calculerPenalite() {
        // Si pas encore retourné, utiliser la date actuelle
        Date dateRetour = (dateRetourEffective != null) ? dateRetourEffective : new Date();
        
        // Vérifier s'il y a un retard
        if (dateRetour.after(dateRetourPrevue)) {
            // Calculer la différence en millisecondes
            long diffMillis = dateRetour.getTime() - dateRetourPrevue.getTime();
            
            // Convertir en jours
            long joursRetard = TimeUnit.MILLISECONDS.toDays(diffMillis);
            
            // Si la différence est de 0 jours mais qu'il y a un retard (même minime)
            // on compte quand même 1 jour
            if (joursRetard == 0 && diffMillis > 0) {
                joursRetard = 1;
            }
            
            // Calculer la pénalité
            return joursRetard * PENALITE_PAR_JOUR;
        }
        
        // Pas de retard, pas de pénalité
        return 0.0;
    }
    
    /**
     * Vérifie si l'emprunt est en retard.
     */
    public boolean estEnRetard() {
        if (dateRetourEffective != null) {
            // Le livre a été retourné, vérifier si c'était en retard
            return dateRetourEffective.after(dateRetourPrevue);
        } else {
            // Le livre n'est pas encore retourné, vérifier si la date prévue est dépassée
            return new Date().after(dateRetourPrevue);
        }
    }
    
    /**
     * Calcule le nombre de jours de retard.
     */
    public long getJoursRetard() {
        Date dateRetour = (dateRetourEffective != null) ? dateRetourEffective : new Date();
        
        if (dateRetour.after(dateRetourPrevue)) {
            long diffMillis = dateRetour.getTime() - dateRetourPrevue.getTime();
            return TimeUnit.MILLISECONDS.toDays(diffMillis);
        }
        
        return 0;
    }
    
    /**
     * Affiche les détails de l'emprunt de manière formatée.
     * Implémentation du Polymorphisme.
     */
    public void afficherDetails() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         DÉTAILS DE L'EMPRUNT           ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ ID Emprunt       : " + String.format("%-19d", idEmprunt) + "║");
        System.out.println("║ ID Membre        : " + String.format("%-19d", membreId) + "║");
        System.out.println("║ ID Livre         : " + String.format("%-19d", livreId) + "║");
        System.out.println("║ Date emprunt     : " + String.format("%-19s", dateFormat.format(dateEmprunt)) + "║");
        System.out.println("║ Retour prévu     : " + String.format("%-19s", dateFormat.format(dateRetourPrevue)) + "║");
        
        if (dateRetourEffective != null) {
            System.out.println("║ Retour effectif  : " + String.format("%-19s", dateFormat.format(dateRetourEffective)) + "║");
        } else {
            System.out.println("║ Retour effectif  : Non retourné        ║");
        }
        
        double penalite = calculerPenalite();
        System.out.println("║ Pénalité         : " + String.format("%-12.2f F CFA", penalite) + "║");
        
        if (estEnRetard()) {
            System.out.println("║ ⚠️  STATUT        : EN RETARD          ║");
            System.out.println("║ Jours de retard  : " + String.format("%-19d", getJoursRetard()) + "║");
        } else {
            System.out.println("║ ✓ STATUT         : À JOUR              ║");
        }
        
        System.out.println("╚════════════════════════════════════════╝");
    }
    
    @Override
    public String toString() {
        return "Emprunt{" +
                "idEmprunt=" + idEmprunt +
                ", membreId=" + membreId +
                ", livreId=" + livreId +
                ", dateEmprunt=" + dateFormat.format(dateEmprunt) +
                ", dateRetourPrevue=" + dateFormat.format(dateRetourPrevue) +
                ", dateRetourEffective=" + (dateRetourEffective != null ? dateFormat.format(dateRetourEffective) : "Non retourné") +
                ", penalite=" + calculerPenalite() + " F CFA" +
                '}';
    }
}