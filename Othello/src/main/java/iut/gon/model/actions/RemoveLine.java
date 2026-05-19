package iut.gon.model.actions;

public class RemoveLine {

    public Set<Coordinate> line;
    public Coordinate ring;

    public Removeline(Set<Coordinate> line, Coordinate ring){
        this.line = line;
        this.ring = ring;
    }

    public Set<Coordinate> getLine() {
        return line;
    }

    public void setLine(Set<Coordinate> line) {
        this.line = line;
    }

    public Coordinate getRing() {
        return ring;
    }

    public void setRing(Coordinate ring) {
        this.ring = ring;
    }
}
