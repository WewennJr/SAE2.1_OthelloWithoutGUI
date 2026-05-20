package iut.gon.model.IA;

import iut.gon.Coordinate.Coordinate;
import iut.gon.model.Team;
import iut.gon.model.actions.Action;
import iut.gon.model.actions.Move;
import iut.gon.model.actions.RemoveLine;
import iut.gon.model.state.IState;
import iut.gon.model.tokens.Pawn;
import iut.gon.model.tokens.Token;

import java.util.*;

/**
 * Intelligence artificielle utilisant l'algorithme Minimax avec élagage alpha-bêta.
 * <p>
 * Cette IA explore les coups possibles jusqu'à une profondeur donnée et évalue les
 * positions selon plusieurs heuristiques (anneaux retirés, lignes, mobilité, victoire).
 * </p>
 */
public class MinimaxAI implements AI {

    private static final int DEPTH = 3;

    private static final int WIN_SCORE = 100_000;
    private static final int RING_SCORE = 1_000;
    private static final int LINE4_SCORE = 50;
    private static final double MOBILITY_W = 0.5;

    private final Team aiTeam;
    private final Team humanTeam;

    /**
     * Crée une IA Minimax pour une équipe donnée.
     *
     * @param aiTeam équipe jouée par l'IA
     */
    public MinimaxAI(Team aiTeam) {
        this.aiTeam = aiTeam;
        this.humanTeam = (aiTeam == Team.BLACK) ? Team.WHITE : Team.BLACK;
    }

    /**
     * Choisit le meilleur coup possible selon Minimax.
     *
     * @param state état actuel du jeu
     * @return action optimale trouvée
     */
    @Override
    public Action chooseMove(IState state) {
        int bestScore  = Integer.MIN_VALUE;
        Action bestAction = null;

        for (Map.Entry<Action, IState> entry : generateSuccessors(state).entrySet()) {
            int score = minimax(entry.getValue(), DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if (score > bestScore) {
                bestScore  = score;
                bestAction = entry.getKey();
            }
        }

        return bestAction;
    }

    /**
     * Algorithme Minimax avec élagage alpha-bêta.
     */
    private int minimax(IState state, int depth, int alpha, int beta, boolean maximizing) {
        if (state.winner() != null || depth == 0) {
            return evaluate(state);
        }

        Map<Action, IState> successors = generateSuccessors(state);
        if (successors.isEmpty()) {
            return evaluate(state);
        }

        if (maximizing) {
            int best = Integer.MIN_VALUE;
            for (IState child : successors.values()) {
                int score = minimax(child, depth - 1, alpha, beta, false);
                best  = Math.max(best, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) {
                    break;
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (IState child : successors.values()) {
                int score = minimax(child, depth - 1, alpha, beta, true);
                best = Math.min(best, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) {
                    break;
                }
            }
            return best;
        }
    }

    /**
     * Génère tous les états successeurs possibles depuis un état donné.
     *
     * @param state état de départ
     * @return map action → nouvel état
     */
    private Map<Action, IState> generateSuccessors(IState state) {
        Map<Action, IState> successors = new LinkedHashMap<>();
        Team current = state.turn();

        List<Set<Coordinate>> lines = state.lines();

        if (!lines.isEmpty()) {
            List<Coordinate> myRings = state.rings().get(current);

            for (Set<Coordinate> line : lines) {
                if (!linesBelongsTo(line, current, state)) {
                    continue;
                }

                for (Coordinate ring : myRings) {
                    try {
                        RemoveLine action = new RemoveLine(line, ring);
                        IState next = state.removeLine(action);
                        successors.put(action, next);
                    } catch (RuntimeException ignored) {
                    }
                }
            }
        } else {
            List<Coordinate> myRings = state.rings().get(current);

            for (Coordinate from : myRings) {
                for (Coordinate to : state.availableMove(from)) {
                    try {
                        Move action = new Move(from, to);
                        IState next = state.move(action);
                        successors.put(action, next);
                    } catch (RuntimeException ignored) {
                    }
                }
            }
        }

        return successors;
    }

    /**
     * Vérifie si une ligne appartient à une équipe donnée.
     */
    private boolean linesBelongsTo(Set<Coordinate> line, Team team, IState state) {
        for (Coordinate c : line) {
            Token t = state.board().get(c);
            if (!(t instanceof Pawn) || t.getTeam() != team) {
                return false;
            }
        }
        return true;
    }

    /**
     * Évalue un état de jeu.
     */
    private int evaluate(IState state) {
        Team winner = state.winner();
        if (winner == aiTeam) {
            return  WIN_SCORE;
        }
        if (winner == humanTeam) {
            return -WIN_SCORE;
        }

        int aiRings = countRemovedRings(state, aiTeam);
        int oppRings = countRemovedRings(state, humanTeam);
        int ringScore = (aiRings - oppRings) * RING_SCORE;

        int aiLines = countLinesOf(state, aiTeam);
        int oppLines = countLinesOf(state, humanTeam);
        int lineScore = (aiLines - oppLines) * LINE4_SCORE;

        int aiMobility = countMobility(state, aiTeam);
        int oppMobility = countMobility(state, humanTeam);
        int mobilityScore = (int) ((aiMobility - oppMobility) * MOBILITY_W);

        return ringScore + lineScore + mobilityScore;
    }

    /**
     * Calcule le nombre d’anneaux retirés pour une équipe.
     */
    private int countRemovedRings(IState state, Team team) {
        int remaining = state.rings().get(team).size();
        return 5 - remaining;
    }

    /**
     * Compte les lignes d’une équipe.
     */
    private int countLinesOf(IState state, Team team) {
        int count = 0;
        for (Set<Coordinate> line : state.lines()) {
            if (linesBelongsTo(line, team, state)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calcule la mobilité d’une équipe.
     */
    private int countMobility(IState state, Team team) {
        int total = 0;
        for (Coordinate ring : state.rings().get(team)) {
            total += state.availableMove(ring).size();
        }
        return total;
    }
}