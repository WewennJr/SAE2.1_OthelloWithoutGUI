package iut.gon.model.factory;

import iut.gon.hexcoordinate.Coordinate;
import iut.gon.hexcoordinate.CoordinateDoubled;
import iut.gon.hexcoordinate.Mode;
import iut.gon.model.Team;
import iut.gon.model.state.IState;
import iut.gon.model.state.State;
import iut.gon.model.tokens.Pawn;
import iut.gon.model.tokens.Ring;
import iut.gon.model.tokens.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory générant les états de test avec le système de coordonnées CoordinateDoubled.
 * La case centrale est [5, 9] (ligne 5, colonne 9).
 *
 * Le terrain est un hexagone de rayon 5 représenté en Doubled.
 * Les coordonnées valides ont des colonnes paires sur les lignes paires et impaires sur les lignes impaires.
 */
public class FactoryDoubled implements IFactory {

    private static final Mode MODE = Mode.FLAT;

    /**
     * Génère toutes les coordonnées valides du terrain YINSH en Doubled.
     * Terrain hexagonal de rayon 5 centré en [5, 9].
     */
    private Map<Coordinate, Token> buildEmptyBoard() {
        Map<Coordinate, Token> board = new HashMap<>();

        int[][] validCells = {
                // ligne 0
                {0, 6}, {0, 8}, {0, 10}, {0, 12},
                // ligne 1
                {1, 3}, {1, 5}, {1, 7}, {1, 9}, {1, 11}, {1, 13}, {1, 15},
                // ligne 2
                {2, 2}, {2, 4}, {2, 6}, {2, 8}, {2, 10}, {2, 12}, {2, 14}, {2, 16},
                // ligne 3
                {3, 1}, {3, 3}, {3, 5}, {3, 7}, {3, 9}, {3, 11}, {3, 13}, {3, 15}, {3, 17},
                // ligne 4
                {4, 0}, {4, 2}, {4, 4}, {4, 6}, {4, 8}, {4, 10}, {4, 12}, {4, 14}, {4, 16}, {4, 18},
                // ligne 5
                {5, 1}, {5, 3}, {5, 5}, {5, 7}, {5, 9}, {5, 11}, {5, 13}, {5, 15}, {5, 17},
                // ligne 6
                {6, 0}, {6, 2}, {6, 4}, {6, 6}, {6, 8}, {6, 10}, {6, 12}, {6, 14}, {6, 16}, {6, 18},
                // ligne 7
                {7, 1}, {7, 3}, {7, 5}, {7, 7}, {7, 9}, {7, 11}, {7, 13}, {7, 15}, {7, 17},
                // ligne 8
                {8, 2}, {8, 4}, {8, 6}, {8, 8}, {8, 10}, {8, 12}, {8, 14}, {8, 16},
                // ligne 9
                {9, 3}, {9, 5}, {9, 7}, {9, 9}, {9, 11}, {9, 13}, {9, 15},
                // ligne 10
                {10, 6}, {10, 8}, {10, 10}, {10, 12},
        };

        for (int[] cell : validCells) {
            board.put(new CoordinateDoubled(cell[0], cell[1]), null);
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
        board.put(new CoordinateDoubled(4, 0), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(5, 3), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(5, 5), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(7, 9), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(8, 8), new Ring(Team.WHITE));

        // Anneaux noirs
        board.put(new CoordinateDoubled(3, 7), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 9), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 11), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 13), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(6, 8), new Ring(Team.BLACK));

        // Pions blancs
        board.put(new CoordinateDoubled(3, 5), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(3, 9), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(4, 6), new Pawn(Team.WHITE));

        // Pions noirs
        board.put(new CoordinateDoubled(6, 2), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(7, 3), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(8, 4), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(9, 5), new Pawn(Team.BLACK));

        return new State(board, Team.BLACK, emptyRemovedRings(), MODE);
    }

    @Override
    public IState stateForBlackLineTest() {
        Map<Coordinate, Token> board = baseLineBoard();

        // Ligne de 5 pions noirs sur la ligne 6, colonnes 2 à 10
        board.put(new CoordinateDoubled(6, 2), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 4), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 6), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 8), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 10), new Pawn(Team.BLACK));

        return new State(board, Team.BLACK, emptyRemovedRings(), MODE);
    }

    @Override
    public IState stateForWhiteLineTest() {
        Map<Coordinate, Token> board = baseLineBoard();

        // Ligne de 5 pions blancs sur la ligne 6, colonnes 2 à 10
        board.put(new CoordinateDoubled(6, 2), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(6, 4), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(6, 6), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(6, 8), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(6, 10), new Pawn(Team.WHITE));

        return new State(board, Team.WHITE, emptyRemovedRings(), MODE);
    }

    private Map<Coordinate, Token> baseLineBoard() {
        Map<Coordinate, Token> board = buildEmptyBoard();

        // Anneaux blancs
        board.put(new CoordinateDoubled(3, 7), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(4, 10), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(4, 12), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(5, 13), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(6, 14), new Ring(Team.WHITE));

        // Anneaux noirs
        board.put(new CoordinateDoubled(4, 6), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 9), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 11), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 15), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(7, 9), new Ring(Team.BLACK));

        // Pions communs (mélangés)
        board.put(new CoordinateDoubled(3, 9), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(3, 11), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(4, 8), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(5, 7), new Pawn(Team.WHITE));

        return board;
    }

    @Override
    public IState doubleLineStateTest() {
        Map<Coordinate, Token> board = buildEmptyBoard();

        // Anneaux blancs
        board.put(new CoordinateDoubled(3, 7), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(4, 10), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(4, 12), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(5, 13), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(6, 14), new Ring(Team.WHITE));

        // Anneaux noirs
        board.put(new CoordinateDoubled(4, 6), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 9), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 11), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 15), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(7, 9), new Ring(Team.BLACK));

        // Ligne de 5 pions noirs (horizontale)
        board.put(new CoordinateDoubled(6, 2), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 4), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 6), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 8), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 10), new Pawn(Team.BLACK));

        // Ligne de 5 pions blancs (horizontale décalée)
        board.put(new CoordinateDoubled(5, 3), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(5, 5), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(5, 7), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(6, 12), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(6, 14), new Pawn(Team.WHITE));

        return new State(board, Team.BLACK, emptyRemovedRings(), MODE);
    }
}