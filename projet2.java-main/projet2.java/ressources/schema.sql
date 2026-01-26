-- Création de la base de données: PostgreSQL
CREATE DATABASE bibliotheque_db;

-- Se connecter à la base de données
-- \c bibliotheque_db

-- SUPPRESSION DES TABLES EXISTANTES (si nécessaire)

DROP TABLE IF EXISTS emprunts CASCADE;
DROP TABLE IF EXISTS membres CASCADE;
DROP TABLE IF EXISTS livres CASCADE;

-- TABLE: livres

CREATE TABLE livres (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(255) NOT NULL,
    categorie VARCHAR(100) NOT NULL,
    nombre_exemplaires INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_nombre_exemplaires CHECK (nombre_exemplaires >= 0)
);

-- TABLE: membres

CREATE TABLE membres (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    adhesion_date DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- TABLE: emprunts

CREATE TABLE emprunts (
    id_emprunt SERIAL PRIMARY KEY,
    membre_id INTEGER NOT NULL,
    livre_id INTEGER NOT NULL,
    date_emprunt DATE NOT NULL DEFAULT CURRENT_DATE,
    date_retour_prevue DATE NOT NULL,
    date_retour_effective DATE,
    penalite DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_membre FOREIGN KEY (membre_id) 
        REFERENCES membres(id) ON DELETE CASCADE,
    CONSTRAINT fk_livre FOREIGN KEY (livre_id) 
        REFERENCES livres(id) ON DELETE CASCADE,
    CONSTRAINT chk_dates CHECK (date_retour_prevue >= date_emprunt),
    CONSTRAINT chk_penalite CHECK (penalite >= 0)
);

-- INDEX POUR OPTIMISER LES PERFORMANCES:

-- Index sur les recherches fréquentes de livres
CREATE INDEX idx_livres_titre ON livres(titre);
CREATE INDEX idx_livres_auteur ON livres(auteur);
CREATE INDEX idx_livres_categorie ON livres(categorie);

-- Index sur les recherches de membres
CREATE INDEX idx_membres_nom ON membres(nom);
CREATE INDEX idx_membres_prenom ON membres(prenom);
CREATE INDEX idx_membres_email ON membres(email);

-- Index sur les emprunts
CREATE INDEX idx_emprunts_membre ON emprunts(membre_id);
CREATE INDEX idx_emprunts_livre ON emprunts(livre_id);
CREATE INDEX idx_emprunts_date_retour ON emprunts(date_retour_prevue);
CREATE INDEX idx_emprunts_en_cours ON emprunts(date_retour_effective) 
    WHERE date_retour_effective IS NULL;

-- DONNÉES DE TEST

-- Insertion de livres
INSERT INTO livres (titre, auteur, categorie, nombre_exemplaires) VALUES
('Le Petit Prince', 'Antoine de Saint-Exupéry', 'Roman', 5),
('1984', 'George Orwell', 'Science-Fiction', 3),
('L''Étranger', 'Albert Camus', 'Roman', 4),
('Les Misérables', 'Victor Hugo', 'Roman', 2),
('Introduction à Java', 'Kathy Sierra', 'Informatique', 6),
('Harry Potter à l''école des sorciers', 'J.K. Rowling', 'Fantasy', 8),
('Le Seigneur des Anneaux', 'J.R.R. Tolkien', 'Fantasy', 4),
('Une brève histoire du temps', 'Stephen Hawking', 'Science', 3),
('L''Alchimiste', 'Paulo Coelho', 'Roman', 5),
('Sapiens', 'Yuval Noah Harari', 'Histoire', 3);

-- Insertion de membres
INSERT INTO membres (nom, prenom, email, adhesion_date) VALUES
('Dupont', 'Jean', 'jean.dupont@email.com', '2024-01-15'),
('Martin', 'Marie', 'marie.martin@email.com', '2024-02-20'),
('Durand', 'Paul', 'paul.durand@email.com', '2024-03-10'),
('Bernard', 'Sophie', 'sophie.bernard@email.com', '2024-04-05'),
('Petit', 'Lucas', 'lucas.petit@email.com', '2024-05-12');

-- Insertion d'emprunts de test (optionnel)
-- Emprunt en cours (pas encore retourné)
INSERT INTO emprunts (membre_id, livre_id, date_emprunt, date_retour_prevue) VALUES
(1, 1, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '9 days'),
(2, 3, CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE + INTERVAL '11 days');

-- Emprunt terminé (retourné à temps)
INSERT INTO emprunts (membre_id, livre_id, date_emprunt, date_retour_prevue, date_retour_effective, penalite) VALUES
(3, 2, CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '6 days', CURRENT_DATE - INTERVAL '7 days', 0.00);

-- Emprunt en retard (simulé)
INSERT INTO emprunts (membre_id, livre_id, date_emprunt, date_retour_prevue) VALUES
(4, 5, CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '6 days');

-- VUES UTILES (optionnel mais recommandé)

-- Vue pour voir les emprunts avec détails complets
CREATE OR REPLACE VIEW vue_emprunts_complets AS
SELECT 
    e.id_emprunt,
    e.date_emprunt,
    e.date_retour_prevue,
    e.date_retour_effective,
    e.penalite,
    m.id as membre_id,
    m.nom || ' ' || m.prenom as membre_nom_complet,
    m.email as membre_email,
    l.id as livre_id,
    l.titre as livre_titre,
    l.auteur as livre_auteur,
    l.categorie as livre_categorie,
    CASE 
        WHEN e.date_retour_effective IS NULL AND e.date_retour_prevue < CURRENT_DATE 
        THEN 'EN RETARD'
        WHEN e.date_retour_effective IS NULL 
        THEN 'EN COURS'
        ELSE 'TERMINE'
    END as statut
FROM emprunts e
JOIN membres m ON e.membre_id = m.id
JOIN livres l ON e.livre_id = l.id;

-- Vue pour les livres disponibles
CREATE OR REPLACE VIEW vue_livres_disponibles AS
SELECT 
    id,
    titre,
    auteur,
    categorie,
    nombre_exemplaires,
    CASE 
        WHEN nombre_exemplaires > 0 THEN 'DISPONIBLE'
        ELSE 'INDISPONIBLE'
    END as disponibilite
FROM livres
WHERE nombre_exemplaires > 0
ORDER BY categorie, titre;

-- Vue pour les emprunts en retard
CREATE OR REPLACE VIEW vue_emprunts_en_retard AS
SELECT 
    e.id_emprunt,
    m.nom || ' ' || m.prenom as membre,
    m.email,
    l.titre as livre,
    e.date_emprunt,
    e.date_retour_prevue,
    CURRENT_DATE - e.date_retour_prevue as jours_retard,
    (CURRENT_DATE - e.date_retour_prevue) * 100 as penalite_calculee
FROM emprunts e
JOIN membres m ON e.membre_id = m.id
JOIN livres l ON e.livre_id = l.id
WHERE e.date_retour_effective IS NULL 
  AND e.date_retour_prevue < CURRENT_DATE
ORDER BY jours_retard DESC;

-- FONCTIONS UTILES (optionnel)

-- Fonction pour calculer la pénalité
CREATE OR REPLACE FUNCTION calculer_penalite(
    p_date_retour_prevue DATE,
    p_date_retour_effective DATE
) RETURNS DECIMAL(10,2) AS $$
DECLARE
    v_jours_retard INTEGER;
    v_penalite DECIMAL(10,2);
BEGIN
    -- Si pas de date de retour effective, utiliser la date actuelle
    IF p_date_retour_effective IS NULL THEN
        p_date_retour_effective := CURRENT_DATE;
    END IF;
    
    -- Calculer les jours de retard
    v_jours_retard := p_date_retour_effective - p_date_retour_prevue;
    
    -- Si retard, calculer pénalité (100 F CFA par jour)
    IF v_jours_retard > 0 THEN
        v_penalite := v_jours_retard * 100.0;
    ELSE
        v_penalite := 0.0;
    END IF;
    
    RETURN v_penalite;
END;
$$ LANGUAGE plpgsql;

-- TRIGGER POUR MISE À JOUR AUTOMATIQUE DES PÉNALITÉS

CREATE OR REPLACE FUNCTION update_penalite_on_retour()
RETURNS TRIGGER AS $$
BEGIN
    -- Calculer et mettre à jour la pénalité lors du retour
    IF NEW.date_retour_effective IS NOT NULL THEN
        NEW.penalite := calculer_penalite(NEW.date_retour_prevue, NEW.date_retour_effective);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_penalite
    BEFORE INSERT OR UPDATE ON emprunts
    FOR EACH ROW
    EXECUTE FUNCTION update_penalite_on_retour();

-- REQUÊTES DE VÉRIFICATION

-- Vérifier que les tables sont créées
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
  AND table_type = 'BASE TABLE'
ORDER BY table_name;

-- Compter les enregistrements
SELECT 
    'livres' as table_name, COUNT(*) as nombre_enregistrements FROM livres
UNION ALL
SELECT 
    'membres' as table_name, COUNT(*) as nombre_enregistrements FROM membres
UNION ALL
SELECT 
    'emprunts' as table_name, COUNT(*) as nombre_enregistrements FROM emprunts;

-- Afficher les livres disponibles
SELECT * FROM vue_livres_disponibles;

-- Afficher les emprunts en retard (si existants)
SELECT * FROM vue_emprunts_en_retard;

-- COMMENTAIRES SUR LES TABLES

COMMENT ON TABLE livres IS 'Table contenant tous les livres de la bibliothèque';
COMMENT ON TABLE membres IS 'Table contenant tous les membres inscrits';
COMMENT ON TABLE emprunts IS 'Table contenant l''historique des emprunts';

COMMENT ON COLUMN livres.nombre_exemplaires IS 'Nombre d''exemplaires disponibles actuellement';
COMMENT ON COLUMN emprunts.penalite IS 'Montant de la pénalité en F CFA (100 F CFA par jour de retard)';

-- FIN DU SCRIPT

-- Message de confirmation
DO $$
BEGIN
    RAISE NOTICE '✓ Base de données bibliothèque_db créée avec succès!';
    RAISE NOTICE '✓ 3 tables créées: livres, membres, emprunts';
    RAISE NOTICE '✓ Données de test insérées';
    RAISE NOTICE '✓ Index créés pour optimisation';
    RAISE NOTICE '✓ Vues et fonctions créées';
    RAISE NOTICE '';
    RAISE NOTICE '→ Vous pouvez maintenant lancer l''application Java!';
END $$;