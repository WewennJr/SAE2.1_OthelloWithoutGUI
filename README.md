# SAE 2.1 & 2.2 — Moteur de Jeu Hexagonal avec IA Minimax

Projet réalisé dans le cadre de la SAE 2.1 & 2.2 du BUT Informatique à l'Université de Caen Normandie.

> L’objectif du projet est de développer :

- Un moteur de jeu en Java pour une variante du jeu Othello sur plateau hexagonal ;
- Une intelligence artificielle utilisant l’algorithme Minimax avec élagage Alpha-Beta ;
- Une architecture logicielle propre et modulaire ;
- Une interface jouable en ligne de commande.

# Fonctionnalités

> Gestion du plateau hexagonal

- Système de coordonnées hexagonales :
    - CoordinateDoubled
    - CoordinateCube
- Conversion entre systèmes de coordonnées
- Gestion des voisins et axes hexagonaux
- Génération automatique des terrains

> Moteur de jeu

- Placement des anneaux
- Déplacement selon les règles officielles
- Gestion des pions
- Détection des lignes de 5
- Suppression des lignes et anneaux
- Détection de victoire et Match nul
- États immuables avec Record

> Intelligence artificielle

- Algorithme Minimax
- Élagage Alpha-Beta
- Fonction heuristique d’évaluation
- Génération des coups possibles
- Choix automatique du meilleur coup

> Qualité logicielle

- Architecture orientée objet
- Projet Maven
- Tests unitaires JUnit
- Gestion Git avec branches

# Technologies utilisées

- Java 21+
- Maven
- JUnit 5
- Git / GitHub
- Java Records
- IntelliJ IDEA / Eclipse

# Structure du projet

```text
HexagonalCoordinates/
├── pom.xml
├── .gitignore
└── src/
    └── main/
        └── java/
            └── iut/
                └── gon/
                    └── Coordinate/
                        ├── Coordinate.java
                        ├── CoordinateCube.java
                        ├── CoordinateDoubled.java
                        ├── DifferentAxisException.java
                        ├── Direction.java
                        ├── Mode.java
                        └── Point.java

Othello/
├── pom.xml
├── .gitignore
└── src/
    ├── main/
    │   └── java/
    │       └── iut/
    │           └── gon/
    │               ├── Main.java
    │               ├── MainAI.java
    │               ├── CUIMain.java
    │               ├── Model.java
    │               ├── Team.java
    │               └── model/
    │                   ├── actions/
    │                   │   ├── Action.java
    │                   │   ├── Move.java
    │                   │   └── RemoveLine.java
    │                   ├── state/
    │                   │   ├── IState.java
    │                   │   └── State.java
    │                   ├── tokens/
    │                   │   ├── Token.java
    │                   │   ├── Pawn.java
    │                   │   └── Ring.java
    │                   ├── IA/
    │                   │   ├── AI.java
    │                   │   ├── MinimaxAI.java
    │                   │   └── Node.java
    │                   └── factory/
    │                       ├── Factory.java
    │                       ├── FactoryCube.java
    │                       └── FactoryDoubled.java
    │
    └── test/
        └── java/
            └── iut/
                └── gon/
                    ├── actions/
                    │   ├── MoveTest.java
                    │   └── RemoveLineTest.java
                    ├── state/
                    │   └── StateTest.java
                    ├── tokens/
                    │   ├── PawnTest.java
                    │   └── RingTest.java
                    ├── IA/
                    │   ├── MinimaxAITest.java
                    │   └── NodeTest.java
                    ├── test/
                    │   ├── CoordinateCubeTest.java
                    │   ├── CoordinateDoubledTest.java
                    │   └── CoordinateTest.java
                    └── factory/
                        └── FactoryTest.java
```

# Architecture du projet

> Module HexagonalCoordinate

Module indépendant permettant de gérer :

- les coordonnées hexagonales;
- les déplacements;
- les axes;
- les conversions de coordonnées.

> Classes principales

- Coordinate
- CoordinateCube
- CoordinateDoubled
- Mode
- FactoryDoubled

# Projet principal

> Gestion du jeu

- State
- IState
- Model
- Token
- Ring
- Pawn

> Actions

- Action
- Move
- RemoveLine

> IA

- Node
- AI
- MinimaxAI

> Interface Utilisateur

- CUIMain
- MainAI
- Main (lancement du jeu)

# Installation

> Cloner le projet

git clone https://redmine-etu.unicaen.fr/git/groupe13-le-fh-ba-gw

# Compilation

> Avec Maven :

mvn clean install

# Lancer le projet

Lancer le fichier Main.java 
(tout en ayant mis HexagonalCoordinates comme `Module Dependency`)

# Règles du jeu

Le jeu se déroule sur un plateau hexagonal.

Chaque joueur possède :

- 5 anneaux ;
- des pions générés lors des déplacements.

> Objectif

Créer des lignes de 5 pions afin de :

- retirer une ligne ;
- retirer un anneau.

Le premier joueur ayant retiré 3 anneaux gagne la partie.

# Déplacements

> Un anneau peut :

- se déplacer librement sur un axe ;
- sauter des pions ;
- ne jamais traverser un autre anneau.

> Après déplacement :

les pions survolés changent de couleur ;
un pion est laissé sur l’ancienne position.

# Intelligence artificielle

> L’IA repose sur :

- Minimax ;
- une profondeur de recherche configurable ;
- une heuristique d’évaluation.

> Critères possibles d’évaluation
- victoire/défaite ;
- nombre d’anneaux restants ;
- lignes de 4 ;
- mobilité ;
- contrôle du centre.

# Git

> Le projet suit une organisation Git avec :

- branches par fonctionnalité ;
- branche master stable ;
- commits réguliers et détaillés.
- Objectifs pédagogiques
- Développement orienté objet avancé
- Manipulation de structures hexagonales
- Conception d’IA de jeu
- Parcours d’arbres
- Algorithme Minimax
- Tests unitaires
- Travail collaboratif avec Git

# Auteurs

BOGDANOV Alexei
GIANG Wendy
FAVEROULT Hugo
LEGROS Ewenn

BUT Informatique — 2025/2026

# Licence

Projet universitaire réalisé dans un cadre pédagogique.
