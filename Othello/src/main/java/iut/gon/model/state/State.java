package iut.gon.model.state;

import iut.gon.Coordinate.Coordinate;
import iut.gon.Coordinate.Direction;
import iut.gon.Coordinate.DifferentAxisException;
import iut.gon.Coordinate.Mode;
import iut.gon.model.Team;
import iut.gon.model.actions.Move;
import iut.gon.model.actions.RemoveLine;
import iut.gon.model.tokens.Pawn;
import iut.gon.model.tokens.Ring;
import iut.gon.model.tokens.Token;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Représentation immuable d'une situation de jeu.
 * Chaque méthode qui modifie l'état renvoie un NOUVEAU State, sans modifier le courant.
 */
public class State implements IState {

    private static final int RINGS_TO_WIN = 3;
    private static final int LINE_LENGTH = 5;

    private final Map<Coordinate, Token> board;
    private final Team turn;
    private final Map<Team, Integer> removedRings;
    private final Mode mode;

    public State(Map<Coordinate, Token> board, Team turn, Map<Team, Integer> removedRings, Mode mode) {
        this.board = Collections.unmodifiableMap(new HashMap<>(board));
        this.turn = turn;
        this.removedRings = Collections.unmodifiableMap(new HashMap<>(removedRings));
        this.mode = mode;
    }

    @Override
    public IState move(Move move) {
        Coordinate from = move.getFrom();
        Coordinate to   = move.getTo();

        if (!lines().isEmpty()) {
            throw new RuntimeException("Une ligne doit être supprimée avant de jouer.");
        }

        if (!isInField(from)) {
            throw new IndexOutOfBoundsException("Coordonnée de départ hors du terrain : " + from);
        }
        if (!isInField(to)) {
            throw new IndexOutOfBoundsException("Coordonnée d'arrivée hors du terrain : " + to);
        }

        Token tokenAt = board.get(from);
        if (!(tokenAt instanceof Ring) || tokenAt.getTeam() != turn) {
            throw new IllegalArgumentException("Pas d'anneau du joueur courant en " + from);
        }

        Set<Coordinate> reachable = availableMove(from);
        if (!reachable.contains(to)) {
            throw new IllegalArgumentException("La case " + to + " n'est pas atteignable depuis " + from);
        }

        Map<Coordinate, Token> newBoard = new HashMap<>(board);

        newBoard.remove(from);

        List<Coordinate> between = new ArrayList<>();
        try {
            between = from.between(mode, to);
        } catch (DifferentAxisException e) {
            throw new IllegalArgumentException("Les coordonnées ne sont pas sur le même axe : " + e.getMessage());
        }
        for (Coordinate c : between) {
            Token t = newBoard.get(c);
            if (t instanceof Pawn) {
                Team opposite = (t.getTeam() == Team.BLACK) ? Team.WHITE : Team.BLACK;
                newBoard.put(c, new Pawn(opposite));
            }
        }

        newBoard.put(from, new Pawn(turn));

        newBoard.put(to, new Ring(turn));

        List<Set<Coordinate>> newLines = getPawnsLines(newBoard);
        Team nextTurn = turn;
        if (newLines.isEmpty()) {
            nextTurn = (turn == Team.BLACK) ? Team.WHITE : Team.BLACK;
        } else {
            // Priorité à l'équipe courante si elle a une ligne
            boolean currentHasLine = newLines.stream().anyMatch(line ->
                    line.stream().anyMatch(c -> newBoard.get(c) != null && newBoard.get(c).getTeam() == turn));
            if (!currentHasLine) {
                nextTurn = (turn == Team.BLACK) ? Team.WHITE : Team.BLACK;
            }
        }

        return new State(newBoard, nextTurn, removedRings, mode);
    }

    @Override
    public IState removeLine(RemoveLine removeLine) {
        Set<Coordinate> line = removeLine.getLine();
        Coordinate ringCoord = removeLine.getRing();

        List<Set<Coordinate>> currentLines = lines();
        if (currentLines.isEmpty()) {
            throw new RuntimeException("Aucune ligne à supprimer sur le plateau.");
        }

        if (line.size() != LINE_LENGTH) {
            throw new RuntimeException("La ligne doit contenir exactement 5 cases, reçu : " + line.size());
        }

        boolean lineExists = currentLines.stream().anyMatch(l -> l.equals(line));
        if (!lineExists) {
            throw new RuntimeException("La ligne fournie n'existe pas sur le plateau.");
        }

        Team lineTeam = null;
        for (Coordinate c : line) {
            Token t = board.get(c);
            if (!(t instanceof Pawn)) {
                throw new RuntimeException("La ligne contient une case sans pion.");
            }
            if (lineTeam == null) {
                lineTeam = t.getTeam();
            } else if (t.getTeam() != lineTeam) {
                throw new RuntimeException("Les pions de la ligne ne sont pas tous de la même équipe.");
            }
        }

        Token ringToken = board.get(ringCoord);
        if (!(ringToken instanceof Ring) || ringToken.getTeam() != turn) {
            throw new RuntimeException("La coordonnée de l'anneau ne contient pas un anneau du joueur courant.");
        }

        Map<Coordinate, Token> newBoard = new HashMap<>(board);

        for (Coordinate c : line) {
            newBoard.remove(c);
        }

        newBoard.put(ringCoord, null);

        Map<Team, Integer> newRemovedRings = new HashMap<>(removedRings);
        newRemovedRings.put(turn, newRemovedRings.getOrDefault(turn, 0) + 1);

        List<Set<Coordinate>> remainingLines = getPawnsLines(newBoard);
        Team nextTurn = turn;
        if (remainingLines.isEmpty()) {
            nextTurn = (turn == Team.BLACK) ? Team.WHITE : Team.BLACK;
        } else {
            boolean currentStillHasLine = remainingLines.stream().anyMatch(l ->
                    l.stream().anyMatch(c -> newBoard.get(c) != null && newBoard.get(c).getTeam() == turn));
            if (!currentStillHasLine) {
                nextTurn = (turn == Team.BLACK) ? Team.WHITE : Team.BLACK;
            }
        }

        return new State(newBoard, nextTurn, newRemovedRings, mode);
    }

    @Override
    public Set<Coordinate> availableMove(Coordinate from) {
        Set<Coordinate> result = new HashSet<>();

        if (!isInField(from) || !(board.get(from) instanceof Ring)) {
            return result;
        }

        Direction[] directions = getDirections();

        for (Direction dir : directions) {
            boolean jumpedOverPawn = false;
            Coordinate current = from.toDir(mode, dir);

            while (current != null && isInField(current)) {
                Token tokenHere = board.get(current);

                if (tokenHere instanceof Ring) {
                    break;
                } else if (tokenHere instanceof Pawn) {
                    jumpedOverPawn = true;
                    current = current.toDir(mode, dir);
                } else {
                    if (jumpedOverPawn) {
                        result.add(current);
                        break;
                    } else {
                        result.add(current);
                        current = current.toDir(mode, dir);
                    }
                }
            }
        }

        return result;
    }

    private Direction[] getDirections() {
        if (mode == Mode.FLAT) {
            return new Direction[]{Direction.N, Direction.S, Direction.NE, Direction.NO, Direction.SE, Direction.SO};
        } else {
            return new Direction[]{Direction.E, Direction.O, Direction.NE, Direction.NO, Direction.SE, Direction.SO};
        }
    }

    @Override
    public Map<Coordinate, Token> board() {
        return board;
    }

    @Override
    public Map<Team, List<Coordinate>> rings() {
        Map<Team, List<Coordinate>> result = new HashMap<>();
        result.put(Team.BLACK, new ArrayList<>());
        result.put(Team.WHITE, new ArrayList<>());

        for (Map.Entry<Coordinate, Token> entry : board.entrySet()) {
            if (entry.getValue() instanceof Ring) {
                result.get(entry.getValue().getTeam()).add(entry.getKey());
            }
        }
        return result;
    }

    @Override
    public List<Set<Coordinate>> lines() {
        return getPawnsLines(board);
    }

    @Override
    public Team turn() {
        return turn;
    }

    @Override
    public Team winner() {
        for (Map.Entry<Team, Integer> entry : removedRings.entrySet()) {
            if (entry.getValue() >= RINGS_TO_WIN) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Détecte toutes les lignes de 5 pions consécutifs dans le board fourni.
     * Une ligne de 6 pions génère deux HashSet de 5 éléments.
     * Les axes dépendent du mode :
     *   FLAT  → N/S, NE/SO, NO/SE
     *   POINTY → E/O, NE/SO, NO/SE
     */
    @Override
    public List<Set<Coordinate>> getPawnsLines(Map<Coordinate, Token> board) {
        List<Set<Coordinate>> result = new ArrayList<>();

        Direction[][] axes;
        if (mode == Mode.FLAT) {
            axes = new Direction[][]{
                    {Direction.N,  Direction.S},
                    {Direction.NE, Direction.SO},
                    {Direction.NO, Direction.SE}
            };
        } else {
            axes = new Direction[][]{
                    {Direction.E,  Direction.O},
                    {Direction.NE, Direction.SO},
                    {Direction.NO, Direction.SE}
            };
        }

        for (Map.Entry<Coordinate, Token> entry : board.entrySet()) {
            if (!(entry.getValue() instanceof Pawn)) {
                continue;
            }
            Coordinate start = entry.getKey();

            for (Direction[] axis : axes) {
                List<Coordinate> sequence = buildSequence(board, start, axis[0], axis[1]);

                if (sequence.size() >= LINE_LENGTH) {
                    for (int i = 0; i <= sequence.size() - LINE_LENGTH; i++) {
                        Set<Coordinate> line = new HashSet<>(sequence.subList(i, i + LINE_LENGTH));
                        // Vérifier que tous les pions sont de la même équipe
                        Team lineTeam = board.get(sequence.get(i)).getTeam();
                        boolean sameTeam = true;
                        for (Coordinate c : line) {
                            Token t = board.get(c);
                            if (!(t instanceof Pawn) || t.getTeam() != lineTeam) {
                                sameTeam = false;
                                break;
                            }
                        }
                        if (sameTeam && !result.contains(line)) {
                            result.add(line);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Construit la séquence de coordonnées alignées contenant des pions,
     * en partant de {@code center} et en explorant dans les deux directions de l'axe.
     */
    private List<Coordinate> buildSequence(Map<Coordinate, Token> board, Coordinate center,
                                           Direction dir1, Direction dir2) {
        // Reculer jusqu'au bout de la séquence continue (dans dir2)
        Coordinate cursor = center;
        while (cursor != null && isInField(cursor) && board.get(cursor) instanceof Pawn) {
            cursor = cursor.toDir(mode, dir2);
        }
        // cursor est maintenant hors de la séquence côté dir2 ; on revient d'une case
        if (cursor == null) {
            cursor = center;
        } else {
            cursor = cursor.toDir(mode, dir1);
        }

        // Avancer vers dir1 et collecter les pions consécutifs
        List<Coordinate> sequence = new ArrayList<>();
        while (cursor != null && isInField(cursor) && board.get(cursor) instanceof Pawn) {
            sequence.add(cursor);
            cursor = cursor.toDir(mode, dir1);
        }

        return sequence;
    }

    @Override
    public IState removeToken(Coordinate c) {
        Map<Coordinate, Token> newBoard = new HashMap<>(board);
        newBoard.put(c, null);
        return new State(newBoard, turn, removedRings, mode);
    }

    @Override
    public IState toggleToken(Coordinate c, Class<? extends Token> tokenClass, Team team) {
        Map<Coordinate, Token> newBoard = new HashMap<>(board);
        Token existing = newBoard.get(c);

        if (existing != null && existing.getClass().equals(tokenClass) && existing.getTeam() == team) {
            newBoard.put(c, null);
        } else {
            try {
                Constructor<?>[] constructors = tokenClass.getConstructors();
                Constructor<?> constructor = constructors[0];
                Token newToken = (Token) constructor.newInstance(team);
                newBoard.put(c, newToken);
            } catch (Exception e) {
                throw new RuntimeException("Impossible de créer un token de type " + tokenClass.getName(), e);
            }
        }

        return new State(newBoard, turn, removedRings, mode);
    }

    @Override
    public boolean isInField(Coordinate c) {
        return board.containsKey(c);
    }

    public Mode getMode() {
        return mode;
    }

    public Map<Team, Integer> getRemovedRings() {
        return removedRings;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        State state = (State) o;
        return Objects.equals(board, state.board) && Objects.equals(turn, state.turn) && Objects.equals(removedRings, state.removedRings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn, removedRings);
    }
}