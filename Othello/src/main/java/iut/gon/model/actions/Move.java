package iut.gon.model.actions;

public class Move {

    public Coordinate from;
    public Coordinate to;

    public Move(Coordinate from, Coordinate to){
        this.from = from;
        this.to = to;
    }

    public Coordinate getFrom() {
        return from;
    }

    public void setFrom(Coordinate from) {

        this.from = from;
    }

    public Coordinate getTo() {
        return to;
    }

    public void setTo(Coordinate to) {
        this.to = to;
    }

    public void doMove() throws IllegalArgumentException, IndexOutOfBoundsException, RuntimeException
        if (from.getRing() == null) {
            throw new IllegalArgumentException("Pas d'anneau à la position de départ");
        }if (to.getCoordinate() == null){
            throw new IndeOutOfBoundsException("La coordonné donné est hors terrain");
        }if (from.getCoordinate() == )
    }

}























