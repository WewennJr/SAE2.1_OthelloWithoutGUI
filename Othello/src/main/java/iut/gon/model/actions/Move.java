package iut.gon.model.actions;

import iut.gon.Coordinate.Coordinate;

/**
 * Représente une action de déplacement entre deux coordonnées.
 * <p>
 * Cette classe permet de modéliser un mouvement d'une position
 * de départ vers une position d'arrivée.
 * </p>
 */
public class Move extends Action {

    /**
     * Coordonnée de départ du déplacement.
     */
    public Coordinate from;

    /**
     * Coordonnée d'arrivée du déplacement.
     */
    public Coordinate to;

    /**
     * Crée une nouvelle action de déplacement.
     *
     * @param from coordonnée de départ
     * @param to coordonnée d'arrivée
     */
    public Move(Coordinate from, Coordinate to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Retourne la coordonnée de départ.
     *
     * @return la coordonnée de départ
     */
    public Coordinate getFrom() {
        return from;
    }

    /**
     * Modifie la coordonnée de départ.
     *
     * @param from nouvelle coordonnée de départ
     */
    public void setFrom(Coordinate from) {
        this.from = from;
    }

    /**
     * Retourne la coordonnée d'arrivée.
     *
     * @return la coordonnée d'arrivée
     */
    public Coordinate getTo() {
        return to;
    }

    /**
     * Modifie la coordonnée d'arrivée.
     *
     * @param to nouvelle coordonnée d'arrivée
     */
    public void setTo(Coordinate to) {
        this.to = to;
    }
}