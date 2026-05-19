package iut.gon.model.tokens;

import iut.gon.model.Team;

public abstract class Token {

    public Team team;

    public Token(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public abstract String charRepr();

    public abstract Token clone();
}