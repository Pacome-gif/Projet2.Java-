package com.bibliotheque.dao;

import com.bibliotheque.model.Emprunt;
import com.bibliotheque.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object pour la gestion des emprunts.
 * Gère les emprunts, retours et calculs de pénalités.
 * 
 * @author Votre Nom
 * @version 1.0
 */
public class EmpruntDAO {
    
    /**
     * Enregistre un nouvel emprunt dans la base de données.
     * Décrémente automatiquement le nombre d'exemplaires disponibles du livre.
     * 
     * @param emprunt L'emprunt à enregistrer
     * @return true si l'enregistrement a réussi, false sinon
     */
    public boolean enregistrerEmprunt(Emprunt emprunt) {
        // Vérifier d'abord si le livre est disponible
        if (!verifierDisponibiliteLivre(emprunt.getLivreId())) {
            System.err.println("✗ Ce livre n'est pas disponible pour l'emprunt.");
            return false;
        }
        
        String sqlEmprunt = "INSERT INTO emprunts (membre_id, livre_id, date_emprunt, date_retour_prevue) VALUES (?, ?, ?, ?)";
        String sqlUpdate = "UPDATE livres SET nombre_exemplaires = nombre_exemplaires - 1 WHERE id = ?";
        
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Démarrer une transaction
            
            // 1. Insérer l'emprunt
            try (PreparedStatement pstmtEmprunt = conn.prepareStatement(sqlEmprunt, Statement.RETURN_GENERATED_KEYS)) {
                pstmtEmprunt.setInt(1, emprunt.getMembreId());
                pstmtEmprunt.setInt(2, emprunt.getLivreId());
                pstmtEmprunt.setDate(3, new java.sql.Date(emprunt.getDateEmprunt().getTime()));
                pstmtEmprunt.setDate(4, new java.sql.Date(emprunt.getDateRetourPrevue().getTime()));
                
                int rowsAffected = pstmtEmprunt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Récupérer l'ID généré
                    try (ResultSet generatedKeys = pstmtEmprunt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            emprunt.setIdEmprunt(generatedKeys.getInt(1));
                        }
                    }
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
            // 2. Décrémenter le nombre d'exemplaires
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setInt(1, emprunt.getLivreId());
                pstmtUpdate.executeUpdate();
            }
            
            conn.commit(); // Valider la transaction
            return true;
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de l'enregistrement de l'emprunt: " + e.getMessage());
            e.printStackTrace();
            
            // En cas d'erreur, annuler la transaction
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Enregistre le retour d'un livre.
     * Incrémente automatiquement le nombre d'exemplaires disponibles et calcule la pénalité.
     * 
     * @param idEmprunt L'ID de l'emprunt
     * @param dateRetour La date de retour effective
     * @return true si l'enregistrement a réussi, false sinon
     */
    public boolean enregistrerRetour(int idEmprunt, Date dateRetour) {
        // Récupérer l'emprunt pour calculer la pénalité
        Emprunt emprunt = getEmpruntById(idEmprunt);
        if (emprunt == null) {
            System.err.println("✗ Emprunt non trouvé avec l'ID: " + idEmprunt);
            return false;
        }
        
        if (emprunt.getDateRetourEffective() != null) {
            System.err.println("✗ Ce livre a déjà été retourné le " + emprunt.getDateRetourEffective());
            return false;
        }
        
        emprunt.setDateRetourEffective(dateRetour);
        double penalite = emprunt.calculerPenalite();
        
        String sqlRetour = "UPDATE emprunts SET date_retour_effective=?, penalite=? WHERE id_emprunt=?";
        String sqlUpdate = "UPDATE livres SET nombre_exemplaires = nombre_exemplaires + 1 WHERE id = ?";
        
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Transaction
            
            // 1. Mettre à jour l'emprunt
            try (PreparedStatement pstmtRetour = conn.prepareStatement(sqlRetour)) {
                pstmtRetour.setDate(1, new java.sql.Date(dateRetour.getTime()));
                pstmtRetour.setDouble(2, penalite);
                pstmtRetour.setInt(3, idEmprunt);
                
                int rowsAffected = pstmtRetour.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // 2. Incrémenter le nombre d'exemplaires
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setInt(1, emprunt.getLivreId());
                pstmtUpdate.executeUpdate();
            }
            
            conn.commit(); // Valider la transaction
            
            // Afficher le résultat
            if (penalite > 0) {
                System.out.println("\n⚠️  ATTENTION: Retard détecté!");
                System.out.println("   Pénalité à payer: " + penalite + " F CFA");
                System.out.println("   Jours de retard: " + emprunt.getJoursRetard());
            } else {
                System.out.println("\n✓ Retour effectué à temps, pas de pénalité.");
            }
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de l'enregistrement du retour: " + e.getMessage());
            e.printStackTrace();
            
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Récupère tous les emprunts en cours (non retournés).
     * 
     * @return Liste des emprunts en cours
     */
    public List<Emprunt> getEmpruntsEnCours() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE date_retour_effective IS NULL ORDER BY date_retour_prevue";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                emprunts.add(extractEmpruntFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la récupération des emprunts en cours: " + e.getMessage());
            e.printStackTrace();
        }
        
        return emprunts;
    }
    
    /**
     * Récupère tous les emprunts en retard (non retournés et date dépassée).
     * 
     * @return Liste des emprunts en retard
     */
    public List<Emprunt> getEmpruntsEnRetard() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE date_retour_effective IS NULL AND date_retour_prevue < CURRENT_DATE ORDER BY date_retour_prevue";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                emprunts.add(extractEmpruntFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la récupération des emprunts en retard: " + e.getMessage());
            e.printStackTrace();
        }
        
        return emprunts;
    }
    
    /**
     * Récupère l'historique complet des emprunts.
     * 
     * @return Liste de tous les emprunts (en cours et terminés)
     */
    public List<Emprunt> getHistoriqueEmprunts() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts ORDER BY date_emprunt DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                emprunts.add(extractEmpruntFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la récupération de l'historique: " + e.getMessage());
            e.printStackTrace();
        }
        
        return emprunts;
    }
    
    /**
     * Récupère les emprunts d'un membre spécifique.
     * 
     * @param membreId L'ID du membre
     * @return Liste des emprunts du membre
     */
    public List<Emprunt> getEmpruntsByMembre(int membreId) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE membre_id=? ORDER BY date_emprunt DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, membreId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                emprunts.add(extractEmpruntFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la récupération des emprunts du membre: " + e.getMessage());
            e.printStackTrace();
        }
        
        return emprunts;
    }
    
    /**
     * Récupère un emprunt par son ID.
     * 
     * @param id L'ID de l'emprunt
     * @return L'emprunt trouvé ou null
     */
    public Emprunt getEmpruntById(int id) {
        String sql = "SELECT * FROM emprunts WHERE id_emprunt=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractEmpruntFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la récupération de l'emprunt: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Vérifie si un livre est disponible pour emprunt.
     * 
     * @param livreId L'ID du livre
     * @return true si disponible, false sinon
     */
    private boolean verifierDisponibiliteLivre(int livreId) {
        String sql = "SELECT nombre_exemplaires FROM livres WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, livreId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("nombre_exemplaires") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la vérification de disponibilité: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Méthode utilitaire pour extraire un Emprunt depuis un ResultSet.
     * 
     * @param rs Le ResultSet
     * @return Un objet Emprunt
     * @throws SQLException en cas d'erreur
     */
    private Emprunt extractEmpruntFromResultSet(ResultSet rs) throws SQLException {
        Date dateRetourEffective = rs.getDate("date_retour_effective");
        
        return new Emprunt(
            rs.getInt("id_emprunt"),
            rs.getInt("membre_id"),
            rs.getInt("livre_id"),
            rs.getDate("date_emprunt"),
            rs.getDate("date_retour_prevue"),
            dateRetourEffective
        );
    }
}