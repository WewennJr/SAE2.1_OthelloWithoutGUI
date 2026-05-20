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

public class MinimaxAI implements AI {

    private static final int DEPTH = 3;

    private static final int WIN_SCORE = 100_000;
    private static final int RING_SCORE = 1_000;
    private static final int LINE4_SCORE = 50;
    private static final double MOBILITY_W = 0.5;

    private final Team aiTeam;
    private final Team humanTeam;

    public MinimaxAI(Team aiTeam) {
        this.aiTeam = aiTeam;
        this.humanTeam = (aiTeam == Team.BLACK) ? Team.WHITE : Team.BLACK;
    }

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

    private int minimax(IState state, int depth, int alpha, int beta, boolean maximizing) {
        // Cas terminal : victoire détectée ou profondeur max atteinte
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

    private Map<Action, IState> generateSuccessors(IState state) {
        Map<Action, IState> successors = new LinkedHashMap<>();
        Team current = state.turn();

        List<Set<Coordinate>> lines = state.lines();

        if (!lines.isEmpty()) {
            // Obligation de supprimer une ligne avant tout déplacement
            List<Coordinate> myRings = state.rings().get(current);

            for (Set<Coordinate> line : lines) {
                // On ne traite que les lignes du joueur courant
                if (!linesBelongsTo(line, current, state)) continue;

                for (Coordinate ring : myRings) {
                    try {
                        RemoveLine action = new RemoveLine(line, ring);
                        IState next = state.removeLine(action);
                        successors.put(action, next);
                    } catch (RuntimeException ignored) {
                        // Combinaison invalide, on passe
                    }
                }
            }
        } else {
            // Déplacements d'anneaux normaux
            List<Coordinate> myRings = state.rings().get(current);

            for (Coordinate from : myRings) {
                for (Coordinate to : state.availableMove(from)) {
                    try {
                        Move action = new Move(from, to);
                        IState next = state.move(action);
                        successors.put(action, next);
                    } catch (RuntimeException ignored) {
                        // Coup invalide, on passe
                    }
                }
            }
        }

        return successors;
    }

    /** Vérifie que tous les pions d'une ligne appartiennent à l'équipe donnée. */
    private boolean linesBelongsTo(Set<Coordinate> line, Team team, IState state) {
        for (Coordinate c : line) {
            Token t = state.board().get(c);
            if (!(t instanceof Pawn) || t.getTeam() != team) {
                return false;
            }
        }
        return true;
    }

    private int evaluate(IState state) {
        // Victoire / défaite : score extrême
        Team winner = state.winner();
        if (winner == aiTeam) {
            return  WIN_SCORE;
        }
        if (winner == humanTeam) {
            return -WIN_SCORE;
        }

        // Anneaux retirés : chaque anneau retiré compte pour 1000
        int aiRings = countRemovedRings(state, aiTeam);
        int oppRings = countRemovedRings(state, humanTeam);
        int ringScore = (aiRings - oppRings) * RING_SCORE;

        // Lignes de 5 complètes présentes sur le plateau (menace immédiate)
        int aiLines = countLinesOf(state, aiTeam);
        int oppLines = countLinesOf(state, humanTeam);
        int lineScore = (aiLines - oppLines) * LINE4_SCORE;

        // Mobilité : nombre total de destinations disponibles pour les anneaux
        int aiMobility = countMobility(state, aiTeam);
        int oppMobility = countMobility(state, humanTeam);
        int mobilityScore = (int) ((aiMobility - oppMobility) * MOBILITY_W);

        return ringScore + lineScore + mobilityScore;
    }

    private int countRemovedRings(IState state, Team team) {
        // On déduit les anneaux retirés en comparant : 5 anneaux au départ, X restants
        int remaining = state.rings().get(team).size();
        return 5 - remaining;
    }

    private int countLinesOf(IState state, Team team) {
        int count = 0;
        for (Set<Coordinate> line : state.lines()) {
            if (linesBelongsTo(line, team, state)) count++;
        }
        return count;
    }

    private int countMobility(IState state, Team team) {
        int total = 0;
        for (Coordinate ring : state.rings().get(team)) {
            total += state.availableMove(ring).size();
        }
        return total;
    }
}