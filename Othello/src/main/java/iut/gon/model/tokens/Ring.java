package iut.gon.model.tokens;

import iut.gon.model.Team;

/**
 * Représente un anneau dans le jeu.
 * <p>
 * Un anneau appartient à une équipe (noire ou blanche) et possède une
 * représentation textuelle différente selon son propriétaire.
 * </p>
 */
public class Ring extends Token {

    /**
     * Crée un anneau appartenant à une équipe donnée.
     *
     * @param color équipe de l'anneau
     */
    public Ring(Team color) {
        super(color);
    }

    /**
     * Retourne une représentation textuelle de l'anneau.
     *
     * @return "O" si l'anneau est noir, "o" si l'anneau est blanc
     */
    @Override
    public String charRepr() {
        if (team == Team.BLACK) {
            return "O";
        }
        else {
            return "o";
        }
    }

    /**
     * Crée une copie de l'anneau.
     *
     * @return une nouvelle instance de Ring avec la même équipe
     */
    @Override
    public Token clone() {
        return new Ring(this.team);
    }
}