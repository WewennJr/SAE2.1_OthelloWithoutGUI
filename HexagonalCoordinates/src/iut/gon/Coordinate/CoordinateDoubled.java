package iut.gon.Coordinate;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class CoordinateDoubled extends Coordinate{
	private int x;
	private int y;
	public CoordinateDoubled(int y, int x ) {
		this.x = x;
		this.y = y;
	}
	
	@Override
    public String toString() {
        return "[" + y + ", " + x + "]";
	} 
	
	@Override
	public Point to2DCoordinate() {
		return new Point(this.x, this.y);
	}

	@Override
	public Coordinate toDir(Mode mode, Direction direction) {
		return switch(direction){
			case N -> this.N(mode);
			case S -> this.S(mode);
			case E -> this.E(mode);
			case O -> this.O(mode);
			case NE -> this.NE(mode);
			case NO -> this.NO(mode);
			case SE -> this.SE(mode);
			case SO -> this.SO(mode);
		};
	}

	@Override
	public List<Coordinate> getNeighbors(Mode mode) {
		List<Coordinate> neighbors = new ArrayList<>();
		if (mode == Mode.POINTY) {
			neighbors.add(NO(mode));
			neighbors.add(NE(mode));
			neighbors.add(O(mode));
			neighbors.add(E(mode));
			neighbors.add(SO(mode));
			neighbors.add(SE(mode));
		}
		else {
			neighbors.add(N(mode));
			neighbors.add(NE(mode));
			neighbors.add(NO(mode));
			neighbors.add(S(mode));
			neighbors.add(SO(mode));
			neighbors.add(SE(mode));
		}	
		return neighbors;
	}

	@Override
	public List<Coordinate> between(Mode mode, Coordinate to) throws DifferentAxisException{
		if (!(to instanceof CoordinateDoubled)){
			throw new IllegalArgumentException("Type de coordonnée imcompatible");
		}
		
		CoordinateDoubled target = (CoordinateDoubled) to;
		
		int deltaY = target.y - this.y;
		int deltaX = target.x - this.x;
		
		boolean isHorizontal = (deltaY == 0 && mode == Mode.POINTY);
		boolean isVertical = (deltaX == 0 && mode == Mode.FLAT);
		boolean isDiagonal = (Math.abs(deltaY) == Math.abs(deltaX));
		
		if (!isHorizontal && !isVertical && !isDiagonal) {
			throw new DifferentAxisException("Les coordonnées ne sont pas sur le même axe = problème");
		}
		/*On initialise la taille des pas en fonction de si c'est Horizontale
		,Vertical ou Diagonal*/
		int stepY = 0;
		int stepX = 0;
		
		if (isHorizontal) {
			if (deltaX > 0) {
				stepX = 2;
			}else {
				stepX =-2;
			}
		}else if (isVertical) {
			if (deltaY > 0) {
				stepY = 2;
			}else {
				stepY =-2;
			}
		}else {
			if (deltaX > 0) {
				stepX = 1;
			}else {
				stepX =-1;
			}
			if (deltaY > 0) {
				stepY = 1;
			}else {
				stepY =-1;
			}
		}
		
		//on initialise la liste des coordonnés entre les 2 coordonnées
		List<Coordinate> path = new ArrayList<>();
		int currentY = this.y + stepY;
		int currentX = this.x + stepX;
		
		while (currentY != target.y || currentX != target.x) {
			path.add(new CoordinateDoubled(currentY, currentX));
			currentY += stepY;
			currentX += stepX;
		}
		return path;
		
	}

	@Override
	public Coordinate NO(Mode mode) {
		return new CoordinateDoubled(this.y-1,this.x-1);
	}

	@Override
	public Coordinate NE(Mode mode) {
		return new CoordinateDoubled(this.y-1,this.x+1);
	}

	@Override
	public Coordinate E(Mode mode) {
		if (mode == Mode.POINTY) {
			return new CoordinateDoubled(this.y,this.x+2);
		}
		else {
			throw new InvalidParameterException("La direction EST n'existe pas en mode FLAT");
		}
	}

	@Override
	public Coordinate SE(Mode mode) {
		return new CoordinateDoubled(this.y+1,this.x+1);
	}

	@Override
	public Coordinate SO(Mode mode) {
		return new CoordinateDoubled(this.y+1,this.x-1);
	}

	@Override
	public Coordinate O(Mode mode) {
		if (mode == Mode.POINTY) {
			return new CoordinateDoubled(this.y,this.x-2);
		}
		else {
			throw new InvalidParameterException("La direction OUEST n'existe pas en mode FLAT");
		}
	}

	@Override
	public Coordinate S(Mode mode) {
		if (mode == Mode.FLAT) {
			return new CoordinateDoubled(this.y+2,this.x);
		}
		else {
			throw new InvalidParameterException("La direction SUD n'existe pas en Mode POINTY");
		}
	}

	@Override
	public Coordinate N(Mode mode) {
		if (mode == Mode.FLAT) {
			return new CoordinateDoubled(this.y-2,this.x);
		}
		else {
			throw new InvalidParameterException("La direciton NORD n'existe pas en Mode POINTY");
		}
	}
	
}
