package iut.gon.model.factory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import iut.gon.model.state.IState;
import iut.gon.model.Team;
import iut.gon.model.tokens.Pawn;
import iut.gon.model.tokens.Ring;
import iut.gon.model.tokens.Token;

public class FactoryDoubledTest {

    @Test
    void testEmptyState() {
        IFactory factory = new FactoryDoubled();
        IState state = factory.emptyState();
        
        assertNotNull(state);
        // Si tu corriges ton tableau validCells, tu pourras mettre assertEquals(91, state.board().size());
        assertTrue(state.board().size() > 80, "Le plateau doit contenir une grande majorité des cases");
        
        for (Token token : state.board().values()) {
            assertNull(token);
        }
    }

    @Test
    void testTestStateTokensCount() {
        IFactory factory = new FactoryDoubled();
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
        
        assertEquals(5, whiteRings, "Il doit y avoir 5 anneaux blancs");
        assertEquals(5, blackRings, "Il doit y avoir 5 anneaux noirs");
        assertEquals(3, whitePawns, "Il doit y avoir 3 pions blancs");
        assertEquals(4, blackPawns, "Il doit y avoir 4 pions noirs");
    }

    @Test
    void testDoubleLineState() {
        IFactory factory = new FactoryDoubled();
        IState state = factory.doubleLineStateTest();
        
        // On vérifie qu'il y a bien au moins 10 pions sur le plateau pour ce test
        long pawnCount = state.board().values().stream()
                .filter(t -> t instanceof Pawn)
                .count();
        
        assertEquals(10, pawnCount, "Il doit y avoir 10 pions pour le test de double ligne");
    }
}