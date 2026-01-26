package com.bibliotheque.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour gérer les connexions à la base de données PostgreSQL.
 * Cette classe utilise le pattern Singleton pour garantir une seule instance de connexion.
 * 
 * @author Votre Nom
 * @version 1.0
 */
public class DatabaseConnection {
    // Configuration de la connexion à PostgreSQL
    private static final String URL = "jdbc:postgresql://localhost:5432/bibliotheque_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "votre_mot_de_passe"; // ⚠️ À MODIFIER
    
    /**
     * Établit et retourne une connexion à la base de données.
     * 
     * @return Connection - Objet de connexion à la base de données
     * @throws RuntimeException si la connexion échoue
     */
    public static Connection getConnection() {
        try {
            // Chargement du driver PostgreSQL
            Class.forName("org.postgresql.Driver");
            
            // Établissement de la connexion
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            
            if (conn != null) {
                System.out.println("✓ Connexion à la base de données établie avec succès!");
            }
            
            return conn;
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ ERREUR: Driver PostgreSQL non trouvé!");
            System.err.println("  Solution: Ajoutez postgresql-XX.jar au classpath");
            System.err.println("  Détails: " + e.getMessage());
            throw new RuntimeException("Driver PostgreSQL non disponible", e);
            
        } catch (SQLException e) {
            System.err.println("✗ ERREUR: Impossible de se connecter à la base de données!");
            System.err.println("  Vérifiez que:");
            System.err.println("  1. PostgreSQL est démarré");
            System.err.println("  2. La base 'bibliotheque_db' existe");
            System.err.println("  3. Les credentials (USER/PASSWORD) sont corrects");
            System.err.println("  Détails: " + e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
    }
    
    /**
     * Teste la connexion à la base de données.
     * Utilisé pour vérifier que tout fonctionne correctement.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}