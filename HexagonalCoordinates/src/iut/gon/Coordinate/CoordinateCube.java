package iut.gon.Coordinate;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CoordinateCube extends Coordinate{
	private final int q;
	private final int r;
	private final int s;
	
	public CoordinateCube(int q, int r, int s) {
		super();
		if(q+r+s != 0) {
			throw new IllegalArgumentException("Coordonnées cubiques invalides : q + r + s = 0. Données reçu : " +r+ " +"+q+" +" +s);
		}
		this.q = q;
		this.r = r;
		this.s = s;
	}
	
	public int getQ() { return q; }
	public int getS() { return s; }
	public int getR() { return r; }
	
	@Override
	public Coordinate toDir(Mode mode, Direction direction) {
		if (mode == Mode.POINTY) {
            // En Pointy, les hexagones se touchent sur les côtés (Est / Ouest)
            return switch (direction) {
            case E  -> new CoordinateCube(q + 1, r, s - 1); // voisin de droite
            case O  -> new CoordinateCube(q - 1, r, s + 1); //voisin de gauche
            case NE -> new CoordinateCube(q + 1, r - 1, s); // voisin de diagonal droite/haut
            case SO -> new CoordinateCube(q - 1, r + 1, s); // voisin de diagonal gauche/bas
            case SE -> new CoordinateCube(q, r + 1, s - 1); //voisin de diagonal droite/bas
            case NO -> new CoordinateCube(q, r - 1, s + 1); // voisin de diagonal gauche/haut
            case N, S -> throw new InvalidParameterException("Les directions Nord et Sud n'existent pas en mode POINTY");
            };
        } else {
            // En Flat, les hexagones se touchent en haut et en bas (Nord / Sud)
            return switch (direction) {
            case N  -> new CoordinateCube(q, r - 1, s + 1); //voisin de gauche/haut
            case S  -> new CoordinateCube(q, r + 1, s - 1);//voisin de droite/bas
            case NE -> new CoordinateCube(q + 1, r - 1, s);//voisin de droite/haut
            case SO -> new CoordinateCube(q - 1, r + 1, s);//voisin de gauche/bas
            case SE -> new CoordinateCube(q + 1, r, s - 1);//de droite
            case NO -> new CoordinateCube(q - 1, r, s + 1);//de gauche
            case E, O -> throw new InvalidParameterException("Les directions Est et Ouest n'existent pas en mode FLAT");
            };
        }
	}

	@Override
	public Point to2DCoordinate() {
	    // Conversion d'un système Cube (centré en 0,0,0) 
	    int x = 2 * this.q + this.r + 9;
	    int y = this.r + 5;
	    return new Point(x, y);
	}

	@Override
	public List<Coordinate> getNeighbors(Mode mode) {
		List<Coordinate> neighbors = new ArrayList<>();
		if (mode == Mode.POINTY) { // POINTY
            neighbors.add(this.E(mode));
            neighbors.add(this.O(mode));
            neighbors.add(this.NE(mode));
            neighbors.add(this.NO(mode));
            neighbors.add(this.SE(mode));
            neighbors.add(this.SO(mode));
        } else { // FLAT
            neighbors.add(this.N(mode));
            neighbors.add(this.S(mode));
            neighbors.add(this.NE(mode));
            neighbors.add(this.NO(mode));
            neighbors.add(this.SE(mode));
            neighbors.add(this.SO(mode));
        }
        return neighbors;
	}

	@Override
	public List<Coordinate> between(Mode mode, Coordinate to) throws DifferentAxisException {

	    if (!(to instanceof CoordinateCube toCube)) {
	        throw new DifferentAxisException("'to' doit être une instance de CoordinateCube !");
	    }

	    if (!(this.getQ() == toCube.getQ() || this.getR() == toCube.getR() || this.getS() == toCube.getS())) {
	        throw new DifferentAxisException("Les cases ne sont pas sur le même axe!");
	    }

	    List<Coordinate> listCoordinate = new ArrayList<>();

	    if (this.equals(to)) { return listCoordinate; }

	    int direction = 1;

	    if (this.getQ() == toCube.getQ()) {

	        if( this.getR() > toCube.getR()) { direction = -1; }

	        for(int posR = this.getR() + direction; posR != toCube.getR(); posR += direction) {
	            listCoordinate.add(new CoordinateCube(this.getQ(), posR, -this.getQ() - posR));
	        }
	    }

	    else if (this.getR() == toCube.getR()) {

	        if( this.getS() > toCube.getS()) { direction = -1; }


	        for(int posS = this.getS() + direction; posS != toCube.getS(); posS += direction) {

	            // q = -r - s
	            listCoordinate.add(new CoordinateCube(-this.getR() - posS, this.getR(), posS));
	        }
	    }
	    else {
	        if( this.getQ() > toCube.getQ()) { direction = -1; }

	        for(int posQ = this.getQ() + direction; posQ != toCube.getQ(); posQ += direction) {
	            listCoordinate.add(new CoordinateCube(posQ, -posQ - this.getS(), this.getS()));
	        }
	    }

	    return listCoordinate;
	}
	
	// Les 8 méthodes qui renvoient vers la logique centrale ---
    @Override
    public Coordinate NO(Mode mode) { return this.toDir(mode, Direction.NO); }
    
    @Override
    public Coordinate N(Mode mode)  { return this.toDir(mode, Direction.N); }
    
    @Override
    public Coordinate NE(Mode mode) { return this.toDir(mode, Direction.NE); }
    
    @Override
    public Coordinate E(Mode mode)  { return this.toDir(mode, Direction.E); }
    
    @Override
    public Coordinate SE(Mode mode) { return this.toDir(mode, Direction.SE); }
    
    @Override
    public Coordinate S(Mode mode)  { return this.toDir(mode, Direction.S); }
    
    @Override
    public Coordinate SO(Mode mode) { return this.toDir(mode, Direction.SO); }
    
    @Override
    public Coordinate O(Mode mode)  { return this.toDir(mode, Direction.O); }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordinateCube that = (CoordinateCube) o;
        return q == that.q && r == that.r && s == that.s;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r, s);
    }
}
