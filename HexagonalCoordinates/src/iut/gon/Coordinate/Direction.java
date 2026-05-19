package iut.gon.Coordinate;

public enum Direction {
	NO,N,NE,E,SE,S,SO,O;
	
	public Direction opposite() {
	    switch (this) {
	        case NO: return SE;
	        case N:  return S;
	        case NE: return SO;
	        case E:  return O;
	        case SE: return NO;
	        case S:  return N;
	        case SO: return NE;
	        case O:  return E;
	        default: throw new IllegalStateException("Direction inattendue : " + this);
	    }
	}
}
