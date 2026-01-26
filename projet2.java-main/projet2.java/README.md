📚 Système de Gestion de Bibliothèque - Projet Java POO
🎓 Informations du Projet

# Projet: Gestion d'une Bibliothèque

Langage: Java 8+
Base de données: PostgreSQL
Architecture: POO avec Pattern DAO

Auteur: KAREMAMANA Jean-Marie

Date: Janvier 2026

# 📖 Description
Application Java complète de gestion de bibliothèque permettant de gérer les livres, les membres inscrits et les emprunts avec calcul automatique des pénalités de retard. Le système utilise PostgreSQL pour la persistance des données et implémente tous les concepts de la Programmation Orientée Objet.

# ✨ Fonctionnalités
📖 Gestion des Livres

✅ Ajouter un nouveau livre avec titre, auteur, catégorie et nombre d'exemplaires
✅ Modifier les informations d'un livre existant
✅ Supprimer un livre de la bibliothèque
✅ Afficher tous les livres (disponibles et empruntés)
✅ Afficher uniquement les livres disponibles
✅ Recherche multicritère (titre, auteur, catégorie)

👥 Gestion des Membres

✅ Inscrire un nouveau membre (nom, prénom, email, date d'adhésion)
✅ Supprimer un membre (avec vérification des emprunts actifs)
✅ Rechercher un membre par nom ou prénom
✅ Afficher la liste complète des membres
✅ Validation de l'unicité de l'email

📝 Gestion des Emprunts

✅ Enregistrer un emprunt (durée: 14 jours)
✅ Enregistrer le retour d'un livre
✅ Calcul automatique des pénalités (100 F CFA/jour de retard)
✅ Afficher les emprunts en cours
✅ Afficher les emprunts en retard avec alertes
✅ Historique complet des emprunts
✅ Gestion automatique du stock (incrémentation/décrémentation)

📊 Fonctionnalités Additionnelles

✅ Statistiques globales de la bibliothèque
✅ Calcul des pénalités totales à percevoir
✅ Interface utilisateur claire et intuitive
✅ Messages d'erreur explicites
✅ Validation des données entrées

# 🛠️ Technologies Utilisées
Technologie    Version    Utilisation
Java           8+         Langage principal
PostgreSQL     12+        Base de données relationnelle
JDBC           4.2+       Connectivité base de données
Pattern DAO    -          Architecture d'accès aux données
Git            -          Gestion de version

# 📦 Prérequis
Logiciels Requis

1. JDK (Java Development Kit) - Version 8 ou supérieure

bash   
# Vérifier l'installation
   java -version
   javac -version

📥 Télécharger: https://www.oracle.com/java/technologies/downloads/

2. PostgreSQL - Version 12 ou supérieure

bash   
# Vérifier l'installation
   psql --version

📥 Télécharger: https://www.postgresql.org/download/

3. Driver JDBC PostgreSQL

📥 Télécharger: https://jdbc.postgresql.org/download/

Fichier: postgresql-42.7.1.jar (ou version récente)


4. IDE recommandé (optionnel mais fortement conseillé)

IntelliJ IDEA Community: https://www.jetbrains.com/idea/download/
Eclipse: https://www.eclipse.org/downloads/
VS Code + Extension Pack for Java




# 🚀 Installation
Étape 1: Cloner le Repository

bash
# Cloner le projet
git clone https://github.com/jeanmarie516/bibliotheque-java.git

# Naviguer dans le dossier
cd bibliotheque-java

Étape 2: Installer et Configurer PostgreSQL

Windows

cmd
# Démarrer PostgreSQL
pg_ctl -D "C:\Program Files\PostgreSQL\15\data" start

# Ou via Services (Win+R → services.msc)

Linux

bash
# Démarrer PostgreSQL
sudo service postgresql start

# Vérifier le statut
sudo service postgresql status

macOS

bash
# Démarrer PostgreSQL
brew services start postgresql

Étape 3: Créer la Base de Données

bash
# Se connecter à PostgreSQL
psql -U postgres

# Dans psql, exécuter:
CREATE DATABASE bibliotheque_db;
\c bibliotheque_db

# Exécuter le script SQL
\i database/schema.sql

# Ou copier-coller le contenu du fichier schema.sql

Alternative: Utiliser pgAdmin (interface graphique)

Ouvrir pgAdmin
Créer une nouvelle base: bibliotheque_db
Ouvrir l'outil Query et exécuter database/schema.sql

Étape 4: Configurer la Connexion

Modifier le fichier 
src/com/bibliotheque/util/DatabaseConnection.java:

java

private static final String URL = "jdbc:postgresql://localhost:5432/bibliotheque_db";
private static final String USER = "postgres";
private static final String PASSWORD = "VOTRE_MOT_DE_PASSE"; // ⚠️ IMPORTANT

Étape 5: Ajouter le Driver JDBC

Option A: Avec IntelliJ IDEA

File → Project Structure → Libraries
Cliquer sur "+" → Java
Sélectionner postgresql-42.x.x.jar
Cliquer OK

Option B: Avec Eclipse

Right-click sur le projet → Build Path → Configure Build Path
Libraries → Add External JARs
Sélectionner postgresql-42.x.x.jar
Apply and Close

Option C: Ligne de commande
bash
# Placer le JAR dans un dossier lib/
mkdir lib
cp postgresql-42.x.x.jar lib/

# Compiler avec le classpath
javac -cp "lib/*:src" -d bin src/com/bibliotheque/**/*.java

# 💻 Compilation et Exécution
Avec un IDE
IntelliJ IDEA / Eclipse:

Importer le projet
Ajouter le driver JDBC aux bibliothèques
Run → BibliothequeApp.java (main)

En Ligne de Commande

bash
# Structure du projet
bibliotheque-java/
├── src/
├── lib/postgresql-42.x.x.jar
├── database/
└── bin/

# Compilation
javac -cp "lib/*" -d bin -sourcepath src src/com/bibliotheque/BibliothequeApp.java

# Exécution
java -cp "bin:lib/*" com.bibliotheque.BibliothequeApp

# Sur Windows, utiliser ; au lieu de :
java -cp "bin;lib/*" com.bibliotheque.BibliothequeApp

# 📁 Structure du Projet

bibliotheque-java/
│
├── src/
│   └── com/
│       └── bibliotheque/
│           ├── BibliothequeApp.java          # Point d'entrée principal
│           │
│           ├── model/                         # Entités métier
│           │   ├── Livre.java                # Classe Livre
│           │   ├── Membre.java               # Classe Membre
│           │   └── Emprunt.java              # Classe Emprunt
│           │
│           ├── dao/                           # Accès aux données
│           │   ├── LivreDAO.java             # CRUD Livres
│           │   ├── MembreDAO.java            # CRUD Membres
│           │   └── EmpruntDAO.java           # CRUD Emprunts
│           │
│           └── util/                          # Utilitaires
│               └── DatabaseConnection.java    # Connexion BD
│
├── database/
│   └── schema.sql                             # Script création tables
│
├── docs/
│   ├── classes_UML.png                        # Diagramme UML
│   └── screenshots/                           # Captures d'écran
│
├── lib/
│   └── postgresql-42.x.x.jar                  # Driver JDBC
│
├── README.md                                   # Ce fichier
├── lien_github.txt                            # Lien repository
├── .gitignore                                 # Fichiers exclus
└── LICENSE                                     # Licence MIT


# 🎓 Concepts POO Implémentés

1. Encapsulation ✅

java
public class Livre {
    private int id;              // Attributs privés
    private String titre;
    
    public int getId() {         // Getters/Setters publics
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
}
2. Héritage ✅

Toutes les classes Model partagent des comportements communs
Méthode afficherDetails() présente dans chaque entité

3. Polymorphisme ✅
java// Implémentation différente selon la classe
livre.afficherDetails();    // Affiche détails du livre
membre.afficherDetails();   // Affiche détails du membre
emprunt.afficherDetails();  // Affiche détails de l'emprunt
4. Abstraction ✅

Pattern DAO sépare la logique métier de l'accès aux données
Les classes utilisent les DAO sans connaître les détails SQL

5. Collections ✅

java
List<Livre> livres = new ArrayList<>();        // ArrayList
Map<String, Livre> livresMap = new HashMap<>(); // HashMap

6. Gestion des Exceptions ✅

java
try {
    // Code risqué
} catch (SQLException e) {
    System.err.println("Erreur: " + e.getMessage());
}

# 📸 Captures d'Écran

Menu Principal
╔══════════════════════════════════════════════════════════╗
║                     MENU PRINCIPAL                       ║
╠══════════════════════════════════════════════════════════╣
║  1. 📖 Gestion des Livres                                ║
║  2. 👥 Gestion des Membres                               ║
║  3. 📝 Gestion des Emprunts                              ║
║  4. 🔍 Rechercher des Livres                             ║
║  5. ⚠️  Afficher les Emprunts en Retard                  ║
║  6. 📊 Statistiques                                      ║
║  0. 🚪 Quitter                                           ║
╚══════════════════════════════════════════════════════════╝

# 🧪 Tests
Scénarios de Test Recommandés

Test Ajout Livre ✅

Ajouter un livre avec toutes les informations
Vérifier l'ID généré automatiquement
Vérifier l'affichage dans la liste


Test Emprunt ✅

Créer un emprunt valide
Vérifier la décrémentation du stock
Vérifier la date de retour (14 jours)


Test Pénalité ✅

Créer un emprunt
Modifier manuellement la date dans la BD pour simuler un retard
Retourner le livre et vérifier le calcul de pénalité


Test Validation ✅

Tenter d'ajouter un membre avec email existant
Tenter d'emprunter un livre non disponible
Tenter de supprimer un membre avec emprunts actifs



Données de Test Fournies
Le script SQL inclut:

5 livres de différentes catégories
3 membres prêts à emprunter
Structure complète pour commencer immédiatement


# 🐛 Résolution de Problèmes
Problème 1: "Driver PostgreSQL non trouvé"
Symptôme:
ClassNotFoundException: org.postgresql.Driver

Solution:

Vérifier que postgresql-42.x.x.jar est dans le classpath
Dans l'IDE: Vérifier les bibliothèques du projet
En ligne de commande: java -cp "lib/*:bin" ...

Problème 2: "Connexion refusée"
Symptôme:

SQLException: Connection refused

Solutions:

Vérifier que PostgreSQL est démarré:

bash   
# Linux/Mac
   sudo service postgresql status
   
   # Windows
   services.msc → PostgreSQL

Vérifier les credentials dans DatabaseConnection.java
Tester la connexion manuellement:

bash   
psql -U postgres -d bibliotheque_db

Problème 3: "Relation does not exist"
Symptôme:
PSQLException: relation "livres" does not exist

Solution:
Les tables n'ont pas été créées. Exécuter:
bash
psql -U postgres -d bibliotheque_db -f database/schema.sql

Problème 4: Erreur de compilation
Symptôme:
error: package does not exist

Solution:
Vérifier la structure des packages et recompiler:
bash
javac -cp "lib/*" -d bin -sourcepath src src/com/bibliotheque/**/*.java

# 🚀 Améliorations Futures
Priorité Haute

 Interface graphique (JavaFX/Swing)
 Export des données (PDF, Excel)
 Envoi d'emails automatiques pour les retards

Priorité Moyenne

 Système d'authentification avec rôles (admin/bibliothécaire)
 Réservation de livres
 Historique détaillé par membre

Priorité Basse

 API REST pour intégrations externes
 Application mobile
 Statistiques avancées avec graphiques
 Multi-bibliothèques (plusieurs succursales)


# 📜 Licence
Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

MIT License

Copyright (c) 2025 [Pacôme]

Permission is hereby granted, free of charge...

👤 Auteur
Pacôme NGWHAYEVI 

📧 Email: pacomengwhayevi@57.com
🐙 GitHub: https://je.github.io
🎓 Établissement: KFOKAM48
📅 Année académique: 2025-2026

