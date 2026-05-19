package iut.gon.model.state;

import iut.gon.hexcoordinate.Coordinate;
import iut.gon.model.Team;
import iut.gon.model.actions.Move;
import iut.gon.model.actions.RemoveLine;
import iut.gon.model.tokens.Token;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IState {

    /**
     * Déplace un anneau et retourne le nouvel état.
     * @throws IllegalArgumentException si la case de départ ne contient pas un anneau du joueur courant,
     *         ou si la destination n'est pas atteignable.
     * @throws IndexOutOfBoundsException si une coordonnée est hors du terrain.
     * @throws RuntimeException si une ligne est déjà présente sur le plateau (doit d'abord être supprimée).
     */
    IState move(Move move);

    /**
     * Supprime une ligne de 5 pions et un anneau, retourne le nouvel état.
     * @throws RuntimeException si la ligne fournie est invalide (taille != 5, n'existe pas, pions pas de la même équipe...)
     *         ou si aucune ligne n'est présente sur le plateau.
     */
    IState removeLine(RemoveLine removeLine);

    /**
     * Retourne l'ensemble des coordonnées accessibles par l'anneau situé en {@code from}.
     */
    Set<Coordinate> availableMove(Coordinate from);

    /**
     * Retourne la map représentant le plateau (coordonnée -> token, null si case vide).
     */
    Map<Coordinate, Token> board();

    /**
     * Retourne la map associant chaque équipe à la liste des coordonnées de ses anneaux.
     */
    Map<Team, List<Coordinate>> rings();

    /**
     * Retourne toutes les lignes de 5 pions présentes sur le plateau (toutes équipes confondues).
     * Une ligne de 6 pions génère deux ensembles de 5.
     */
    List<Set<Coordinate>> lines();

    /**
     * Retourne l'équipe dont c'est le tour.
     */
    Team turn();

    /**
     * Retourne null si personne n'a encore gagné, sinon l'équipe ayant retiré 3 anneaux.
     */
    Team winner();

    /**
     * Détecte toutes les lignes de 5 pions dans le plateau donné, toutes couleurs confondues.
     */
    List<Set<Coordinate>> getPawnsLines(Map<Coordinate, Token> board);

    /**
     * Supprime le token à la coordonnée donnée et retourne le nouvel état.
     */
    IState removeToken(Coordinate c);

    /**
     * Place ou retire un token à la coordonnée donnée selon la logique toggle :
     * si un token de même classe et même équipe existe déjà, on le retire ; sinon on le place.
     */
    IState toggleToken(Coordinate c, Class<? extends Token> tokenClass, Team team);

    /**
     * Retourne true si la coordonnée est dans le terrain.
     */
    boolean isInField(Coordinate c);
}