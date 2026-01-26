package com.bibliotheque;

import com.bibliotheque.dao.*;
import com.bibliotheque.model.*;
import com.bibliotheque.util.DatabaseConnection;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Application principale de gestion de bibliothèque.
 * Point d'entrée du programme avec menu interactif.
 * 
 * @author Votre Nom
 * @version 1.0
 */
public class BibliothequeApp {
    // Scanner pour la saisie utilisateur
    private static final Scanner scanner = new Scanner(System.in);
    
    // DAOs pour accéder aux données
    private static final LivreDAO livreDAO = new LivreDAO();
    private static final MembreDAO membreDAO = new MembreDAO();
    private static final EmpruntDAO empruntDAO = new EmpruntDAO();
    
    // Format de date
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Point d'entrée principal de l'application.
     */
    public static void main(String[] args) {
        afficherBanniere();
        
        // Tester la connexion à la base de données
        if (!DatabaseConnection.testConnection()) {
            System.err.println("\n✗ Impossible de se connecter à la base de données.");
            System.err.println("  Vérifiez que PostgreSQL est démarré et configuré correctement.");
            System.err.println("  Appuyez sur Entrée pour quitter...");
            scanner.nextLine();
            return;
        }
        
        boolean continuer = true;
        
        while (continuer) {
            afficherMenuPrincipal();
            int choix = lireEntier("\n➤ Votre choix: ");
            System.out.println(); 
            
            switch (choix) {
                case 1:
                    gererLivres();
                    break;
                case 2:
                    gererMembres();
                    break;
                case 3:
                    gererEmprunts();
                    break;
                case 4:
                    rechercherLivres();
                    break;
                case 5:
                    afficherEmpruntsEnRetard();
                    break;
                case 6:
                    afficherStatistiques();
                    break;
                case 0:
                    continuer = false;
                    afficherMessageFin();
                    break;
                default:
                    System.out.println("❌ Choix invalide. Veuillez réessayer.");
            }
            
            if (continuer) {
                System.out.println("\nAppuyez sur Entrée pour continuer...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    /**
     * Affiche la bannière de démarrage.
     */
    private static void afficherBanniere() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                          ║");
        System.out.println("║        📚 SYSTÈME DE GESTION DE BIBLIOTHÈQUE 📚          ║");
        System.out.println("║                                                          ║");
        System.out.println("║              Gestion Complète et Efficace                ║");
        System.out.println("║                  Version 1.0 - 2024                      ║");
        System.out.println("║                                                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    /**
     * Affiche le menu principal.
     */
    private static void afficherMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                     MENU PRINCIPAL                       ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  1. 📖 Gestion des Livres                                ║");
        System.out.println("║  2. 👥 Gestion des Membres                               ║");
        System.out.println("║  3. 📝 Gestion des Emprunts                              ║");
        System.out.println("║  4. 🔍 Rechercher des Livres                             ║");
        System.out.println("║  5. ⚠️  Afficher les Emprunts en Retard                  ║");
        System.out.println("║  6. 📊 Statistiques                                      ║");
        System.out.println("║  0. 🚪 Quitter                                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Menu de gestion des livres.
     */
    private static void gererLivres() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                  GESTION DES LIVRES                      ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  1. ➕ Ajouter un livre                                  ║");
        System.out.println("║  2. ✏️  Modifier un livre                                ║");
        System.out.println("║  3. ❌ Supprimer un livre                                ║");
        System.out.println("║  4. 📚 Afficher tous les livres                          ║");
        System.out.println("║  5. ✅ Afficher les livres disponibles                   ║");
        System.out.println("║  0. ↩️  Retour                                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        
        int choix = lireEntier("\n➤ Votre choix: ");
        System.out.println();
        
        switch (choix) {
            case 1:
                ajouterLivre();
                break;
            case 2:
                modifierLivre();
                break;
            case 3:
                supprimerLivre();
                break;
            case 4:
                afficherTousLesLivres();
                break;
            case 5:
                afficherLivresDisponibles();
                break;
        }
    }
    
    /**
     * Ajoute un nouveau livre.
     */
    private static void ajouterLivre() {
        System.out.println("═══════════ AJOUTER UN LIVRE ═══════════");
        scanner.nextLine(); // Consommer le retour à la ligne
        
        System.out.print("📌 Titre: ");
        String titre = scanner.nextLine().trim();
        
        if (titre.isEmpty()) {
            System.out.println("❌ Le titre ne peut pas être vide.");
            return;
        }
        
        System.out.print("✍️  Auteur: ");
        String auteur = scanner.nextLine().trim();
        
        if (auteur.isEmpty()) {
            System.out.println("❌ L'auteur ne peut pas être vide.");
            return;
        }
        
        System.out.print("📂 Catégorie: ");
        String categorie = scanner.nextLine().trim();
        
        int exemplaires = lireEntier("📊 Nombre d'exemplaires: ");
        
        if (exemplaires < 1) {
            System.out.println("❌ Le nombre d'exemplaires doit être au moins 1.");
            return;
        }
        
        Livre livre = new Livre(titre, auteur, categorie, exemplaires);
        
        if (livreDAO.ajouterLivre(livre)) {
            System.out.println("\n✅ Livre ajouté avec succès!");
            System.out.println("   ID attribué: " + livre.getId());
        } else {
            System.out.println("\n❌ Erreur lors de l'ajout du livre.");
        }
    }
    
    /**
     * Modifie un livre existant.
     */
    private static void modifierLivre() {
        System.out.println("═══════════ MODIFIER UN LIVRE ═══════════");
        
        int id = lireEntier("🔢 ID du livre à modifier: ");
        
        Livre livreExistant = livreDAO.getLivreById(id);
        if (livreExistant == null) {
            System.out.println("❌ Aucun livre trouvé avec l'ID " + id);
            return;
        }
        
        System.out.println("\n📖 Livre actuel:");
        livreExistant.afficherDetails();
        
        scanner.nextLine(); // Consommer
        
        System.out.println("\n📝 Entrez les nouvelles informations (laissez vide pour garder l'ancienne valeur):");
        
        System.out.print("Nouveau titre [" + livreExistant.getTitre() + "]: ");
        String titre = scanner.nextLine().trim();
        if (!titre.isEmpty()) livreExistant.setTitre(titre);
        
        System.out.print("Nouvel auteur [" + livreExistant.getAuteur() + "]: ");
        String auteur = scanner.nextLine().trim();
        if (!auteur.isEmpty()) livreExistant.setAuteur(auteur);
        
        System.out.print("Nouvelle catégorie [" + livreExistant.getCategorie() + "]: ");
        String categorie = scanner.nextLine().trim();
        if (!categorie.isEmpty()) livreExistant.setCategorie(categorie);
        
        System.out.print("Nouveau nombre d'exemplaires [" + livreExistant.getNombreExemplaires() + "]: ");
        String exemplairesStr = scanner.nextLine().trim();
        if (!exemplairesStr.isEmpty()) {
            try {
                int exemplaires = Integer.parseInt(exemplairesStr);
                livreExistant.setNombreExemplaires(exemplaires);
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Nombre invalide, valeur conservée.");
            }
        }
        
        if (livreDAO.modifierLivre(livreExistant)) {
            System.out.println("\n✅ Livre modifié avec succès!");
        } else {
            System.out.println("\n❌ Erreur lors de la modification.");
        }
    }
    
    /**
     * Supprime un livre.
     */
    private static void supprimerLivre() {
        System.out.println("═══════════ SUPPRIMER UN LIVRE ═══════════");
        
        int id = lireEntier("🔢 ID du livre à supprimer: ");
        
        Livre livre = livreDAO.getLivreById(id);
        if (livre == null) {
            System.out.println("❌ Aucun livre trouvé avec l'ID " + id);
            return;
        }
        
        livre.afficherDetails();
        
        scanner.nextLine(); // Consommer
        System.out.print("\n⚠️  Êtes-vous sûr de vouloir supprimer ce livre? (oui/non): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("oui")) {
            if (livreDAO.supprimerLivre(id)) {
                System.out.println("\n✅ Livre supprimé avec succès!");
            } else {
                System.out.println("\n❌ Erreur lors de la suppression.");
            }
        } else {
            System.out.println("❌ Suppression annulée.");
        }
    }
    /**
     * Affiche tous les livres.
     */
    private static void afficherTousLesLivres() {
        System.out.println("═══════════ TOUS LES LIVRES ═══════════");
        List<Livre> livres = livreDAO.afficherTousLivres();
        
        if (livres.isEmpty()) {
            System.out.println("📭 Aucun livre dans la bibliothèque.");
        } else {
            System.out.println("📚 Nombre total de livres: " + livres.size());
            for (Livre livre : livres) {
                livre.afficherDetails();
            }
        }
    }
    
    /**
     * Affiche les livres disponibles.
     */
    private static void afficherLivresDisponibles() {
        System.out.println("═══════════ LIVRES DISPONIBLES ═══════════");
        List<Livre> livres = livreDAO.afficherLivresDisponibles();
        
        if (livres.isEmpty()) {
            System.out.println("📭 Aucun livre disponible actuellement.");
        } else {
            System.out.println("✅ Nombre de livres disponibles: " + livres.size());
            for (Livre livre : livres) {
                livre.afficherDetails();
            }
        }
    }
    
    /**
     * Menu de gestion des membres.
     */
    private static void gererMembres() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                 GESTION DES MEMBRES                      ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  1. ➕ Inscrire un nouveau membre                        ║");
        System.out.println("║  2. ❌ Supprimer un membre                               ║");
        System.out.println("║  3. 🔍 Rechercher un membre                              ║");
        System.out.println("║  4. 👥 Afficher tous les membres                         ║");
        System.out.println("║  0. ↩️  Retour                                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        
        int choix = lireEntier("\n➤ Votre choix: ");
        System.out.println();
        
        switch (choix) {
            case 1:
                ajouterMembre();
                break;
            case 2:
                supprimerMembre();
                break;
            case 3:
                rechercherMembre();
                break;
            case 4:
                afficherTousLesMembres();
                break;
        }
    }
    
    /**
     * Inscrit un nouveau membre.
     */
    private static void ajouterMembre() {
        System.out.println("═══════════ INSCRIRE UN MEMBRE ═══════════");
        scanner.nextLine(); // Consommer
        
        System.out.print("📌 Nom: ");
        String nom = scanner.nextLine().trim();
        
        if (nom.isEmpty()) {
            System.out.println("❌ Le nom ne peut pas être vide.");
            return;
        }
        
        System.out.print("📌 Prénom: ");
        String prenom = scanner.nextLine().trim();
        
        if (prenom.isEmpty()) {
            System.out.println("❌ Le prénom ne peut pas être vide.");
            return;
        }
        
        System.out.print("📧 Email: ");
        String email = scanner.nextLine().trim();
        
        if (email.isEmpty() || !email.contains("@")) {
            System.out.println("❌ L'email est invalide.");
            return;
        }
        
        // Vérifier si l'email existe déjà
        if (membreDAO.rechercherParEmail(email) != null) {
            System.out.println("❌ Cet email est déjà utilisé par un autre membre.");
            return;
        }
        
        Membre membre = new Membre(nom, prenom, email);
        
        if (membreDAO.ajouterMembre(membre)) {
            System.out.println("\n✅ Membre inscrit avec succès!");
            System.out.println("   ID attribué: " + membre.getId());
            System.out.println("   Date d'adhésion: " + dateFormat.format(membre.getAdhesionDate()));
        } else {
            System.out.println("\n❌ Erreur lors de l'inscription.");
        }
    }
    
    /**
     * Supprime un membre.
     */
    private static void supprimerMembre() {
        System.out.println("═══════════ SUPPRIMER UN MEMBRE ═══════════");
        
        int id = lireEntier("🔢 ID du membre à supprimer: ");
        
        Membre membre = membreDAO.getMembreById(id);
        if (membre == null) {
            System.out.println("❌ Aucun membre trouvé avec l'ID " + id);
            return;
        }
        
        membre.afficherDetails();
        
        // Vérifier si le membre a des emprunts en cours
        List<Emprunt> empruntsEnCours = empruntDAO.getEmpruntsByMembre(id);
        long empruntsActifs = empruntsEnCours.stream()
            .filter(e -> e.getDateRetourEffective() == null)
            .count();
        
        if (empruntsActifs > 0) {
            System.out.println("\n⚠️  ATTENTION: Ce membre a " + empruntsActifs + " emprunt(s) en cours.");
            System.out.println("   Il faut d'abord retourner tous les livres avant de supprimer le membre.");
            return;
        }
        
        scanner.nextLine(); // Consommer
        System.out.print("\n⚠️  Êtes-vous sûr de vouloir supprimer ce membre? (oui/non): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("oui")) {
            if (membreDAO.supprimerMembre(id)) {
                System.out.println("\n✅ Membre supprimé avec succès!");
            } else {
                System.out.println("\n❌ Erreur lors de la suppression.");
            }
        } else {
            System.out.println("❌ Suppression annulée.");
        }
    }
    
    /**
     * Recherche un membre par nom.
     */
    private static void rechercherMembre() {
        System.out.println("═══════════ RECHERCHER UN MEMBRE ═══════════");
        scanner.nextLine(); // Consommer
        
        System.out.print("🔍 Nom ou prénom à rechercher: ");
        String nom = scanner.nextLine().trim();
        
        List<Membre> membres = membreDAO.rechercherParNom(nom);
        
        if (membres.isEmpty()) {
            System.out.println("❌ Aucun membre trouvé avec ce nom.");
        } else {
            System.out.println("\n✅ " + membres.size() + " membre(s) trouvé(s):");
            for (Membre membre : membres) {
                membre.afficherDetails();
            }
        }
    }
    
    /**
     * Affiche tous les membres.
     */
    private static void afficherTousLesMembres() {
        System.out.println("═══════════ TOUS LES MEMBRES ═══════════");
        List<Membre> membres = membreDAO.afficherTousMembres();
        
        if (membres.isEmpty()) {
            System.out.println("📭 Aucun membre inscrit.");
        } else {
            System.out.println("👥 Nombre total de membres: " + membres.size());
            for (Membre membre : membres) {
                membre.afficherDetails();
            }
        }
    }
    
    /**
     * Menu de gestion des emprunts.
     */
    private static void gererEmprunts() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                GESTION DES EMPRUNTS                      ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  1. ➕ Enregistrer un emprunt                            ║");
        System.out.println("║  2. ↩️  Enregistrer un retour                            ║");
        System.out.println("║  3. 📋 Afficher les emprunts en cours                    ║");
        System.out.println("║  4. 📜 Historique complet des emprunts                   ║");
        System.out.println("║  0. ↩️  Retour                                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        
        int choix = lireEntier("\n➤ Votre choix: ");
        System.out.println();
        
        switch (choix) {
            case 1:
                enregistrerEmprunt();
                break;
            case 2:
                enregistrerRetour();
                break;
            case 3:
                afficherEmpruntsEnCours();
                break;
            case 4:
                afficherHistoriqueEmprunts();
                break;
        }
    }
    
    /**
     * Enregistre un nouvel emprunt.
     */
    private static void enregistrerEmprunt() {
        System.out.println("═══════════ NOUVEL EMPRUNT ═══════════");
        
        int membreId = lireEntier("🔢 ID du membre: ");
        Membre membre = membreDAO.getMembreById(membreId);
        
        if (membre == null) {
            System.out.println("❌ Aucun membre trouvé avec l'ID " + membreId);
            return;
        }
        
        System.out.println("\n👤 Membre: " + membre.getNomComplet());
        
        int livreId = lireEntier("🔢 ID du livre: ");
        Livre livre = livreDAO.getLivreById(livreId);
        
        if (livre == null) {
            System.out.println("❌ Aucun livre trouvé avec l'ID " + livreId);
            return;
        }
        
        System.out.println("📖 Livre: " + livre.getTitre());
        System.out.println("✍️  Auteur: " + livre.getAuteur());
        System.out.println("📊 Exemplaires disponibles: " + livre.getNombreExemplaires());
        
        if (!livre.estDisponible()) {
            System.out.println("\n❌ Ce livre n'est pas disponible actuellement.");
            return;
        }
        
        // Dates
        Date dateEmprunt = new Date(); // Aujourd'hui
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateEmprunt);
        cal.add(Calendar.DAY_OF_MONTH, 14); // +14 jours
        Date dateRetourPrevue = cal.getTime();
        
        Emprunt emprunt = new Emprunt(membreId, livreId, dateEmprunt, dateRetourPrevue);
        
        if (empruntDAO.enregistrerEmprunt(emprunt)) {
            System.out.println("\n✅ Emprunt enregistré avec succès!");
            System.out.println("   ID de l'emprunt: " + emprunt.getIdEmprunt());
            System.out.println("   Date d'emprunt: " + dateFormat.format(dateEmprunt));
            System.out.println("   Date de retour prévue: " + dateFormat.format(dateRetourPrevue));
            System.out.println("\n⚠️  Rappel: Pénalité de 100 F CFA par jour de retard.");
        } else {
            System.out.println("\n❌ Erreur lors de l'enregistrement de l'emprunt.");
        }
    }
    
    /**
     * Enregistre le retour d'un livre.
     */
    private static void enregistrerRetour() {
        System.out.println("═══════════ RETOUR DE LIVRE ═══════════");
        
        int idEmprunt = lireEntier("🔢 ID de l'emprunt: ");
        
        Emprunt emprunt = empruntDAO.getEmpruntById(idEmprunt);
        if (emprunt == null) {
            System.out.println("❌ Aucun emprunt trouvé avec l'ID " + idEmprunt);
            return;
        }
        
        System.out.println("\n📋 Détails de l'emprunt:");
        emprunt.afficherDetails();
        
        if (emprunt.getDateRetourEffective() != null) {
            System.out.println("\n❌ Ce livre a déjà été retourné.");
            return;
        }
        
        Date dateRetour = new Date(); // Aujourd'hui
        
        if (empruntDAO.enregistrerRetour(idEmprunt, dateRetour)) {
            System.out.println("\n✅ Retour enregistré avec succès!");
        } else {
            System.out.println("\n❌ Erreur lors de l'enregistrement du retour.");
        }
    }
    
    /**
     * Affiche les emprunts en cours.
     */
    private static void afficherEmpruntsEnCours() {
        System.out.println("═══════════ EMPRUNTS EN COURS ═══════════");
        List<Emprunt> emprunts = empruntDAO.getEmpruntsEnCours();
        
        if (emprunts.isEmpty()) {
            System.out.println("✅ Aucun emprunt en cours.");
        } else {
            System.out.println("📋 Nombre d'emprunts en cours: " + emprunts.size());
            for (Emprunt emprunt : emprunts) {
                emprunt.afficherDetails();
                
                // Afficher les infos du membre et du livre
                Membre membre = membreDAO.getMembreById(emprunt.getMembreId());
                Livre livre = livreDAO.getLivreById(emprunt.getLivreId());
                
                if (membre != null && livre != null) {
                    System.out.println("   👤 Membre: " + membre.getNomComplet());
                    System.out.println("   📖 Livre: " + livre.getTitre());
                }
                System.out.println();
            }
        }
    }
    
    /**
     * Affiche l'historique complet des emprunts.
     */
    private static void afficherHistoriqueEmprunts() {
        System.out.println("═══════════ HISTORIQUE DES EMPRUNTS ═══════════");
        List<Emprunt> emprunts = empruntDAO.getHistoriqueEmprunts();
        
        if (emprunts.isEmpty()) {
            System.out.println("📭 Aucun emprunt enregistré.");
        } else {
            System.out.println("📜 Nombre total d'emprunts: " + emprunts.size());
            
            long empruntsActifs = emprunts.stream()
                .filter(e -> e.getDateRetourEffective() == null)
                .count();
            long empruntsTermines = emprunts.size() - empruntsActifs;
            
            System.out.println("   ✅ Terminés: " + empruntsTermines);
            System.out.println("   📋 En cours: " + empruntsActifs);
            System.out.println();
            
            for (Emprunt emprunt : emprunts) {
                emprunt.afficherDetails();
                
                Membre membre = membreDAO.getMembreById(emprunt.getMembreId());
                Livre livre = livreDAO.getLivreById(emprunt.getLivreId());
                
                if (membre != null && livre != null) {
                    System.out.println("   👤 Membre: " + membre.getNomComplet());
                    System.out.println("   📖 Livre: " + livre.getTitre());
                }
                System.out.println();
            }
        }
    }
    
    /**
     * Recherche des livres par différents critères.
     */
    private static void rechercherLivres() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                RECHERCHE DE LIVRES                       ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  1. 🔍 Rechercher par titre                              ║");
        System.out.println("║  2. ✍️  Rechercher par auteur                            ║");
        System.out.println("║  3. 📂 Rechercher par catégorie                          ║");
        System.out.println("║  0. ↩️  Retour                                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        
        int choix = lireEntier("\n➤ Votre choix: ");
        scanner.nextLine(); // Consommer
        System.out.println();
        
        List<Livre> livres = new ArrayList<>();
        
        switch (choix) {
            case 1:
                System.out.print("🔍 Titre à rechercher: ");
                String titre = scanner.nextLine().trim();
                livres = livreDAO.rechercherParTitre(titre);
                break;
            case 2:
                System.out.print("🔍 Auteur à rechercher: ");
                String auteur = scanner.nextLine().trim();
                livres = livreDAO.rechercherParAuteur(auteur);
                break;
            case 3:
                System.out.print("🔍 Catégorie à rechercher: ");
                String categorie = scanner.nextLine().trim();
                livres = livreDAO.rechercherParCategorie(categorie);
                break;
            default:
                return;
        }
        
        if (livres.isEmpty()) {
            System.out.println("\n❌ Aucun livre trouvé.");
        } else {
            System.out.println("\n✅ " + livres.size() + " livre(s) trouvé(s):");
            for (Livre livre : livres) {
                livre.afficherDetails();
            }
        }
    }
    
    /**
     * Affiche les emprunts en retard.
     */
    private static void afficherEmpruntsEnRetard() {
        System.out.println("═══════════ ⚠️  EMPRUNTS EN RETARD ═══════════");
        List<Emprunt> emprunts = empruntDAO.getEmpruntsEnRetard();
        
        if (emprunts.isEmpty()) {
            System.out.println("✅ Aucun emprunt en retard. Excellent!");
        } else {
            System.out.println("⚠️  " + emprunts.size() + " emprunt(s) en retard:");
            
            double penaliteTotale = 0;
            
            for (Emprunt emprunt : emprunts) {
                emprunt.afficherDetails();
                
                Membre membre = membreDAO.getMembreById(emprunt.getMembreId());
                Livre livre = livreDAO.getLivreById(emprunt.getLivreId());
                
                if (membre != null && livre != null) {
                    System.out.println("   👤 Membre: " + membre.getNomComplet());
                    System.out.println("   📧 Email: " + membre.getEmail());
                    System.out.println("   📖 Livre: " + livre.getTitre());
                }
                
                penaliteTotale += emprunt.calculerPenalite();
                System.out.println();
            }
            
            System.out.println("💰 Pénalités totales: " + penaliteTotale + " F CFA");
        }
    }
    
    /**
     * Affiche des statistiques sur la bibliothèque.
     */
    private static void afficherStatistiques() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                     STATISTIQUES                         ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        
        // Livres
        List<Livre> livres = livreDAO.afficherTousLivres();
        List<Livre> livresDisponibles = livreDAO.afficherLivresDisponibles();
        int totalExemplaires = livres.stream()
            .mapToInt(Livre::getNombreExemplaires)
            .sum();
        
        System.out.println("║  📚 LIVRES                                               ║");
        System.out.println("║     Nombre de titres: " + String.format("%-33d", livres.size()) + "║");
        System.out.println("║     Total d'exemplaires: " + String.format("%-30d", totalExemplaires) + "║");
        System.out.println("║     Livres disponibles: " + String.format("%-31d", livresDisponibles.size()) + "║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        
        // Membres
        List<Membre> membres = membreDAO.afficherTousMembres();
        System.out.println("║  👥 MEMBRES                                              ║");
        System.out.println("║     Nombre de membres inscrits: " + String.format("%-25d", membres.size()) + "║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        
        // Emprunts
        List<Emprunt> empruntsEnCours = empruntDAO.getEmpruntsEnCours();
        List<Emprunt> empruntsEnRetard = empruntDAO.getEmpruntsEnRetard();
        List<Emprunt> historique = empruntDAO.getHistoriqueEmprunts();
        
        System.out.println("║  📋 EMPRUNTS                                             ║");
        System.out.println("║     Total des emprunts: " + String.format("%-31d", historique.size()) + "║");
        System.out.println("║     Emprunts en cours: " + String.format("%-32d", empruntsEnCours.size()) + "║");
        System.out.println("║     Emprunts en retard: " + String.format("%-31d", empruntsEnRetard.size()) + "║");
        
        // Pénalités
        double penalitesTotales = empruntsEnRetard.stream()
            .mapToDouble(Emprunt::calculerPenalite)
            .sum();
        
        System.out.println("║     Pénalités à percevoir: " + String.format("%-24.2f F CFA", penalitesTotales) + "║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Affiche le message de fin.
     */
    private static void afficherMessageFin() {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                          ║");
        System.out.println("║            Merci d'avoir utilisé notre système           ║");
        System.out.println("║                   À bientôt! 👋                          ║");
        System.out.println("║                                                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Méthode utilitaire pour lire un entier avec gestion d'erreur.
     * 
     * @param message Le message à afficher
     * @return L'entier saisi
     */
    private static int lireEntier(String message) {
        while (true) {
            try {
                System.out.print(message);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un nombre valide.");
            }
        }
    }
}