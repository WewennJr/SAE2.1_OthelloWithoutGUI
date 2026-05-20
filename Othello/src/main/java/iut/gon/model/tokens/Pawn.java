package iut.gon.model.tokens;

import iut.gon.model.Team;

/**
 * Représente un pion dans le jeu.
 * <p>
 * Un pion appartient à une équipe (noire ou blanche) et peut changer de camp
 * selon les règles du jeu.
 * </p>
 */
public class Pawn extends Token {

    /**
     * Crée un pion appartenant à une équipe donnée.
     *
     * @param color équipe du pion
     */
    public Pawn(Team color) {
        super(color);
    }

    /**
     * Change l'équipe du pion (BLACK ↔ WHITE).
     */
    public void changeTeam() {
        if (this.team == Team.BLACK) {
            this.team = Team.WHITE;
        } else {
            this.team = Team.BLACK;
        }
    }

    /**
     * Retourne une représentation textuelle du pion.
     *
     * @return "x" si le pion est noir, "." si le pion est blanc
     */
    @Override
    public String charRepr() {
        if (team == Team.BLACK) {
            return "x";
        }
        else {
            return ".";
        }
    }

    /**
     * Crée une copie du pion.
     *
     * @return une nouvelle instance de Pawn avec la même équipe
     */
    @Override
    public Token clone() {
        return new Pawn(this.team);
    }
}