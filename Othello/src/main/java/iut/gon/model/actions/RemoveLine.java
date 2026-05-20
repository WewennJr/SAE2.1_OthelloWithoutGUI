package iut.gon.model.actions;

import iut.gon.Coordinate.Coordinate;

import java.util.Set;

/**
 * Représente une action de suppression d'une ligne sur le plateau.
 * <p>
 * Cette action contient un ensemble de coordonnées formant une ligne
 * ainsi qu'un anneau associé à cette suppression.
 * </p>
 */
public class RemoveLine extends Action {

    /**
     * Ensemble des coordonnées formant la ligne supprimée.
     */
    public Set<Coordinate> line;

    /**
     * Coordonnée de l'anneau associé à l'action.
     */
    public Coordinate ring;

    /**
     * Crée une nouvelle action de suppression de ligne.
     *
     * @param line ensemble des coordonnées de la ligne supprimée
     * @param ring coordonnée de l'anneau associé
     */
    public RemoveLine(Set<Coordinate> line, Coordinate ring) {
        this.line = line;
        this.ring = ring;
    }

    /**
     * Retourne la ligne supprimée.
     *
     * @return un ensemble de coordonnées représentant la ligne
     */
    public Set<Coordinate> getLine() {
        return line;
    }

    /**
     * Modifie la ligne supprimée.
     *
     * @param line nouvel ensemble de coordonnées
     */
    public void setLine(Set<Coordinate> line) {
        this.line = line;
    }

    /**
     * Retourne la coordonnée de l'anneau associé.
     *
     * @return la coordonnée de l'anneau
     */
    public Coordinate getRing() {
        return ring;
    }

    /**
     * Modifie la coordonnée de l'anneau associé.
     *
     * @param ring nouvelle coordonnée de l'anneau
     */
    public void setRing(Coordinate ring) {
        this.ring = ring;
    }
}