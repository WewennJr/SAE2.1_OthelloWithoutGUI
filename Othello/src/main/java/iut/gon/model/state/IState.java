package iut.gon.model.state;

import iut.gon.model.Team;
import iut.gon.model.actions.Move;
import iut.gon.model.actions.RemoveLine;
import iut.gon.model.tokens.Token;

public interface IState {

    public IState move(Move move);
    public IState removeLine(RemoveLine removeLine);
    public Set<Coordinate> availableMove(Coordinate from);
    public Map<Coordinate, Token> board();
    public Map<Team, List<Coordinate>> rings();
    public List<Set<Coordinate>> lines();
    public Team turn();
    public List<Set<Coordinate>> getPawnsLines(Map<Coordinate, Token> board);
}
