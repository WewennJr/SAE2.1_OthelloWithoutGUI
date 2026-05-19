package iut.gon.model.tokens;

public abstract class Token {

    public Team team;

    public Token(Team team){
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String charRepr(){
        return null;
    }

    public Token clone(){
        return null;
    }

}
