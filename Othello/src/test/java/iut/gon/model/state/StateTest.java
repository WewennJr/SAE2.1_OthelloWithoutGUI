package iut.gon.model.state;

import iut.gon.Coordinate.Coordinate;
import iut.gon.Coordinate.CoordinateCube;
import iut.gon.model.Team;
import iut.gon.model.actions.Move;
import iut.gon.model.actions.RemoveLine;
import iut.gon.model.factory.FactoryCube;
import iut.gon.model.tokens.Pawn;
import iut.gon.model.tokens.Ring;
import iut.gon.model.tokens.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StateTest {

    private FactoryCube factory;

    // Coordonnées issues de testState (FactoryCube)
    // Anneaux noirs : (-2,0,2), (0,0,0), (0,1,-1), (0,2,-2), (1,-1,0)
    // Anneaux blancs : (-5,1,4), (-2,-2,4), (-2,-3,5), (2,-3,1), (3,-4,1)
    private final CoordinateCube ANNEAU_NOIR  = new CoordinateCube(0, 0, 0);
    private final CoordinateCube ANNEAU_BLANC = new CoordinateCube(-5, 1, 4);
    private final CoordinateCube HORS_TERRAIN = new CoordinateCube(99, -50, -49);
    private final CoordinateCube CENTRE       = new CoordinateCube(0, 0, 0);

    @BeforeEach
    public void setUp() {
        factory = new FactoryCube();
    }

    @Test
    public void move_anneau_seDeplace() {
        IState state = factory.testState();
        Set<Coordinate> destinations = state.availableMove(ANNEAU_NOIR);
        assertFalse(destinations.isEmpty());

        Coordinate to = destinations.iterator().next();
        IState next = state.move(new Move(ANNEAU_NOIR, to));

        assertInstanceOf(Ring.class, next.board().get(to));
        assertEquals(Team.BLACK, next.board().get(to).getTeam());
    }

    @Test
    public void move_posePionALanciennePosition() {
        IState state = factory.testState();
        Coordinate to = state.availableMove(ANNEAU_NOIR).iterator().next();

        IState next = state.move(new Move(ANNEAU_NOIR, to));

        assertInstanceOf(Pawn.class, next.board().get(ANNEAU_NOIR));
        assertEquals(Team.BLACK, next.board().get(ANNEAU_NOIR).getTeam());
    }

    @Test
    public void move_changeTour_siAucuneLigne() {
        IState state = factory.testState(); // tour = BLACK
        Coordinate to = state.availableMove(ANNEAU_NOIR).iterator().next();

        IState next = state.move(new Move(ANNEAU_NOIR, to));

        if (next.lines().isEmpty()) {
            assertEquals(Team.WHITE, next.turn());
        }
    }

    @Test
    public void move_immuabilite() {
        IState state = factory.testState();
        Coordinate to = state.availableMove(ANNEAU_NOIR).iterator().next();

        IState next = state.move(new Move(ANNEAU_NOIR, to));

        // L'état original est intact
        assertInstanceOf(Ring.class, state.board().get(ANNEAU_NOIR));
        assertNotSame(state, next);
    }

    @Test
    public void move_coordonneeDepart_horsChamp_leveIndexOutOfBounds() {
        IState state = factory.testState();
        assertThrows(IndexOutOfBoundsException.class, () ->
                state.move(new Move(HORS_TERRAIN, ANNEAU_NOIR))
        );
    }

    @Test
    public void move_coordonneeArrivee_horsChamp_leveIndexOutOfBounds() {
        IState state = factory.testState();
        assertThrows(IndexOutOfBoundsException.class, () ->
                state.move(new Move(ANNEAU_NOIR, HORS_TERRAIN))
        );
    }

    @Test
    public void move_pasDAnneau_leveIllegalArgument() {
        IState state = factory.emptyState();
        // Le centre est vide dans emptyState
        CoordinateCube vide = new CoordinateCube(0, 0, 0);
        CoordinateCube dest = new CoordinateCube(1, -1, 0);
        assertThrows(IllegalArgumentException.class, () ->
                state.move(new Move(vide, dest))
        );
    }

    @Test
    public void move_lignePresente_leveRuntimeException() {
        IState state = factory.stateForBlackLineTest();
        Coordinate from = state.rings().get(Team.BLACK).get(0);
        Coordinate to   = state.availableMove(from).iterator().next();
        assertThrows(RuntimeException.class, () ->
                state.move(new Move(from, to))
        );
    }

    @Test
    public void removeLine_supprimeLes5Pions() {
        IState state = factory.stateForBlackLineTest();
        Set<Coordinate> line = state.lines().get(0);
        Coordinate ring = state.rings().get(Team.BLACK).get(0);

        IState next = state.removeLine(new RemoveLine(line, ring));

        for (Coordinate c : line) {
            assertNull(next.board().get(c));
        }
    }

    @Test
    public void removeLine_supprimeAnneau() {
        IState state = factory.stateForBlackLineTest();
        Set<Coordinate> line = state.lines().get(0);
        Coordinate ring = state.rings().get(Team.BLACK).get(0);

        IState next = state.removeLine(new RemoveLine(line, ring));

        assertNull(next.board().get(ring));
    }

    @Test
    public void removeLine_diminueNombreAnneaux() {
        IState state = factory.stateForBlackLineTest();
        int before = state.rings().get(Team.BLACK).size();
        Set<Coordinate> line = state.lines().get(0);
        Coordinate ring = state.rings().get(Team.BLACK).get(0);

        IState next = state.removeLine(new RemoveLine(line, ring));

        assertEquals(before - 1, next.rings().get(Team.BLACK).size());
    }

    @Test
    public void removeLine_immuabilite() {
        IState state = factory.stateForBlackLineTest();
        Set<Coordinate> line = state.lines().get(0);
        Coordinate ring = state.rings().get(Team.BLACK).get(0);

        IState next = state.removeLine(new RemoveLine(line, ring));

        assertNotSame(state, next);
        assertFalse(state.lines().isEmpty()); // état original intact
    }

    @Test
    public void removeLine_sansLignePresente_leveRuntimeException() {
        IState state = factory.testState(); // pas de ligne
        Set<Coordinate> fausse = new HashSet<>();
        fausse.add(new CoordinateCube(0, 0, 0));
        assertThrows(RuntimeException.class, () ->
                state.removeLine(new RemoveLine(fausse, new CoordinateCube(0, 0, 0)))
        );
    }

    @Test
    public void removeLine_ligneTropCourte_leveRuntimeException() {
        IState state = factory.stateForBlackLineTest();
        // Seulement 3 cases au lieu de 5
        Set<Coordinate> courte = new HashSet<>();
        courte.add(new CoordinateCube(-4, 1, 3));
        courte.add(new CoordinateCube(-3, 1, 2));
        courte.add(new CoordinateCube(-2, 1, 1));
        Coordinate ring = state.rings().get(Team.BLACK).get(0);

        assertThrows(RuntimeException.class, () ->
                state.removeLine(new RemoveLine(courte, ring))
        );
    }

    @Test
    public void availableMove_caseVide_retourneVide() {
        IState state = factory.emptyState();
        Set<Coordinate> moves = state.availableMove(CENTRE);
        assertTrue(moves.isEmpty());
    }

    @Test
    public void availableMove_anneau_retourneDestinations() {
        IState state = factory.testState();
        Set<Coordinate> moves = state.availableMove(ANNEAU_NOIR);
        assertFalse(moves.isEmpty());
    }

    @Test
    public void availableMove_neContientPasLaSource() {
        IState state = factory.testState();
        Set<Coordinate> moves = state.availableMove(ANNEAU_NOIR);
        assertFalse(moves.contains(ANNEAU_NOIR));
    }

    @Test
    public void availableMove_toutesLesDestinationsSontDansLeTerrain() {
        IState state = factory.testState();
        for (Coordinate dest : state.availableMove(ANNEAU_NOIR)) {
            assertTrue(state.isInField(dest));
        }
    }

    @Test
    public void lines_etatVide_aucuneLigne() {
        assertTrue(factory.emptyState().lines().isEmpty());
    }

    @Test
    public void lines_ligneNoire_retourneUneLigne() {
        assertEquals(1, factory.stateForBlackLineTest().lines().size());
    }

    @Test
    public void lines_ligneBlanc_retourneUneLigne() {
        assertEquals(1, factory.stateForWhiteLineTest().lines().size());
    }

    @Test
    public void lines_doubleLigne_retourneDeuxLignes() {
        assertEquals(2, factory.doubleLineStateTest().lines().size());
    }

    @Test
    public void lines_chaqueLigneContient5Cases() {
        IState state = factory.stateForBlackLineTest();
        for (Set<Coordinate> line : state.lines()) {
            assertEquals(5, line.size());
        }
    }

    @Test
    public void winner_etatInitial_retourneNull() {
        assertNull(factory.testState().winner());
    }

    @Test
    public void removeToken_supprimeLeToken() {
        IState state = factory.testState();
        IState next = state.removeToken(ANNEAU_NOIR);
        assertNull(next.board().get(ANNEAU_NOIR));
    }

    @Test
    public void removeToken_immuabilite() {
        IState state = factory.testState();
        IState next = state.removeToken(ANNEAU_NOIR);
        assertNotSame(state, next);
        assertNotNull(state.board().get(ANNEAU_NOIR)); // original intact
    }

    @Test
    public void toggleToken_caseVide_placeLeToken() {
        IState state = factory.emptyState();
        IState next = state.toggleToken(CENTRE, Pawn.class, Team.BLACK);
        assertInstanceOf(Pawn.class, next.board().get(CENTRE));
        assertEquals(Team.BLACK, next.board().get(CENTRE).getTeam());
    }

    @Test
    public void toggleToken_memeTokenMemeEquipe_supprime() {
        IState state = factory.emptyState();
        IState avecPion   = state.toggleToken(CENTRE, Pawn.class, Team.BLACK);
        IState apresToggle = avecPion.toggleToken(CENTRE, Pawn.class, Team.BLACK);
        assertNull(apresToggle.board().get(CENTRE));
    }

    @Test
    public void toggleToken_tokenDifferent_remplace() {
        IState state = factory.emptyState();
        IState avecPion   = state.toggleToken(CENTRE, Pawn.class, Team.BLACK);
        IState avecAnneau = avecPion.toggleToken(CENTRE, Ring.class, Team.WHITE);
        assertInstanceOf(Ring.class, avecAnneau.board().get(CENTRE));
        assertEquals(Team.WHITE, avecAnneau.board().get(CENTRE).getTeam());
    }

    @Test
    public void isInField_centre_retourneTrue() {
        assertTrue(factory.emptyState().isInField(CENTRE));
    }

    @Test
    public void isInField_horsChamp_retourneFalse() {
        assertFalse(factory.emptyState().isInField(HORS_TERRAIN));
    }
}