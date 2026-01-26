package com.bibliotheque.dao;

import com.bibliotheque.model.Membre;
import com.bibliotheque.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object pour la gestion des membres.
 * Fournit toutes les opérations CRUD pour les membres de la bibliothèque.
 * 
 * @author Votre Nom
 * @version 1.0
 */
public class MembreDAO {
    
    /**
     * Ajoute un nouveau membre dans la base de données.
     * 
     * @param membre Le membre à inscrire
     * @return true si l'inscription a réussi, false sinon
     */
    public boolean ajouterMembre(Membre membre) {
        String sql = "INSERT INTO membres (nom, prenom, email, adhesion_date) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, membre.getNom());
            pstmt.setString(2, membre.getPrenom());
            pstmt.setString(3, membre.getEmail());
            pstmt.setDate(4, new java.sql.Date(membre.getAdhesionDate().getTime()));
            
            int rowsAffected = pstmt.executeUpdate();
            
            // Récupérer l'ID généré
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        membre.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de l'ajout du membre: " + e.getMessage());
            if (e.getMessage().contains("unique") || e.getMessage().contains("duplicate")) {
                System.err.println("  Cet email est déjà utilisé par un autre membre.");
            }
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Supprime un membre de la base de données.
     * Attention: supprimera aussi tous les emprunts associés (cascade).
     * 
     * @param id L'ID du membre à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerMembre(int id) {
        String sql = "DELETE FROM membres WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la suppression du membre: " + e.getMessage());
            System.err.println("  Vérifiez qu'aucun emprunt actif n'est lié à ce membre.");
            return false;
        }
    }
    
    /**
     * Recherche des membres par nom (recherche partielle, insensible à la casse).
     * 
     * @param nom Le nom ou partie du nom à rechercher
     * @return Liste des membres trouvés
     */
    public List<Membre> rechercherParNom(String nom) {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membres WHERE LOWER(nom) LIKE LOWER(?) OR LOWER(prenom) LIKE LOWER(?) ORDER BY nom, prenom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + nom + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                membres.add(extractMembreFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la recherche de membres: " + e.getMessage());
            e.printStackTrace();
        }
        
        return membres;
    }
    
    /**
     * Recherche un membre par son email.
     * 
     * @param email L'email du membre
     * @return Le membre trouvé ou null
     */
    public Membre rechercherParEmail(String email) {
        String sql = "SELECT * FROM membres WHERE LOWER(email) = LOWER(?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractMembreFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la recherche par email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Récupère un membre par son ID.
     * 
     * @param id L'ID du membre
     * @return Le membre trouvé ou null
     */
    public Membre getMembreById(int id) {
        String sql = "SELECT * FROM membres WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractMembreFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la récupération du membre: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Récupère tous les membres inscrits.
     * 
     * @return Liste de tous les membres
     */
    public List<Membre> afficherTousMembres() {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membres ORDER BY nom, prenom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                membres.add(extractMembreFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la récupération de tous les membres: " + e.getMessage());
            e.printStackTrace();
        }
        
        return membres;
    }
    
    /**
     * Méthode utilitaire pour extraire un objet Membre depuis un ResultSet.
     * 
     * @param rs Le ResultSet contenant les données du membre
     * @return Un objet Membre
     * @throws SQLException en cas d'erreur d'accès aux données
     */
    private Membre extractMembreFromResultSet(ResultSet rs) throws SQLException {
        return new Membre(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("email"),
            rs.getDate("adhesion_date")
        );
    }
}