package iut.gon.model;

import iut.gon.Coordinate.Coordinate;
import iut.gon.model.actions.Move;
import iut.gon.model.actions.RemoveLine;
import iut.gon.model.state.IState;
import iut.gon.model.tokens.Token;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Classe façade du modèle : stocke l'état courant et délègue toutes les opérations à IState.
 * <p>
 * Elle sert d'interface simplifiée entre la logique de jeu et les couches supérieures (IA, UI, contrôleur).
 * </p>
 */
public class Model {

    private IState currentState;

    /**
     * Crée un modèle à partir d'un état initial.
     *
     * @param currentState état initial du jeu
     */
    public Model(IState currentState) {
        this.currentState = currentState;
    }

    /**
     * Retourne l'état actuel du jeu.
     */
    public IState getCurrentState() {
        return currentState;
    }

    /**
     * Modifie l'état courant du jeu.
     *
     * @param currentState nouvel état
     */
    public void setCurrentState(IState currentState) {
        this.currentState = currentState;
    }

    /**
     * Déplace un anneau de {@code from} vers {@code to}.
     */
    public void moveRing(Coordinate from, Coordinate to) {
        currentState = currentState.move(new Move(from, to));
    }

    /**
     * Supprime une ligne de 5 pions et un anneau.
     */
    public void removeLine(Set<Coordinate> line, Coordinate ring) {
        currentState = currentState.removeLine(new RemoveLine(line, ring));
    }

    /**
     * Retourne les déplacements possibles depuis une coordonnée donnée.
     *
     * @param from coordonnée de départ
     * @return ensemble des positions accessibles
     */
    public Set<Coordinate> movesFrom(Coordinate from) {
        return currentState.availableMove(from);
    }

    /**
     * Retourne toutes les lignes de 5 pions présentes sur le plateau.
     *
     * @return liste des lignes détectées
     */
    public List<Set<Coordinate>> getPawnsLines() {
        return currentState.lines();
    }

    /**
     * Retourne le plateau complet (coordonnée -> token ou null).
     */
    public Map<Coordinate, Token> getBoard() {
        return currentState.board();
    }

    /**
     * Retourne le token présent à une coordonnée donnée.
     *
     * @param c coordonnée
     * @return token ou null si vide
     */
    public Token getTokenAt(Coordinate c) {
        return currentState.board().get(c);
    }

    /**
     * Vérifie si une coordonnée appartient au terrain.
     *
     * @param c coordonnée
     * @return true si la case est valide
     */
    public boolean isInField(Coordinate c) {
        return currentState.isInField(c);
    }

    /**
     * Retourne les coordonnées des anneaux d'une équipe.
     *
     * @param team équipe concernée
     * @return liste des positions des anneaux
     */
    public List<Coordinate> getRings(Team team) {
        return currentState.rings().get(team);
    }

    /**
     * Retourne les coordonnées des pions d'une équipe.
     *
     * @param team équipe concernée
     * @return liste des positions des pions
     */
    public List<Coordinate> getPawns(Team team) {
        return currentState.board().entrySet().stream()
                .filter(e -> e.getValue() instanceof iut.gon.model.tokens.Pawn && e.getValue().getTeam() == team)
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Retourne l'équipe dont c'est le tour.
     */
    public Team getTurn() {
        return currentState.turn();
    }

    /**
     * Retourne le gagnant de la partie, ou null si elle n'est pas terminée.
     */
    public Team getWinner() {
        return currentState.winner();
    }
}