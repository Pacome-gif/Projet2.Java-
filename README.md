Description du Projet :
Les étudiants doivent concevoir une application qui permet de :
1. Ajouter, modifier et supprimer des livres dans la bibliothèque.
2. Gérer les membres inscrits à la bibliothèque.
3. Gérer les emprunts et retours de livres.
4. Rechercher des livres par titre, auteur ou catégorie.
5. Gérer les pénalités pour les retards d’emprunts.
Fonctionnalités Obligatoires :
1. Gestion des Livres :
o Attributs : id, titre, auteur, categorie, nombreExemplaires.
o Méthodes :
 Ajouter un livre.
 Rechercher un livre par titre ou catégorie.
 Afficher tous les livres disponibles.
2. Gestion des Membres :
o Attributs : id, nom, prénom, email, adhesionDate.
o Méthodes :
 Inscrire un nouveau membre.
 Supprimer un membre.
 Rechercher un membre par nom.
3. Gestion des Emprunts :
o Attributs : idEmprunt, membreId, livreId, dateEmprunt,
dateRetourPrevue, dateRetourEffective.
o Méthodes :
 Enregistrer un emprunt.
 Gérer le retour d’un livre.
 Calculer les pénalités en cas de retard.
4. Système de Recherche :
o Rechercher un livre par :
 Titre.
 Auteur.
 Catégorie.
5. Gestion des Retards et Pénalités :
o Calculer le retard à partir de la dateRetourPrevue et appliquer une pénalité
(exemple : 100 F CFA par jour de retard).
