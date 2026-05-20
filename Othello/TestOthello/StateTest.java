package iut.gon.model.state;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import iut.gon.Coordinate.Coordinate;
import iut.gon.Coordinate.CoordinateCube;
import iut.gon.Coordinate.Mode;
import iut.gon.model.Team;
import iut.gon.model.actions.Move;
import iut.gon.model.factory.FactoryCube;
import iut.gon.model.factory.IFactory;
import iut.gon.model.tokens.Pawn;
import iut.gon.model.tokens.Ring;

import java.util.HashMap;
import java.util.Map;

public class StateTest {

    @Test
    void testWinner() {
        // On simule une partie où les Noirs ont retiré 3 anneaux
        Map<Team, Integer> removedRings = new HashMap<>();
        removedRings.put(Team.BLACK, 3);
        removedRings.put(Team.WHITE, 1);
        
        State state = new State(new HashMap<>(), Team.BLACK, removedRings, Mode.FLAT);
        
        assertEquals(Team.BLACK, state.winner(), "Les Noirs doivent gagner avec 3 anneaux");
    }

    @Test
    void testToggleToken() {
        IFactory factory = new FactoryCube();
        IState emptyState = factory.emptyState();
        Coordinate c = new CoordinateCube(0, 0, 0);
        
        // 1. Ajouter un pion
        IState state1 = emptyState.toggleToken(c, Pawn.class, Team.BLACK);
        assertTrue(state1.board().get(c) instanceof Pawn);
        assertEquals(Team.BLACK, state1.board().get(c).getTeam());
        
        // 2. Le supprimer (en recliquant dessus avec les mêmes paramètres)
        IState state2 = state1.toggleToken(c, Pawn.class, Team.BLACK);
        assertNull(state2.board().get(c), "Le pion aurait dû être supprimé");
    }

    @Test
    void testMoveRing() {
        // On va se créer un mini-scénario avec la Factory
        IFactory factory = new FactoryCube();
        IState state = factory.emptyState();
        
        Coordinate start = new CoordinateCube(0, 0, 0);
        Coordinate middle = new CoordinateCube(0, -1, 1);
        Coordinate end = new CoordinateCube(0, -2, 2);
        
        // On place un anneau noir au centre et un pion blanc à côté
        state = state.toggleToken(start, Ring.class, Team.BLACK);
        state = state.toggleToken(middle, Pawn.class, Team.WHITE);
        
        // Le joueur noir déplace son anneau par-dessus le pion blanc
        Move move = new Move(start, end);
        IState newState = state.move(move);
        
        // --- Vérifications ---
        // 1. L'anneau noir est bien à l'arrivée
        assertTrue(newState.board().get(end) instanceof Ring);
        assertEquals(Team.BLACK, newState.board().get(end).getTeam());
        
        // 2. L'ancienne place contient maintenant un pion noir
        assertTrue(newState.board().get(start) instanceof Pawn);
        assertEquals(Team.BLACK, newState.board().get(start).getTeam());
        
        // 3. Le pion blanc sauté a été retourné (il est devenu noir)
        assertTrue(newState.board().get(middle) instanceof Pawn);
        assertEquals(Team.BLACK, newState.board().get(middle).getTeam());
        
        // 4. C'est au tour des blancs
        assertEquals(Team.WHITE, newState.turn());
    }

    @Test
    void testMoveExceptionIfNoRing() {
        IFactory factory = new FactoryCube();
        IState state = factory.emptyState();
        
        Coordinate c1 = new CoordinateCube(0, 0, 0);
        Coordinate c2 = new CoordinateCube(1, -1, 0);
        
        // On tente de bouger depuis une case vide
        assertThrows(IllegalArgumentException.class, () -> {
            state.move(new Move(c1, c2));
        });
    }
}