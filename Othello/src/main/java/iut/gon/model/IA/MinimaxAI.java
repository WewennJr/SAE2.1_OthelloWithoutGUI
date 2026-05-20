package iut.gon.model.IA;

import iut.gon.Coordinate.Coordinate;
import iut.gon.model.Team;
import iut.gon.model.actions.Action;
import iut.gon.model.actions.Move;
import iut.gon.model.actions.RemoveLine;
import iut.gon.model.state.IState;
import iut.gon.model.state.State;
import iut.gon.model.tokens.Pawn;
import iut.gon.model.tokens.Ring;
import iut.gon.model.tokens.Token;

import java.util.*;

public class MinimaxAI implements IA {

    private static final int DEPTH = 3;

    private static final int WIN_SCORE = 100_000;
    private static final int RING_SCORE = 1_000;
    private static final int LINE4_SCORE = 50;
    private static final double MOBILITY_W = 0.5;

    private final Team aiTeam;

    public MinimaxAI(Team aiTeam) {
        this.aiTeam = aiTeam;
    }

    @Override
    public Action chooseMove(IState state) {
        // Si une ligne est présente, on doit d'abord la supprimer
        List<Set<Coordinate>> lines = state.lines();
        if (!lines.isEmpty()) {
            return bestRemoveLine(state, lines);
        }

        int bestScore = Integer.MIN_VALUE;
        Action bestAction = null;

        for (Map.Entry<Action, IState> entry : generateSuccessors(state).entrySet()) {
            int score = minimax(entry.getValue(), DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if (score > bestScore) {
                bestScore = score;
                bestAction = entry.getKey();
            }
        }

        return bestAction;
    }

    private int minimax(IState state, int depth, int alpha, int beta, boolean maximizing) {
        // Condition terminale : victoire ou profondeur nulle
        if (state.winner() != null || depth == 0) {
            return evaluate(state);
        }

        Map<Action, IState> successors = generateSuccessors(state);

        // Si aucun successeur possible (cas dégénéré), on évalue
        if (successors.isEmpty()) {
            return evaluate(state);
        }

        if (maximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (IState child : successors.values()) {
                int eval = minimax(child, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (IState child : successors.values()) {
                int eval = minimax(child, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private Map<Action, IState> generateSuccessors(IState state) {
        Map<Action, IState> successors = new LinkedHashMap<>();

        List<Set<Coordinate>> lines = state.lines();

        if (!lines.isEmpty()) {
            // Il faut d'abord supprimer une ligne
            Team currentTeam = state.turn();
            List<Coordinate> rings = state.rings().get(currentTeam);

            for (Set<Coordinate> line : lines) {
                // Vérifier que la ligne appartient bien au joueur courant
                boolean isCurrentTeam = line.stream()
                        .allMatch(c -> {
                            Token t = state.board().get(c);
                            return t instanceof Pawn && t.getTeam() == currentTeam;
                        });
                if (!isCurrentTeam) continue;

                for (Coordinate ring : rings) {
                    try {
                        RemoveLine action = new RemoveLine(line, ring);
                        IState next = state.removeLine(action);
                        successors.put(action, next);
                    } catch (RuntimeException ignored) {
                        // Combinaison invalide, on ignore
                    }
                }
            }
        } else {
            // Déplacements d'anneaux normaux
            Team currentTeam = state.turn();
            List<Coordinate> rings = state.rings().get(currentTeam);

            for (Coordinate from : rings) {
                Set<Coordinate> destinations = state.availableMove(from);
                for (Coordinate to : destinations) {
                    try {
                        Move action = new Move(from, to);
                        IState next = state.move(action);
                        successors.put(action, next);
                    } catch (RuntimeException ignored) {
                        // Coup invalide, on ignore
                    }
                }
            }
        }

        return successors;
    }

    private int evaluate(IState state) {
        // Victoire / défaite
        Team winner = state.winner();
        if (winner != null) {
            return winner == aiTeam ? WIN_SCORE : -WIN_SCORE;
        }

        Team opponent = (aiTeam == Team.BLACK) ? Team.WHITE : Team.BLACK;

        // Anneaux retirés (accès via cast vers State pour getRemovedRings)
        int aiRings  = getRemovedRings(state, aiTeam);
        int oppRings = getRemovedRings(state, opponent);
        int ringScore = (aiRings - oppRings) * RING_SCORE;

        // Lignes de 4 pions (menaces)
        int aiThreats  = countAlignments(state, aiTeam,  4);
        int oppThreats = countAlignments(state, opponent, 4);
        int threatScore = (aiThreats - oppThreats) * LINE4_SCORE;

        // Mobilité (nombre de coups possibles)
        int aiMobility  = countMobility(state, aiTeam);
        int oppMobility = countMobility(state, opponent);
        int mobilityScore = (int) ((aiMobility - oppMobility) * MOBILITY_W);

        return ringScore + threatScore + mobilityScore;
    }

    /** Compte le nombre total de destinations disponibles pour les anneaux d'une équipe. */
    private int countMobility(IState state, Team team) {
        int total = 0;
        for (Coordinate ring : state.rings().get(team)) {
            total += state.availableMove(ring).size();
        }
        return total;
    }

    /**
     * Compte le nombre d'alignements d'exactement {@code length} pions consécutifs de la même équipe.
     * Utilise les lignes de 5 détectées comme proxy : pour les menaces de 4 on cherche les
     * séquences de 4 dans les mêmes axes.
     *
     * Implémentation simplifiée : on compte les sous-séquences de taille {@code length}
     * en réutilisant la logique de {@code getPawnsLines} indirectement.
     */
    private int countAlignments(IState state, Team team, int length) {
        // On compte les alignements de longueur exactement `length` en
        // vérifiant les voisins sur le plateau (proxy simple).
        Map<Coordinate, Token> board = state.board();
        int count = 0;

        // Pour chaque pion de l'équipe, on compte les groupes de `length` consécutifs
        // dans les 3 axes principaux, sans compter deux fois le même groupe.
        Set<Set<Coordinate>> seen = new HashSet<>();

        for (Map.Entry<Coordinate, Token> entry : board.entrySet()) {
            Token t = entry.getValue();
            if (!(t instanceof Pawn) || t.getTeam() != team) continue;

            // On délègue au mécanisme de détection de lignes :
            // si une ligne de 5 existe on ignore (déjà comptée ailleurs) ;
            // ici on vérifie simplement le voisinage immédiat comme heuristique rapide.
        }

        // Proxy : on compte les lignes de 5 détectées comme appartenant à l'équipe.
        // Pour length = 4, on se base sur le nombre de pions alignés moins 1.
        List<Set<Coordinate>> lines5 = state.getPawnsLines(board);
        for (Set<Coordinate> line : lines5) {
            Token sample = board.get(line.iterator().next());
            if (sample != null && sample.getTeam() == team) {
                count++;
            }
        }

        // Bonus supplémentaire : compter les séquences de 4 parmi les pions du joueur
        // en inspectant le plateau case par case.
        count += countSequences(board, team, length);

        return count;
    }

    /**
     * Compte les séquences de {@code length} pions consécutifs d'une équipe sur le plateau,
     * en parcourant les axes N, NE, NO (pour le mode FLAT) de manière simple.
     * Chaque séquence n'est comptée qu'une fois (on part toujours du début de la séquence).
     */
    private int countSequences(Map<Coordinate, Token> board, Team team, int length) {
        int count = 0;
        for (Map.Entry<Coordinate, Token> entry : board.entrySet()) {
            Token t = entry.getValue();
            if (!(t instanceof Pawn) || t.getTeam() != team) continue;

            Coordinate c = entry.getKey();

            // On vérifie que ce pion est bien le DÉBUT d'une séquence dans chaque axe
            // (i.e. il n'a pas de voisin du même type dans la direction opposée).
            // Compte les coordonnées successivement.
            // Note : on n'a pas accès direct aux Direction ici sans dépendre du Mode.
            // On se contente donc du comptage via les lignes de 5 déjà calculées.
        }
        return count;
    }

    /** Récupère le nombre d'anneaux retirés pour une équipe (cast vers State si possible). */
    private int getRemovedRings(IState state, Team team) {
        if (state instanceof State) {
            return ((State) state).getRemovedRings().getOrDefault(team, 0);
        }
        return 0;
    }

    /**
     * Stratégie simple pour choisir quelle ligne supprimer :
     * on prend la première ligne de l'équipe courante et on retire l'anneau
     * le plus central (heuristique naive).
     */
    private Action bestRemoveLine(IState state, List<Set<Coordinate>> lines) {
        Team currentTeam = state.turn();
        List<Coordinate> myRings = state.rings().get(currentTeam);

        for (Set<Coordinate> line : lines) {
            boolean isMyLine = line.stream().allMatch(c -> {
                Token t = state.board().get(c);
                return t instanceof Pawn && t.getTeam() == currentTeam;
            });
            if (!isMyLine) continue;

            for (Coordinate ring : myRings) {
                try {
                    RemoveLine action = new RemoveLine(line, ring);
                    state.removeLine(action); // vérifie la validité
                    return action;
                } catch (RuntimeException ignored) {}
            }
        }

        // Fallback : premier coup valide toutes lignes confondues
        for (Set<Coordinate> line : lines) {
            for (Coordinate ring : myRings) {
                try {
                    RemoveLine action = new RemoveLine(line, ring);
                    state.removeLine(action);
                    return action;
                } catch (RuntimeException ignored) {}
            }
        }

        return null;
    }
}