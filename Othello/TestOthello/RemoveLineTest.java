
package iut.gon.model.actions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import iut.gon.Coordinate.Coordinate;
import iut.gon.Coordinate.CoordinateCube;

public class RemoveLineTest {

    @Test
    void testConstructorAndGetters() {
        Coordinate ring = new CoordinateCube(0, 0, 0);
        Set<Coordinate> line = new HashSet<>();
        line.add(new CoordinateCube(1, -1, 0));
        line.add(new CoordinateCube(2, -2, 0));
        
        RemoveLine removeLine = new RemoveLine(line, ring);
        
        assertEquals(line, removeLine.getLine());
        assertEquals(ring, removeLine.getRing());
    }

    @Test
    void testSetters() {
        RemoveLine removeLine = new RemoveLine(null, null);
        
        Coordinate ring = new CoordinateCube(0, 1, -1);
        Set<Coordinate> line = new HashSet<>();
        line.add(new CoordinateCube(0, -1, 1));
        
        removeLine.setLine(line);
        removeLine.setRing(ring);
        
        assertEquals(line, removeLine.getLine());
        assertEquals(ring, removeLine.getRing());
    }
}