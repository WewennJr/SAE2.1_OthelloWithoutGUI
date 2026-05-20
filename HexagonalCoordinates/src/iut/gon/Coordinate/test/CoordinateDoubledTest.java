package iut.gon.Coordinate.test;

import static org.junit.jupiter.api.Assertions.*;
import java.security.InvalidParameterException;
import java.util.List;
import org.junit.jupiter.api.Test;

import iut.gon.Coordinate.Coordinate;
import iut.gon.Coordinate.CoordinateDoubled;
import iut.gon.Coordinate.DifferentAxisException;
import iut.gon.Coordinate.Mode;
import iut.gon.Coordinate.Direction;
import iut.gon.Coordinate.Point;

public class CoordinateDoubledTest {

    @Test
    void testConstructorAndGetters() {
        CoordinateDoubled cd1 = new CoordinateDoubled(0, 0);
        assertEquals(0, cd1.getX());
        assertEquals(0, cd1.getY());

        CoordinateDoubled cd2 = new CoordinateDoubled(4, 5);
        assertEquals(5, cd2.getX());
        assertEquals(4, cd2.getY());

        CoordinateDoubled cd3 = new CoordinateDoubled(-10, -20);
        assertEquals(-20, cd3.getX());
        assertEquals(-10, cd3.getY());
    }

    @Test
    void testToString() {
        CoordinateDoubled cd = new CoordinateDoubled(4, 5);
        assertEquals("[4, 5]", cd.toString());
    }

    @Test
    void testTo2DCoordinate() {
        CoordinateDoubled cd = new CoordinateDoubled(10, 20);
        Point p = cd.to2DCoordinate();
        
        assertNotNull(p);
        // C'est plus précis de tester directement les valeurs du Point
        assertEquals(20, p.x()); 
        assertEquals(10, p.y()); 
    }

    @Test
    void testToAllDirectionsDoubled() {
        CoordinateDoubled cd = new CoordinateDoubled(2, 2);
        
        // En POINTY, Nord et Sud sont interdits
        assertThrows(InvalidParameterException.class, () -> {
            cd.toDir(Mode.POINTY, Direction.N);
        });
        assertThrows(InvalidParameterException.class, () -> {
            cd.toDir(Mode.POINTY, Direction.S);
        });
        
        // Les autres fonctionnent
        assertNotNull(cd.toDir(Mode.POINTY, Direction.E));
        assertNotNull(cd.toDir(Mode.POINTY, Direction.O));
        assertNotNull(cd.toDir(Mode.POINTY, Direction.NE));
        assertNotNull(cd.toDir(Mode.POINTY, Direction.NO));
        assertNotNull(cd.toDir(Mode.POINTY, Direction.SE));
        assertNotNull(cd.toDir(Mode.POINTY, Direction.SO));
    }

    @Test
    void testGetNeighborsCountAndValidity() {
        CoordinateDoubled cd = new CoordinateDoubled(3, 3);
        List<Coordinate> neighbors = cd.getNeighbors(Mode.POINTY);
        
        assertNotNull(neighbors);
        assertEquals(6, neighbors.size(), "Doit retourner exactement 6 voisins");
    }

    @Test
    void testBetween() throws DifferentAxisException {
        CoordinateDoubled depart = new CoordinateDoubled(0, 0);
        CoordinateDoubled arrivee = new CoordinateDoubled(0, 6); // Même ligne, saut de 6 sur X (Mode Pointy)
        
        List<Coordinate> path = depart.between(Mode.POINTY, arrivee);
        
        // Sauts de 2 : il y a (0,2) et (0,4) entre les deux
        assertEquals(2, path.size());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordinateDoubled that = (CoordinateDoubled) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(y, x);
    }
}