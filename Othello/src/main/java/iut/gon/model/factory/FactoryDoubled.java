package iut.gon.model.factory;

import iut.gon.Coordinate.Coordinate;
import iut.gon.Coordinate.CoordinateDoubled;
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
 * Fabrique permettant de créer des états de jeu en coordonnées "doubled".
 * <p>
 * Cette classe génère un plateau selon une représentation en grille
 * avec des coordonnées doublées (offset grid), ainsi que différents
 * états de test pour le jeu.
 * </p>
 */
public class FactoryDoubled implements IFactory {

    /**
     * Mode de représentation du plateau.
     */
    private static final Mode MODE = Mode.FLAT;

    /**
     * Construit un plateau vide en initialisant toutes les cases valides à null.
     *
     * @return une map représentant le plateau vide
     */
    private Map<Coordinate, Token> buildEmptyBoard() {
        Map<Coordinate, Token> board = new HashMap<>();

        int[][] validCells = {
                {0, 6}, {0, 8}, {0, 10}, {0, 12},
                {1, 3}, {1, 5}, {1, 7}, {1, 9}, {1, 11}, {1, 13}, {1, 15},
                {2, 2}, {2, 4}, {2, 6}, {2, 8}, {2, 10}, {2, 12}, {2, 14}, {2, 16},
                {3, 1}, {3, 3}, {3, 5}, {3, 7}, {3, 9}, {3, 11}, {3, 13}, {3, 15}, {3, 17},
                {4, 0}, {4, 2}, {4, 4}, {4, 6}, {4, 8}, {4, 10}, {4, 12}, {4, 14}, {4, 16}, {4, 18},
                {5, 1}, {5, 3}, {5, 5}, {5, 7}, {5, 9}, {5, 11}, {5, 13}, {5, 15}, {5, 17},
                {6, 0}, {6, 2}, {6, 4}, {6, 6}, {6, 8}, {6, 10}, {6, 12}, {6, 14}, {6, 16}, {6, 18},
                {7, 1}, {7, 3}, {7, 5}, {7, 7}, {7, 9}, {7, 11}, {7, 13}, {7, 15}, {7, 17},
                {8, 2}, {8, 4}, {8, 6}, {8, 8}, {8, 10}, {8, 12}, {8, 14}, {8, 16},
                {9, 3}, {9, 5}, {9, 7}, {9, 9}, {9, 11}, {9, 13}, {9, 15},
                {10, 6}, {10, 8}, {10, 10}, {10, 12},
        };

        for (int[] cell : validCells) {
            board.put(new CoordinateDoubled(cell[0], cell[1]), null);
        }
        return board;
    }

    /**
     * Crée une map initialisant le nombre d'anneaux retirés pour chaque équipe à zéro.
     *
     * @return map des anneaux retirés par équipe
     */
    private Map<Team, Integer> emptyRemovedRings() {
        Map<Team, Integer> m = new HashMap<>();
        m.put(Team.BLACK, 0);
        m.put(Team.WHITE, 0);
        return m;
    }

    /**
     * Crée un état vide du jeu.
     *
     * @return état initial sans pièces placées
     */
    @Override
    public IState emptyState() {
        return new State(buildEmptyBoard(), Team.BLACK, emptyRemovedRings(), MODE);
    }

    /**
     * Crée un état de test avec des pièces placées de manière prédéfinie.
     *
     * @return état de test du jeu
     */
    @Override
    public IState testState() {
        Map<Coordinate, Token> board = buildEmptyBoard();

        board.put(new CoordinateDoubled(4, 0), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(5, 3), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(5, 5), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(7, 9), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(8, 8), new Ring(Team.WHITE));

        board.put(new CoordinateDoubled(3, 7), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 9), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 11), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 13), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(6, 8), new Ring(Team.BLACK));

        board.put(new CoordinateDoubled(3, 5), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(3, 9), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(4, 6), new Pawn(Team.WHITE));

        board.put(new CoordinateDoubled(6, 2), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(7, 3), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(8, 4), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(9, 5), new Pawn(Team.BLACK));

        return new State(board, Team.BLACK, emptyRemovedRings(), MODE);
    }

    /**
     * Crée un état de test pour vérifier la détection de ligne noire.
     *
     * @return état avec une ligne noire complète
     */
    @Override
    public IState stateForBlackLineTest() {
        Map<Coordinate, Token> board = baseLineBoard();

        board.put(new CoordinateDoubled(6, 2), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 4), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 6), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 8), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 10), new Pawn(Team.BLACK));

        return new State(board, Team.BLACK, emptyRemovedRings(), MODE);
    }

    /**
     * Crée un état de test pour vérifier la détection de ligne blanche.
     *
     * @return état avec une ligne blanche complète
     */
    @Override
    public IState stateForWhiteLineTest() {
        Map<Coordinate, Token> board = baseLineBoard();

        board.put(new CoordinateDoubled(6, 2), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(6, 4), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(6, 6), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(6, 8), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(6, 10), new Pawn(Team.WHITE));

        return new State(board, Team.WHITE, emptyRemovedRings(), MODE);
    }

    /**
     * Construit un plateau de base avec des pièces initiales.
     *
     * @return plateau partiellement rempli
     */
    private Map<Coordinate, Token> baseLineBoard() {
        Map<Coordinate, Token> board = buildEmptyBoard();

        board.put(new CoordinateDoubled(3, 7), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(4, 10), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(4, 12), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(5, 13), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(6, 14), new Ring(Team.WHITE));

        board.put(new CoordinateDoubled(4, 6), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 9), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 11), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 15), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(7, 9), new Ring(Team.BLACK));

        board.put(new CoordinateDoubled(3, 9), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(3, 11), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(4, 8), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(5, 7), new Pawn(Team.WHITE));

        return board;
    }

    /**
     * Crée un état de test avec deux lignes complètes simultanées.
     *
     * @return état contenant plusieurs lignes actives
     */
    @Override
    public IState doubleLineStateTest() {
        Map<Coordinate, Token> board = buildEmptyBoard();

        board.put(new CoordinateDoubled(3, 7), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(4, 10), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(4, 12), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(5, 13), new Ring(Team.WHITE));
        board.put(new CoordinateDoubled(6, 14), new Ring(Team.WHITE));

        board.put(new CoordinateDoubled(4, 6), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 9), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 11), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(5, 15), new Ring(Team.BLACK));
        board.put(new CoordinateDoubled(7, 9), new Ring(Team.BLACK));

        board.put(new CoordinateDoubled(6, 2), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 4), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 6), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 8), new Pawn(Team.BLACK));
        board.put(new CoordinateDoubled(6, 10), new Pawn(Team.BLACK));

        board.put(new CoordinateDoubled(5, 3), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(5, 5), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(5, 7), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(6, 12), new Pawn(Team.WHITE));
        board.put(new CoordinateDoubled(6, 14), new Pawn(Team.WHITE));

        return new State(board, Team.BLACK, emptyRemovedRings(), MODE);
    }
}