package iut.gon.model.tokens;

import iut.gon.model.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenTest {

    @Test
    public void pawnNoir_charRepr_retourneX() {
        Pawn p = new Pawn(Team.BLACK);
        assertEquals("x", p.charRepr());
    }

    @Test
    public void pawnBlanc_charRepr_retournePoing() {
        Pawn p = new Pawn(Team.WHITE);
        assertEquals(".", p.charRepr());
    }

    @Test
    public void pawnNoir_changeTeam_devientBlanc() {
        Pawn p = new Pawn(Team.BLACK);
        p.changeTeam();
        assertEquals(Team.WHITE, p.getTeam());
    }

    @Test
    public void pawnBlanc_changeTeam_devientNoir() {
        Pawn p = new Pawn(Team.WHITE);
        p.changeTeam();
        assertEquals(Team.BLACK, p.getTeam());
    }

    @Test
    public void pawn_clone_retourneNouvelleInstance() {
        Pawn p = new Pawn(Team.BLACK);
        Token clone = p.clone();
        assertNotSame(p, clone);
        assertEquals(p.getTeam(), clone.getTeam());
        assertInstanceOf(Pawn.class, clone);
    }

    @Test
    public void pawn_getTeam_retourneBonneEquipe() {
        Pawn p = new Pawn(Team.WHITE);
        assertEquals(Team.WHITE, p.getTeam());
    }

    @Test
    public void ringNoir_charRepr_retourneGrandO() {
        Ring r = new Ring(Team.BLACK);
        assertEquals("O", r.charRepr());
    }

    @Test
    public void ringBlanc_charRepr_retournePetitO() {
        Ring r = new Ring(Team.WHITE);
        assertEquals("o", r.charRepr());
    }

    @Test
    public void ring_clone_retourneNouvelleInstance() {
        Ring r = new Ring(Team.WHITE);
        Token clone = r.clone();
        assertNotSame(r, clone);
        assertEquals(r.getTeam(), clone.getTeam());
        assertInstanceOf(Ring.class, clone);
    }

    @Test
    public void ring_getTeam_retourneBonneEquipe() {
        Ring r = new Ring(Team.BLACK);
        assertEquals(Team.BLACK, r.getTeam());
    }

    @Test
    public void ring_setTeam_modifieEquipe() {
        Ring r = new Ring(Team.BLACK);
        r.setTeam(Team.WHITE);
        assertEquals(Team.WHITE, r.getTeam());
    }
}