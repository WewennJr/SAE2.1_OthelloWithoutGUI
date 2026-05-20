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
 * Fabrique permettant de créer des états de jeu basés sur une grille en coordonnées cubiques.
 * <p>
 * Cette classe construit différents états prédéfinis du jeu (état vide, état de test,
 * états pour tests de lignes, etc.) en initialisant un plateau hexagonal.
 * </p>
 */
public class FactoryCube implements IFactory {

    /**
     * Mode de représentation du plateau.
     */
    private static final Mode MODE = Mode.FLAT;

    /**
     * Rayon du plateau hexagonal.
     */
    private static final int RADIUS = 5;

    /**
     * Construit un plateau vide avec toutes les coordonnées initialisées à null.
     *
     * @return une map représentant le plateau vide
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

        board.put(new CoordinateCube(-5, 1, 4), new Ring(Team.WHITE));
        board.put(new CoordinateCube(-2, -2, 4), new Ring(Team.WHITE));
        board.put(new CoordinateCube(-2, -3, 5), new Ring(Team.WHITE));
        board.put(new CoordinateCube(2, -3, 1), new Ring(Team.WHITE));
        board.put(new CoordinateCube(3, -4, 1), new Ring(Team.WHITE));

        board.put(new CoordinateCube(-2, 0, 2), new Ring(Team.BLACK));
        board.put(new CoordinateCube(0, 0, 0), new Ring(Team.BLACK));
        board.put(new CoordinateCube(0, 1, -1), new Ring(Team.BLACK));
        board.put(new CoordinateCube(0, 2, -2), new Ring(Team.BLACK));
        board.put(new CoordinateCube(1, -1, 0), new Ring(Team.BLACK));

        board.put(new CoordinateCube(-2, 1, 1), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-1, 0, 1), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-1, -1, 2), new Pawn(Team.WHITE));

        board.put(new CoordinateCube(-4, 1, 3), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-3, 0, 3), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-2, -1, 3), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-1, -2, 3), new Pawn(Team.BLACK));

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

        board.put(new CoordinateCube(-4, 1, 3), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-3, 1, 2), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-2, 1, 1), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-1, 1, 0), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(0, 1, -1), new Pawn(Team.BLACK));

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

        board.put(new CoordinateCube(-4, 1, 3), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-3, 1, 2), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-2, 1, 1), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-1, 1, 0), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(0, 1, -1), new Pawn(Team.WHITE));

        return new State(board, Team.WHITE, emptyRemovedRings(), MODE);
    }

    /**
     * Construit un plateau de base avec des anneaux et quelques pions.
     *
     * @return plateau initial partiellement rempli
     */
    private Map<Coordinate, Token> baseLineBoard() {
        Map<Coordinate, Token> board = buildEmptyBoard();

        board.put(new CoordinateCube(-2, -2, 4), new Ring(Team.WHITE));
        board.put(new CoordinateCube(-1, -3, 4), new Ring(Team.WHITE));
        board.put(new CoordinateCube(0, -3, 3), new Ring(Team.WHITE));
        board.put(new CoordinateCube(1, -3, 2), new Ring(Team.WHITE));
        board.put(new CoordinateCube(2, -3, 1), new Ring(Team.WHITE));

        board.put(new CoordinateCube(-3, 0, 3), new Ring(Team.BLACK));
        board.put(new CoordinateCube(0, 0, 0), new Ring(Team.BLACK));
        board.put(new CoordinateCube(1, 0, -1), new Ring(Team.BLACK));
        board.put(new CoordinateCube(2, 0, -2), new Ring(Team.BLACK));
        board.put(new CoordinateCube(2, -2, 0), new Ring(Team.BLACK));

        board.put(new CoordinateCube(-2, 0, 2), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-1, 0, 1), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-1, -1, 2), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(0, -1, 1), new Pawn(Team.WHITE));

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

        board.put(new CoordinateCube(-2, -2, 4), new Ring(Team.WHITE));
        board.put(new CoordinateCube(-1, -3, 4), new Ring(Team.WHITE));
        board.put(new CoordinateCube(0, -3, 3), new Ring(Team.WHITE));
        board.put(new CoordinateCube(1, -3, 2), new Ring(Team.WHITE));
        board.put(new CoordinateCube(2, -3, 1), new Ring(Team.WHITE));

        board.put(new CoordinateCube(-3, 0, 3), new Ring(Team.BLACK));
        board.put(new CoordinateCube(0, 0, 0), new Ring(Team.BLACK));
        board.put(new CoordinateCube(1, 0, -1), new Ring(Team.BLACK));
        board.put(new CoordinateCube(2, 0, -2), new Ring(Team.BLACK));
        board.put(new CoordinateCube(2, -2, 0), new Ring(Team.BLACK));

        board.put(new CoordinateCube(-4, 1, 3), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-3, 1, 2), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-2, 1, 1), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(-1, 1, 0), new Pawn(Team.BLACK));
        board.put(new CoordinateCube(0, 1, -1), new Pawn(Team.BLACK));

        board.put(new CoordinateCube(-4, 2, 2), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-3, 2, 1), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-2, 2, 0), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(-1, 2, -1), new Pawn(Team.WHITE));
        board.put(new CoordinateCube(0, 2, -2), new Pawn(Team.WHITE));

        return new State(board, Team.BLACK, emptyRemovedRings(), MODE);
    }
}