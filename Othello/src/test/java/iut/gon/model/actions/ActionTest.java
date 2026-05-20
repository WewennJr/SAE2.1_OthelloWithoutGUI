package iut.gon.model.actions;

import iut.gon.Coordinate.CoordinateCube;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ActionTest {

    @Test
    public void move_getFrom_retourneBonneCoordonnee() {
        CoordinateCube from = new CoordinateCube(0, 0, 0);
        CoordinateCube to   = new CoordinateCube(1, -1, 0);
        Move move = new Move(from, to);
        assertEquals(from, move.getFrom());
    }

    @Test
    public void move_getTo_retourneBonneCoordonnee() {
        CoordinateCube from = new CoordinateCube(0, 0, 0);
        CoordinateCube to   = new CoordinateCube(1, -1, 0);
        Move move = new Move(from, to);
        assertEquals(to, move.getTo());
    }

    @Test
    public void move_setFrom_modifieCoordonnee() {
        CoordinateCube from = new CoordinateCube(0, 0, 0);
        CoordinateCube to   = new CoordinateCube(1, -1, 0);
        CoordinateCube newFrom = new CoordinateCube(2, -1, -1);
        Move move = new Move(from, to);
        move.setFrom(newFrom);
        assertEquals(newFrom, move.getFrom());
    }

    @Test
    public void move_setTo_modifieCoordonnee() {
        CoordinateCube from = new CoordinateCube(0, 0, 0);
        CoordinateCube to   = new CoordinateCube(1, -1, 0);
        CoordinateCube newTo = new CoordinateCube(3, -2, -1);
        Move move = new Move(from, to);
        move.setTo(newTo);
        assertEquals(newTo, move.getTo());
    }

    @Test
    public void move_estUneAction() {
        Move move = new Move(new CoordinateCube(0, 0, 0), new CoordinateCube(1, -1, 0));
        assertInstanceOf(Action.class, move);
    }

    @Test
    public void removeLine_getLine_retourneBonneLigne() {
        Set<CoordinateCube> line = new HashSet<>();
        line.add(new CoordinateCube(0, 0, 0));
        line.add(new CoordinateCube(1, 0, -1));
        line.add(new CoordinateCube(2, 0, -2));
        line.add(new CoordinateCube(3, 0, -3));
        line.add(new CoordinateCube(4, 0, -4));
        CoordinateCube ring = new CoordinateCube(5, 0, -5);

        RemoveLine rl = new RemoveLine(new HashSet<>(line), ring);
        assertEquals(line, rl.getLine());
    }

    @Test
    public void removeLine_getRing_retourneBonneCoordonnee() {
        Set<CoordinateCube> line = new HashSet<>();
        line.add(new CoordinateCube(0, 0, 0));
        line.add(new CoordinateCube(1, 0, -1));
        line.add(new CoordinateCube(2, 0, -2));
        line.add(new CoordinateCube(3, 0, -3));
        line.add(new CoordinateCube(4, 0, -4));
        CoordinateCube ring = new CoordinateCube(5, 0, -5);

        RemoveLine rl = new RemoveLine(new HashSet<>(line), ring);
        assertEquals(ring, rl.getRing());
    }

    @Test
    public void removeLine_setRing_modifieCoordonnee() {
        Set<CoordinateCube> line = new HashSet<>();
        line.add(new CoordinateCube(0, 0, 0));
        line.add(new CoordinateCube(1, 0, -1));
        line.add(new CoordinateCube(2, 0, -2));
        line.add(new CoordinateCube(3, 0, -3));
        line.add(new CoordinateCube(4, 0, -4));
        CoordinateCube ring    = new CoordinateCube(5, 0, -5);
        CoordinateCube newRing = new CoordinateCube(-1, 0, 1);

        RemoveLine rl = new RemoveLine(new HashSet<>(line), ring);
        rl.setRing(newRing);
        assertEquals(newRing, rl.getRing());
    }

    @Test
    public void removeLine_estUneAction() {
        RemoveLine rl = new RemoveLine(new HashSet<>(), new CoordinateCube(0, 0, 0));
        assertInstanceOf(Action.class, rl);
    }
}