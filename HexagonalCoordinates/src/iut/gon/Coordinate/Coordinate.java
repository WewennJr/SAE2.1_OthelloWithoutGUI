package iut.gon.Coordinate;


import java.util.List;

public abstract class Coordinate {
	public abstract Point to2DCoordinate();
	
	public abstract Coordinate toDir(Mode mode, Direction direction); 
	
	public abstract List<Coordinate> getNeighbors(Mode mode);
	public abstract List<Coordinate> between(Mode mode, Coordinate to);
	
	public abstract Coordinate NO (Mode mode);
	public abstract Coordinate NE (Mode mode);
	public abstract Coordinate E (Mode mode);
	public abstract Coordinate SE (Mode mode);
	public abstract Coordinate SO (Mode mode);
	public abstract Coordinate O (Mode mode);
	public abstract Coordinate S (Mode mode);
	public abstract Coordinate N (Mode mode);
	
}
