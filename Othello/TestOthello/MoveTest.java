package iut.gon.model.actions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import iut.gon.Coordinate.Coordinate;
import iut.gon.Coordinate.CoordinateCube;

public class MoveTest {

    @Test
    void testConstructorAndGetters() {
        Coordinate from = new CoordinateCube(0, 0, 0);
        Coordinate to = new CoordinateCube(1, -1, 0);
        
        Move move = new Move(from, to);
        
        assertEquals(from, move.getFrom());
        assertEquals(to, move.getTo());
    }

    @Test
    void testSetters() {
        Move move = new Move(null, null);
        
        Coordinate from = new CoordinateCube(1, 1, -2);
        Coordinate to = new CoordinateCube(-1, 2, -1);
        
        move.setFrom(from);
        move.setTo(to);
        
        assertEquals(from, move.getFrom());
        assertEquals(to, move.getTo());
    }
}