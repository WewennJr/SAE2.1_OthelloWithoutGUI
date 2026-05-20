package iut.gon.Coordinate.test;

import static org.junit.jupiter.api.Assertions.*;

import iut.gon.Coordinate.Point;

import org.junit.jupiter.api.Test;

public class PointTest {

    @Test
    void testConstructorAndAccessors() {
        Point p = new Point(5, -3);
       
        assertEquals(5, p.x());
        assertEquals(-3, p.y());
    }

    @Test
    void testToString() {
        Point p = new Point(10, 20);
        
        // On vérifie le format exact
        assertEquals("(10,20)", p.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Point p1 = new Point(1, 2);
        Point p2 = new Point(1, 2);
        Point p3 = new Point(3, 4);

        assertEquals(p1, p2, "Deux points avec les mêmes valeurs doivent être égaux");
        assertNotEquals(p1, p3);
        
        assertEquals(p1.hashCode(), p2.hashCode());
    }//ça fonctionne ?
}
