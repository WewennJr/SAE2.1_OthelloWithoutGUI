package iut.gon.model;

import iut.gon.model.state.IState;

public class Model {
    private IState currentState;

    public Model(IState currentState) {
        this.currentState = currentState;
    }

    public void setCurrentState(IState currentState) {
        this.currentState = currentState;
    }

    public Set<Coordinate> movesFrom(Coordinate from) {
        return null;
    }

    public void moveRing(Coordinate from, Coordinate to) {

    }
    public List<Set<Coordinate>> getPawnsLines() {
        return null;
    }

    public void removeLine(Set<Coordinate> line, Coordinate ring) {

    }

    public Map<Coordinate, Token> getBoard() {
        return null;
    }

    public Token getTokenAt(Coordinate c) {
        return null;
    }

    public boolean isInField(Coordinate c) {
        return false;
    }

    public List<Coordinate> getRings(Team team) {
        return null;
    }

    public List<Coordinate> getPawns(Team team) {
        return null;
    }

    public Team getTurn() {
        return null;
    }

    public IState getCurrentState() {
        return null;
    }
}
