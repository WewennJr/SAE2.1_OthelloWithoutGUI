package iut.gon.Coordinate.test;

import static org.junit.jupiter.api.Assertions.*;
import java.security.InvalidParameterException;
import java.util.List;
import org.junit.jupiter.api.Test;

import iut.gon.Coordinate.Coordinate;
import iut.gon.Coordinate.CoordinateCube;
import iut.gon.Coordinate.Mode;
import iut.gon.Coordinate.Direction;
import iut.gon.Coordinate.Point;

public class CoordinateCubeTest {

    @Test
    void testValidConstructors() {
        // Quantité : On teste plusieurs combinaisons valides (x + y + z = 0)
        assertNotNull(new CoordinateCube(0, 0, 0));
        assertNotNull(new CoordinateCube(1, -1, 0));
        assertNotNull(new CoordinateCube(-5, 2, 3));
        assertNotNull(new CoordinateCube(100, -50, -50));
    }

    @Test
    void testInvalidConstructorShouldThrowException() {
        // Poussé : On s'assure que le constructeur rejette les fausses coordonnées cubiques
        assertThrows(InvalidParameterException.class, () -> {
            new CoordinateCube(1, 1, 1); // 1 + 1 + 1 != 0
        });
        assertThrows(InvalidParameterException.class, () -> {
            new CoordinateCube(-1, -1, -1);
        });
    }

    @Test
    void testToStringFormat() {
        CoordinateCube cc1 = new CoordinateCube(0, 0, 0);
        CoordinateCube cc2 = new CoordinateCube(1, -2, 1);
        
        // Adapte les chaînes de caractères selon ton format exact (ex: "[1, -2, 1]")
        assertNotNull(cc1.toString());
        assertTrue(cc2.toString().contains("-2"));
    }

    @Test
    void testTo2DCoordinate() {
        CoordinateCube cc = new CoordinateCube(2, -3, 1);
        Point p = cc.to2DCoordinate();
        
        assertNotNull(p, "La conversion en Point 2D ne doit pas renvoyer null");
    }

    @Test
    void testToAllDirections() {
        CoordinateCube cc = new CoordinateCube(0, 0, 0);
        
        // Poussé : On teste TOUTES les directions du switch pour être sûr qu'aucune ne crash
        for (Direction dir : Direction.values()) {
            Coordinate moved = cc.toDir(Mode.FLAT, dir);
            assertNotNull(moved, "Le déplacement vers " + dir + " a échoué");
        }
    }

    @Test
    void testGetNeighbors() {
        CoordinateCube cc = new CoordinateCube(0, 0, 0);
        List<Coordinate> neighbors = cc.getNeighbors(Mode.FLAT);
        
        assertNotNull(neighbors);
        // Sur une grille hexagonale, un élément a structurellement 6 voisins
        assertEquals(6, neighbors.size(), "Une coordonnée hexagonale doit avoir exactement 6 voisins");
        
        // On s'assure qu'aucun voisin dans la liste n'est null
        for (Coordinate neighbor : neighbors) {
            assertNotNull(neighbor);
        }
    }
}