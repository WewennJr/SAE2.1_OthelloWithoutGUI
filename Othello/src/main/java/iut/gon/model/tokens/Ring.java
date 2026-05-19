package iut.gon.model.tokens;

import iut.gon.model.Team;

public class Ring extends Token {

    public Ring(Team color) {
        super(color);
    }

    @Override
    public String charRepr() {
        if (team == Team.BLACK) {
            return "O";
        }
        else {
            return "o";
        }
    }

    @Override
    public Token clone() {
        return new Ring(this.team);
    }
}