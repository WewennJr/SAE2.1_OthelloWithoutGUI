package iut.gon.model;

import iut.gon.hexcoordinate.Coordinate;
import iut.gon.model.actions.Move;
import iut.gon.model.actions.RemoveLine;
import iut.gon.model.state.IState;
import iut.gon.model.tokens.Token;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Classe façade du modèle : stocke l'état courant et délègue toutes les opérations à IState.
 * Les méthodes qui modifient l'état ne renvoient rien : elles mettent à jour currentState.
 */
public class Model {

    private IState currentState;

    public Model(IState currentState) {
        this.currentState = currentState;
    }

    public IState getCurrentState() {
        return currentState;
    }

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
     * Retourne les déplacements possibles depuis {@code from}.
     */
    public Set<Coordinate> movesFrom(Coordinate from) {
        return currentState.availableMove(from);
    }

    /**
     * Retourne toutes les lignes de 5 pions présentes sur le plateau.
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
     * Retourne le token à la coordonnée donnée, ou null si la case est vide.
     */
    public Token getTokenAt(Coordinate c) {
        return currentState.board().get(c);
    }

    /**
     * Retourne true si la coordonnée est dans le terrain.
     */
    public boolean isInField(Coordinate c) {
        return currentState.isInField(c);
    }

    /**
     * Retourne la liste des coordonnées des anneaux d'une équipe.
     */
    public List<Coordinate> getRings(Team team) {
        return currentState.rings().get(team);
    }

    /**
     * Retourne la liste des coordonnées des pions d'une équipe.
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
     * Retourne le gagnant, ou null si la partie n'est pas terminée.
     */
    public Team getWinner() {
        return currentState.winner();
    }
}