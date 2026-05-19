package iut.gon.model.tokens;

import iut.gon.model.Team;

public class Pawn extends Token {

    public Pawn(Team color) {
        super(color);
    }

    public void changeTeam() {
        if (this.team == Team.BLACK) {
            this.team = Team.WHITE;
        } else {
            this.team = Team.BLACK;
        }
    }

    @Override
    public String charRepr() {
        return team == Team.BLACK ? "x" : ".";
    }

    @Override
    public Token clone() {
        return new Pawn(this.team);
    }
}