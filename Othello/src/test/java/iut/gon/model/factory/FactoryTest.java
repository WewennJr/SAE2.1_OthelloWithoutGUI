package iut.gon.model.factory;

import iut.gon.model.Team;
import iut.gon.model.state.IState;
import iut.gon.model.tokens.Pawn;
import iut.gon.model.tokens.Ring;
import iut.gon.model.tokens.Token;
import iut.gon.Coordinate.Coordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FactoryTest {

    private FactoryCube    factoryCube;
    private FactoryDoubled factoryDoubled;

    @BeforeEach
    public void setUp() {
        factoryCube    = new FactoryCube();
        factoryDoubled = new FactoryDoubled();
    }

    @Test
    public void cube_emptyState_aucunToken() {
        IState state = factoryCube.emptyState();
        for (Token t : state.board().values()) {
            assertNull(t);
        }
    }

    @Test
    public void cube_emptyState_plateauNonVide() {
        IState state = factoryCube.emptyState();
        assertFalse(state.board().isEmpty());
    }

    @Test
    public void cube_emptyState_aucunAnneau() {
        IState state = factoryCube.emptyState();
        assertTrue(state.rings().get(Team.BLACK).isEmpty());
        assertTrue(state.rings().get(Team.WHITE).isEmpty());
    }

    @Test
    public void cube_emptyState_aucuneLigne() {
        IState state = factoryCube.emptyState();
        assertTrue(state.lines().isEmpty());
    }

    @Test
    public void doubled_emptyState_aucunToken() {
        IState state = factoryDoubled.emptyState();
        for (Token t : state.board().values()) {
            assertNull(t);
        }
    }

    @Test
    public void doubled_emptyState_plateauNonVide() {
        IState state = factoryDoubled.emptyState();
        assertFalse(state.board().isEmpty());
    }

    @Test
    public void cube_testState_cinqAnneauxParEquipe() {
        IState state = factoryCube.testState();
        assertEquals(5, state.rings().get(Team.BLACK).size());
        assertEquals(5, state.rings().get(Team.WHITE).size());
    }

    @Test
    public void cube_testState_tourNoir() {
        IState state = factoryCube.testState();
        assertEquals(Team.BLACK, state.turn());
    }

    @Test
    public void cube_testState_pasDeVainqueur() {
        IState state = factoryCube.testState();
        assertNull(state.winner());
    }

    @Test
    public void cube_testState_pasDeLigne() {
        IState state = factoryCube.testState();
        assertTrue(state.lines().isEmpty());
    }

    @Test
    public void cube_stateForBlackLineTest_uneSeuleLigneNoire() {
        IState state = factoryCube.stateForBlackLineTest();
        List<Set<Coordinate>> lines = state.lines();
        assertEquals(1, lines.size());

        // Tous les pions de la ligne sont noirs
        Set<Coordinate> line = lines.get(0);
        for (Coordinate c : line) {
            Token t = state.board().get(c);
            assertInstanceOf(Pawn.class, t);
            assertEquals(Team.BLACK, t.getTeam());
        }
    }

    @Test
    public void cube_stateForBlackLineTest_ligneDe5Pions() {
        IState state = factoryCube.stateForBlackLineTest();
        Set<Coordinate> line = state.lines().get(0);
        assertEquals(5, line.size());
    }

    @Test
    public void cube_stateForWhiteLineTest_uneSeuleLigneBlanc() {
        IState state = factoryCube.stateForWhiteLineTest();
        List<Set<Coordinate>> lines = state.lines();
        assertEquals(1, lines.size());

        Set<Coordinate> line = lines.get(0);
        for (Coordinate c : line) {
            Token t = state.board().get(c);
            assertInstanceOf(Pawn.class, t);
            assertEquals(Team.WHITE, t.getTeam());
        }
    }

    @Test
    public void cube_stateForWhiteLineTest_tourBlanc() {
        IState state = factoryCube.stateForWhiteLineTest();
        assertEquals(Team.WHITE, state.turn());
    }

    @Test
    public void cube_doubleLineStateTest_deuxLignes() {
        IState state = factoryCube.doubleLineStateTest();
        List<Set<Coordinate>> lines = state.lines();
        assertEquals(2, lines.size());
    }

    @Test
    public void cube_doubleLineStateTest_uneNoire_uneBlanc() {
        IState state = factoryCube.doubleLineStateTest();
        List<Set<Coordinate>> lines = state.lines();

        int blackLines = 0;
        int whiteLines = 0;

        for (Set<Coordinate> line : lines) {
            Token sample = state.board().get(line.iterator().next());
            if (sample.getTeam() == Team.BLACK) blackLines++;
            else whiteLines++;
        }

        assertEquals(1, blackLines);
        assertEquals(1, whiteLines);
    }

    @Test
    public void doubled_stateForBlackLineTest_uneSeuleLigneNoire() {
        IState state = factoryDoubled.stateForBlackLineTest();
        List<Set<Coordinate>> lines = state.lines();
        assertEquals(1, lines.size());

        for (Coordinate c : lines.get(0)) {
            Token t = state.board().get(c);
            assertInstanceOf(Pawn.class, t);
            assertEquals(Team.BLACK, t.getTeam());
        }
    }

    @Test
    public void doubled_stateForWhiteLineTest_uneSeuleLigneBlanc() {
        IState state = factoryDoubled.stateForWhiteLineTest();
        List<Set<Coordinate>> lines = state.lines();
        assertEquals(1, lines.size());

        for (Coordinate c : lines.get(0)) {
            Token t = state.board().get(c);
            assertInstanceOf(Pawn.class, t);
            assertEquals(Team.WHITE, t.getTeam());
        }
    }

    @Test
    public void doubled_doubleLineStateTest_deuxLignes() {
        IState state = factoryDoubled.doubleLineStateTest();
        assertEquals(2, state.lines().size());
    }
}