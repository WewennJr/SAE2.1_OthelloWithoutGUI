package iut.gon.model;

import java.awt.Color;

/**
 * Représente les deux équipes possibles dans le jeu.
 */
public enum Team {

    BLACK,
    WHITE;

    /**
     * Couleur associée à l'équipe (utilisée pour l'affichage).
     */
    public Color color;

    /**
     * Retourne la couleur associée à l'équipe.
     *
     * @return couleur de l'équipe
     */
    public Color getColor() {
        return color;
    }

    /**
     * Définit la couleur associée à l'équipe.
     *
     * @param color nouvelle couleur
     */
    public void setColor(Color color) {
        this.color = color;
    }
}