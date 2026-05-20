package iut.gon.model.tokens;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import iut.gon.model.Team;

public class TokenTest {

    @Test
    void testPawn() {
        Pawn pawn = new Pawn(Team.BLACK);
        assertEquals(Team.BLACK, pawn.getTeam());
        assertEquals("x", pawn.charRepr());
        
        pawn.changeTeam();
        
        assertEquals(Team.WHITE, pawn.getTeam());
        assertEquals(".", pawn.charRepr());
    }

    @Test
    void testRing() {
        Ring ring = new Ring(Team.WHITE);
        assertEquals(Team.WHITE, ring.getTeam());
        assertEquals("o", ring.charRepr());
        
        ring.setTeam(Team.BLACK);
        assertEquals(Team.BLACK, ring.getTeam());
        assertEquals("O", ring.charRepr());
    }

    @Test
    void testClones() {
        Pawn pawn = new Pawn(Team.BLACK);
        Token clonedPawn = pawn.clone();
        
        assertNotSame(pawn, clonedPawn); // Vérifie que c'est bien une nouvelle instance en mémoire
        assertEquals(Team.BLACK, clonedPawn.getTeam());
        assertTrue(clonedPawn instanceof Pawn);
        
        Ring ring = new Ring(Team.WHITE);
        Token clonedRing = ring.clone();
        
        assertNotSame(ring, clonedRing);
        assertEquals(Team.WHITE, clonedRing.getTeam());
        assertTrue(clonedRing instanceof Ring);
    }
}