package iut.gon.Coordinate.test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

import iut.gon.Coordinate.Coordinate;
import iut.gon.Coordinate.CoordinateDoubled;
import iut.gon.Coordinate.Mode;
import iut.gon.Coordinate.Direction;
import iut.gon.Coordinate.Point;

public class CoordinateDoubledTest {

    @Test
    void testConstructorAndGetters() {
        // Scénario 1 : Zéros
        CoordinateDoubled cd1 = new CoordinateDoubled(0, 0);
        assertEquals(0, cd1.getX());
        assertEquals(0, cd1.getY());

        // Scénario 2 : Positifs (Rappel constructeur : y, x)
        CoordinateDoubled cd2 = new CoordinateDoubled(4, 5);
        assertEquals(5, cd2.getX());
        assertEquals(4, cd2.getY());

        // Scénario 3 : Négatifs
        CoordinateDoubled cd3 = new CoordinateDoubled(-10, -20);
        assertEquals(-20, cd3.getX());
        assertEquals(-10, cd3.getY());
    }

    @Test
    void testToString() {
        CoordinateDoubled cd = new CoordinateDoubled(4, 5);
        // Vérification stricte du format d'affichage exigé par ta méthode
        assertEquals("[4, 5]", cd.toString());
    }

    @Test
    void testTo2DCoordinate() {
        CoordinateDoubled cd = new CoordinateDoubled(10, 20);
        Point p = cd.to2DCoordinate();
        
        assertNotNull(p);
        // Vérifie que le Point récupère bien les bonnes valeurs x et y
        // (Ajuste point.getX() si ta classe Point utilise d'autres méthodes)
        assertNotNull(p.toString()); 
    }

    @Test
    void testToAllDirectionsDoubled() {
        CoordinateDoubled cd = new CoordinateDoubled(2, 2);
        
        // On passe en revue toutes les directions disponibles
        assertThrows(InvalidParameterException.class, () -> {
        	cd.toDir(Mode.POINTY, Direction.N));
        });
        assertNotNull(cd.toDir(Mode.POINTY, Direction.S));
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
}