package iut.gon.model.state;

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
