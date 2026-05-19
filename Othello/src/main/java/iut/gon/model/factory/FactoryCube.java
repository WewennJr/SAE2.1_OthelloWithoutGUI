package iut.gon.model.factory;

import iut.gon.Coordinate.Coordinate;
import iut.gon.Coordinate.CoordinateCube;
import iut.gon.Coordinate.Mode;
import iut.gon.model.Team;
import iut.gon.model.state.IState;
import iut.gon.model.state.State;
import iut.gon.model.tokens.Pawn;
import iut.gon.model.tokens.Ring;
import iut.gon.model.tokens.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory générant les états de test avec le système de coordonnées CoordinateCube.
 * La case centrale est [0, 0, 0].
 * Contrainte : q + r + s = 0.
 *
 * Terrain hexagonal de rayon 5.
 */
public class FactoryCube implements IFactory {

    private static final Mode MODE = Mode.FLAT;
    private static final int RADIUS = 5;

    /**
     * Génère toutes les coordonnées valides du terrain YINSH en coordonnées cubiques.
     * Toutes les cases (q, r, s) telles que max(|q|, |r|, |s|) <= RADIUS et q+r+s=0.
     */
    private Map<Coordinate, Token> buildEmptyBoard() {
        Map<Coordinate, Token> board = new HashMap<>();
        for (int q = -RADIUS; q <= RADIUS; q++) {
            for (int r = Math.max(-RADIUS, -q - RADIUS); r <= Math.min(RADIUS, -q + RADIUS); r++) {
                int s = -q - r;
                board.put(new CoordinateCube(q, r, s), null);
            }
        }
        return board;
    }

    private Map<Team, Integer> emptyRemovedRings() {
        Map<Team, Integer> m = new HashMap<>();
        m.put(Team.BLACK, 0);
        m.put(Team.WHITE, 0);
        return m;
    }

    @Override
    public IState emptyState() {
        return new State(buildEmptyBoard(), Team.BLACK, emptyRemovedRings(), MODE);
    }

    @Override
    public IState testState() {
        Map<Coordinate, Token> board = buildEmptyBoard();

        // Anneaux blancs
        board.put(new CoordinateCube(-5, 1, 4), new Ring(Team.WHITE));
        board.put(new CoordinateCube(-2, -2, 4), new Ring(Team.WHITE));
        board.put(new CoordinateCube(-2, -3, 5), new Ring(Team.WHITE));
        board.put(new CoordinateCube(2, -3, 1), new Ring(Team.WHITE));
        board.put(new CoordinateCube(3, -4, 1), new Ring(Team.WHITE));

        // Anneaux noirs
        board.put(new CoordinateCube(-2, 0, 2), new Ring(Team.BLACK));
        board.put(new CoordinateCube(0, 0, 0), new Ring(Team.BLACK));
        board.put(new CoordinateCube(0, 1, -1), new Ring(Team.BLACK));
        board.put(new CoordinateCube(0, 2, -2), new Ring(Team.BLACK));
        board.put(new CoordinateCube(1, -1, 0), new Ring(Team.BLACK));

        // Pions blancs
        board.put(new CoordinateCube(-2, 1, 1), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-1, 0, 1), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-1, -1, 2), new Pawn(Team.WHITE));

        // Pions noirs
        board.put(new CoordinateCube(-4, 1, 3), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-3, 0, 3), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-2, -1, 3), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-1, -2, 3), new Pawn(Team.BLACK));

        return new State(board, Team.BLACK, emptyRemovedRings(), MODE);
    }

    @Override
    public IState stateForBlackLineTest() {
        Map<Coordinate, Token> board = baseLineBoard();

        // Ligne de 5 pions noirs horizontaux (r constant = 1)
        board.put(new CoordinateCube(-4, 1, 3), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-3, 1, 2), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-2, 1, 1), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-1, 1, 0), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(0, 1, -1), new Pawn(Team.BLACK));

        return new State(board, Team.BLACK, emptyRemovedRings(), MODE);
    }

    @Override
    public IState stateForWhiteLineTest() {
        Map<Coordinate, Token> board = baseLineBoard();

        // Ligne de 5 pions blancs horizontaux (r constant = 1)
        board.put(new CoordinateCube(-4, 1, 3), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-3, 1, 2), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-2, 1, 1), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-1, 1, 0), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(0, 1, -1), new Pawn(Team.WHITE));

        return new State(board, Team.WHITE, emptyRemovedRings(), MODE);
    }

    private Map<Coordinate, Token> baseLineBoard() {
        Map<Coordinate, Token> board = buildEmptyBoard();

        // Anneaux blancs
        board.put(new CoordinateCube(-2, -2, 4), new Ring(Team.WHITE));
        board.put(new CoordinateCube(-1, -3, 4), new Ring(Team.WHITE));
        board.put(new CoordinateCube(0, -3, 3), new Ring(Team.WHITE));
        board.put(new CoordinateCube(1, -3, 2), new Ring(Team.WHITE));
        board.put(new CoordinateCube(2, -3, 1), new Ring(Team.WHITE));

        // Anneaux noirs
        board.put(new CoordinateCube(-3, 0, 3), new Ring(Team.BLACK));
        board.put(new CoordinateCube(0, 0, 0), new Ring(Team.BLACK));
        board.put(new CoordinateCube(1, 0, -1), new Ring(Team.BLACK));
        board.put(new CoordinateCube(2, 0, -2), new Ring(Team.BLACK));
        board.put(new CoordinateCube(2, -2, 0), new Ring(Team.BLACK));

        // Pions communs
        board.put(new CoordinateCube(-2, 0, 2), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-1, 0, 1), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-1, -1, 2), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(0, -1, 1), new Pawn(Team.WHITE));

        return board;
    }

    @Override
    public IState doubleLineStateTest() {
        Map<Coordinate, Token> board = buildEmptyBoard();

        // Anneaux blancs
        board.put(new CoordinateCube(-2, -2, 4), new Ring(Team.WHITE));
        board.put(new CoordinateCube(-1, -3, 4), new Ring(Team.WHITE));
        board.put(new CoordinateCube(0, -3, 3), new Ring(Team.WHITE));
        board.put(new CoordinateCube(1, -3, 2), new Ring(Team.WHITE));
        board.put(new CoordinateCube(2, -3, 1), new Ring(Team.WHITE));

        // Anneaux noirs
        board.put(new CoordinateCube(-3, 0, 3), new Ring(Team.BLACK));
        board.put(new CoordinateCube(0, 0, 0), new Ring(Team.BLACK));
        board.put(new CoordinateCube(1, 0, -1), new Ring(Team.BLACK));
        board.put(new CoordinateCube(2, 0, -2), new Ring(Team.BLACK));
        board.put(new CoordinateCube(2, -2, 0), new Ring(Team.BLACK));

        // Ligne de 5 pions noirs (r = 1)
        board.put(new CoordinateCube(-4, 1, 3), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-3, 1, 2), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-2, 1, 1), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-1, 1, 0), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(0, 1, -1), new Pawn(Team.BLACK));

        // Ligne de 5 pions blancs (r = 2)
        board.put(new CoordinateCube(-4, 2, 2), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-3, 2, 1), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-2, 2, 0), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-1, 2, -1), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(0, 2, -2), new Pawn(Team.WHITE));

        return new State(board, Team.BLACK, emptyRemovedRings(), MODE);
    }
}