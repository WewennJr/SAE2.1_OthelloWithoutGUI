package iut.gon.model.factory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import iut.gon.model.state.IState;
import iut.gon.model.Team;
import iut.gon.model.tokens.Pawn;
import iut.gon.model.tokens.Ring;
import iut.gon.model.tokens.Token;

public class FactoryCubeTest {

    @Test
    void testEmptyState() {
        IFactory factory = new FactoryCube();
        IState state = factory.emptyState();
        
        assertNotNull(state);
        // Un hexagone de rayon 5 possède exactement 91 cases
        assertEquals(91, state.board().size());
        
        // On vérifie que toutes les cases sont bien vides (null)
        for (Token token : state.board().values()) {
            assertNull(token);
        }
    }

    @Test
    void testTestStateTokensCount() {
        IFactory factory = new FactoryCube();
        IState state = factory.testState();
        
        int whiteRings = 0;
        int blackRings = 0;
        int whitePawns = 0;
        int blackPawns = 0;
        
        for (Token token : state.board().values()) {
            if (token instanceof Ring) {
                if (token.getTeam() == Team.WHITE) whiteRings++;
                else blackRings++;
            } else if (token instanceof Pawn) {
                if (token.getTeam() == Team.WHITE) whitePawns++;
                else blackPawns++;
            }
        }
        
        // D'après ton code d'initialisation, on s'attend à ces quantités :
        assertEquals(5, whiteRings, "Il doit y avoir 5 anneaux blancs");
        assertEquals(5, blackRings, "Il doit y avoir 5 anneaux noirs");
        assertEquals(3, whitePawns, "Il doit y avoir 3 pions blancs");
        assertEquals(4, blackPawns, "Il doit y avoir 4 pions noirs");
    }

    @Test
    void testLinesAreCorrectlyCreated() {
        IFactory factory = new FactoryCube();
        IState stateBlack = factory.stateForBlackLineTest();
        IState stateWhite = factory.stateForWhiteLineTest();
        
        // On vérifie juste qu'on a bien généré les états et que le tour est bon
        assertEquals(Team.BLACK, stateBlack.turn());
        assertEquals(Team.WHITE, stateWhite.turn());
    }
}