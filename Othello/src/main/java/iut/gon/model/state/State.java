package iut.gon.model.state;

import iut.gon.model.Team;
import iut.gon.model.actions.Move;
import iut.gon.model.actions.RemoveLine;
import iut.gon.model.tokens.Token;

import java.util.Objects;


public class State implements IState {
    private Map<Coordinate, Token> board;
    private Team turn;
    private List<Set<Coordinate>> lines;

    public void State(Map<Coordinate, Token> board, Team turn, List<Set<Coordinate>> lines) {
        this.board = board;
        this.turn = turn;
        this.lines = lines;
    }

    @Override
    public IState move(Move move) {
        return null;
    }

    @Override
    public IState removeLine(RemoveLine removeLine) {
        return null;
    }

    @Override
    public Set<Coordinate> availableMove(Coordinate from) {
        return null;
    }

    @Override
    public Map<Coordinate, Token> board() {
        return board;
    }

    @Override
    public Map<Team, List<Coordinate>> rings() {
        return null;
    }

    @Override
    public List<Set<Coordinate>> lines() {
        return null;
    }

    @Override
    public Team turn() {
        return null;
    }

    @Override
    public List<Set<Coordinate>> getPawnsLines(Map<Coordinate, Token> board) {
        return null;
    }

    public boolean isInField(Coordinate c) {
        return false;
    }

    public Team winner() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(board, state.board) && Objects.equals(turn, state.turn) && Objects.equals(lines, state.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn, lines);
    }
}
