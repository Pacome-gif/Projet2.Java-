package com.bibliotheque.dao;

import com.bibliotheque.model.Livre;
import com.bibliotheque.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object pour la gestion des livres.
 * Fournit toutes les opérations CRUD (Create, Read, Update, Delete) pour les livres.
 * Pattern DAO pour séparer la logique métier de l'accès aux données.
 * 
 * @author Votre Nom
 * @version 1.0
 */
public class LivreDAO {
    
    /**
     * Ajoute un nouveau livre dans la base de données.
     * 
     * @param livre Le livre à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouterLivre(Livre livre) {
        String sql = "INSERT INTO livres (titre, auteur, categorie, nombre_exemplaires) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Définir les paramètres de la requête
            pstmt.setString(1, livre.getTitre());
            pstmt.setString(2, livre.getAuteur());
            pstmt.setString(3, livre.getCategorie());
            pstmt.setInt(4, livre.getNombreExemplaires());
            
            // Exécuter la requête
            int rowsAffected = pstmt.executeUpdate();
            
            // Récupérer l'ID généré automatiquement
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        livre.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de l'ajout du livre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Modifie un livre existant dans la base de données.
     * 
     * @param livre Le livre avec les nouvelles informations
     * @return true si la modification a réussi, false sinon
     */
    public boolean modifierLivre(Livre livre) {
        String sql = "UPDATE livres SET titre=?, auteur=?, categorie=?, nombre_exemplaires=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, livre.getTitre());
            pstmt.setString(2, livre.getAuteur());
            pstmt.setString(3, livre.getCategorie());
            pstmt.setInt(4, livre.getNombreExemplaires());
            pstmt.setInt(5, livre.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la modification du livre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Supprime un livre de la base de données par son ID.
     * Attention: cela supprimera aussi tous les emprunts associés (cascade).
     * 
     * @param id L'ID du livre à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerLivre(int id) {
        String sql = "DELETE FROM livres WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la suppression du livre: " + e.getMessage());
            System.err.println("  Vérifiez qu'aucun emprunt actif n'est lié à ce livre.");
            return false;
        }
    }
    
    /**
     * Recherche des livres par titre (recherche partielle, insensible à la casse).
     * 
     * @param titre Le titre ou partie du titre à rechercher
     * @return Liste des livres trouvés
     */
    public List<Livre> rechercherParTitre(String titre) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres WHERE LOWER(titre) LIKE LOWER(?) ORDER BY titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + titre + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                livres.add(extractLivreFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la recherche par titre: " + e.getMessage());
            e.printStackTrace();
        }
        
        return livres;
    }
    
    /**
     * Recherche des livres par auteur (recherche partielle, insensible à la casse).
     * 
     * @param auteur Le nom de l'auteur ou partie du nom
     * @return Liste des livres trouvés
     */
    public List<Livre> rechercherParAuteur(String auteur) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres WHERE LOWER(auteur) LIKE LOWER(?) ORDER BY auteur, titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + auteur + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                livres.add(extractLivreFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la recherche par auteur: " + e.getMessage());
            e.printStackTrace();
        }
        
        return livres;
    }
    
    /**
     * Recherche des livres par catégorie.
     * 
     * @param categorie La catégorie à rechercher
     * @return Liste des livres de cette catégorie
     */
    public List<Livre> rechercherParCategorie(String categorie) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres WHERE LOWER(categorie) LIKE LOWER(?) ORDER BY titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + categorie + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                livres.add(extractLivreFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la recherche par catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        
        return livres;
    }
    
    /**
     * Récupère un livre par son ID.
     * 
     * @param id L'ID du livre
     * @return Le livre trouvé ou null si non trouvé
     */
    public Livre getLivreById(int id) {
        String sql = "SELECT * FROM livres WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractLivreFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la récupération du livre: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Affiche tous les livres disponibles dans la bibliothèque.
     * 
     * @return Liste de tous les livres
     */
    public List<Livre> afficherTousLivres() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres ORDER BY categorie, titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                livres.add(extractLivreFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la récupération de tous les livres: " + e.getMessage());
            e.printStackTrace();
        }
        
        return livres;
    }
    
    /**
     * Affiche uniquement les livres disponibles (avec au moins 1 exemplaire).
     * 
     * @return Liste des livres disponibles
     */
    public List<Livre> afficherLivresDisponibles() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres WHERE nombre_exemplaires > 0 ORDER BY categorie, titre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                livres.add(extractLivreFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la récupération des livres disponibles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return livres;
    }
    
    /**
     * Méthode utilitaire pour extraire un objet Livre depuis un ResultSet.
     * Évite la duplication de code dans les différentes méthodes.
     * 
     * @param rs Le ResultSet contenant les données du livre
     * @return Un objet Livre
     * @throws SQLException en cas d'erreur d'accès aux données
     */
    private Livre extractLivreFromResultSet(ResultSet rs) throws SQLException {
        return new Livre(
            rs.getInt("id"),
            rs.getString("titre"),
            rs.getString("auteur"),
            rs.getString("categorie"),
            rs.getInt("nombre_exemplaires")
        );
    }
}