package iut.gon.Coordinate;

public record Point(int x, int y) {
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}
}
