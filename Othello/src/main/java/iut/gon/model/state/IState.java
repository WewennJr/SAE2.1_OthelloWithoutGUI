package iut.gon.model.state;

import iut.gon.Coordinate.Coordinate;
import iut.gon.model.Team;
import iut.gon.model.actions.Move;
import iut.gon.model.actions.RemoveLine;
import iut.gon.model.tokens.Token;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Représente l'état complet du jeu à un instant donné.
 * <p>
 * Cette interface définit toutes les opérations possibles sur le plateau :
 * déplacement des anneaux, suppression de lignes, gestion des tours,
 * détection des lignes de pions et accès au plateau.
 * </p>
 */
public interface IState {

    /**
     * Déplace un anneau et retourne le nouvel état.
     *
     * @param move déplacement à effectuer
     * @return nouvel état du jeu après le déplacement
     * @throws IllegalArgumentException si la case de départ ne contient pas un anneau du joueur courant,
     *         ou si la destination n'est pas atteignable.
     * @throws IndexOutOfBoundsException si une coordonnée est hors du terrain.
     * @throws RuntimeException si une ligne est déjà présente sur le plateau (doit d'abord être supprimée).
     */
    IState move(Move move);

    /**
     * Supprime une ligne de 5 pions et un anneau, retourne le nouvel état.
     *
     * @param removeLine ligne à supprimer
     * @return nouvel état du jeu après suppression
     * @throws RuntimeException si la ligne fournie est invalide (taille != 5, n'existe pas,
     *         pions pas de la même équipe...) ou si aucune ligne n'est présente sur le plateau.
     */
    IState removeLine(RemoveLine removeLine);

    /**
     * Retourne l'ensemble des coordonnées accessibles par l'anneau situé en {@code from}.
     *
     * @param from coordonnée de départ de l'anneau
     * @return ensemble des positions atteignables
     */
    Set<Coordinate> availableMove(Coordinate from);

    /**
     * Retourne la map représentant le plateau.
     *
     * @return map coordonnée -> token (null si case vide)
     */
    Map<Coordinate, Token> board();

    /**
     * Retourne la map associant chaque équipe à la liste des coordonnées de ses anneaux.
     *
     * @return positions des anneaux par équipe
     */
    Map<Team, List<Coordinate>> rings();

    /**
     * Retourne toutes les lignes de 5 pions présentes sur le plateau.
     * Une ligne de 6 pions génère deux ensembles de 5.
     *
     * @return liste des lignes détectées
     */
    List<Set<Coordinate>> lines();

    /**
     * Retourne l'équipe dont c'est le tour.
     *
     * @return équipe actuelle
     */
    Team turn();

    /**
     * Retourne l'équipe gagnante si elle existe.
     *
     * @return équipe ayant retiré 3 anneaux, ou null si aucune victoire
     */
    Team winner();

    /**
     * Détecte toutes les lignes de 5 pions dans le plateau donné.
     *
     * @param board plateau à analyser
     * @return liste des lignes détectées
     */
    List<Set<Coordinate>> getPawnsLines(Map<Coordinate, Token> board);

    /**
     * Supprime le token à la coordonnée donnée.
     *
     * @param c coordonnée ciblée
     * @return nouvel état du jeu après suppression
     */
    IState removeToken(Coordinate c);

    /**
     * Place ou retire un token à une coordonnée (toggle).
     *
     * @param c coordonnée ciblée
     * @param tokenClass type de token
     * @param team équipe du token
     * @return nouvel état après modification
     */
    IState toggleToken(Coordinate c, Class<? extends Token> tokenClass, Team team);

    /**
     * Vérifie si une coordonnée appartient au plateau.
     *
     * @param c coordonnée à tester
     * @return true si la coordonnée est dans le terrain, false sinon
     */
    boolean isInField(Coordinate c);
}