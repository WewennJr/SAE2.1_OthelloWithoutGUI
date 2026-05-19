package iut.gon.model.state;

import iut.gon.hexcoordinate.Coordinate;
import iut.gon.hexcoordinate.Direction;
import iut.gon.hexcoordinate.Mode;
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
        Coordinate to = move.getTo();

        // Lever RuntimeException s'il y a une ligne à supprimer
        if (!lines().isEmpty()) {
            throw new RuntimeException("Une ligne doit être supprimée avant de jouer.");
        }

        // Lever IndexOutOfBoundsException si une coordonnée est hors terrain
        if (!isInField(from)) {
            throw new IndexOutOfBoundsException("Coordonnée de départ hors du terrain : " + from);
        }
        if (!isInField(to)) {
            throw new IndexOutOfBoundsException("Coordonnée d'arrivée hors du terrain : " + to);
        }

        // Lever IllegalArgumentException si pas d'anneau du joueur courant en 'from'
        Token tokenAt = board.get(from);
        if (!(tokenAt instanceof Ring) || tokenAt.getTeam() != turn) {
            throw new IllegalArgumentException("Pas d'anneau du joueur courant en " + from);
        }

        // Lever IllegalArgumentException si la destination n'est pas atteignable
        Set<Coordinate> reachable = availableMove(from);
        if (!reachable.contains(to)) {
            throw new IllegalArgumentException("La case " + to + " n'est pas atteignable depuis " + from);
        }

        // Construction du nouveau plateau
        Map<Coordinate, Token> newBoard = new HashMap<>(board);

        // 1. Retirer l'anneau de sa position de départ
        newBoard.remove(from);

        // 2. Retourner tous les pions survolés entre from et to
        List<Coordinate> between = from.between(mode, to);
        for (Coordinate c : between) {
            Token t = newBoard.get(c);
            if (t instanceof Pawn) {
                // On crée un nouveau pion de l'équipe opposée (immuabilité)
                Team opposite = (t.getTeam() == Team.BLACK) ? Team.WHITE : Team.BLACK;
                newBoard.put(c, new Pawn(opposite));
            }
        }

        // 3. Placer un pion de la couleur de l'anneau à l'ancienne position
        newBoard.put(from, new Pawn(turn));

        // 4. Placer l'anneau à sa nouvelle position
        newBoard.put(to, new Ring(turn));

        // Le tour ne change que s'il n'y a pas de ligne créée pour l'équipe courante
        // (priorité à l'équipe courante si les deux ont une ligne)
        List<Set<Coordinate>> newLines = getPawnsLines(newBoard);
        Team nextTurn = turn;
        // Si aucune ligne à supprimer, on passe au joueur suivant
        if (newLines.isEmpty()) {
            if (turn == Team.BLACK) {
                nextTurn = Team.WHITE;
            }
            else {
                nextTurn = Team.BLACK;
            }
        }
        // Si seulement des lignes adverses existent, on passe aussi la main
        // mais la consigne dit : priorité à l'équipe courante, donc on garde le tour courant
        // tant que l'équipe courante a au moins une ligne à retirer.
        boolean currentHasLine = newLines.stream().anyMatch(line -> line.stream().anyMatch(c -> newBoard.get(c) != null && newBoard.get(c).getTeam() == turn));
        if (!newLines.isEmpty() && !currentHasLine) {
            // Seule l'équipe adverse a une ligne : on lui passe le tour
            nextTurn = (turn == Team.BLACK) ? Team.WHITE : Team.BLACK;
        }

        return new State(newBoard, nextTurn, removedRings, mode);
    }

    @Override
    public IState removeLine(RemoveLine removeLine) {
        Set<Coordinate> line = removeLine.getLine();
        Coordinate ringCoord = removeLine.getRing();

        // Lever RuntimeException si aucune ligne n'est présente
        List<Set<Coordinate>> currentLines = lines();
        if (currentLines.isEmpty()) {
            throw new RuntimeException("Aucune ligne à supprimer sur le plateau.");
        }

        // Valider la ligne : exactement 5 cases
        if (line.size() != LINE_LENGTH) {
            throw new RuntimeException("La ligne doit contenir exactement 5 cases, reçu : " + line.size());
        }

        // Valider que la ligne existe sur le plateau
        boolean lineExists = currentLines.stream().anyMatch(l -> l.equals(line));
        if (!lineExists) {
            throw new RuntimeException("La ligne fournie n'existe pas sur le plateau.");
        }

        // Valider que tous les pions sont de la même équipe
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

        // Valider que l'anneau à retirer appartient au joueur courant
        Token ringToken = board.get(ringCoord);
        if (!(ringToken instanceof Ring) || ringToken.getTeam() != turn) {
            throw new RuntimeException("La coordonnée de l'anneau ne contient pas un anneau du joueur courant.");
        }

        // Construction du nouveau plateau
        Map<Coordinate, Token> newBoard = new HashMap<>(board);

        // Supprimer les 5 pions de la ligne
        for (Coordinate c : line) {
            newBoard.remove(c);
        }

        // Supprimer l'anneau choisi
        newBoard.remove(ringCoord);

        // Mettre à jour le compteur d'anneaux retirés
        Map<Team, Integer> newRemovedRings = new HashMap<>(removedRings);
        newRemovedRings.put(turn, newRemovedRings.getOrDefault(turn, 0) + 1);

        // Vérifier s'il reste des lignes à supprimer
        List<Set<Coordinate>> remainingLines = getPawnsLines(newBoard);
        Team nextTurn = turn;
        if (remainingLines.isEmpty()) {
            // Plus de lignes : on passe au joueur suivant
            nextTurn = (turn == Team.BLACK) ? Team.WHITE : Team.BLACK;
        } else {
            // Il peut rester des lignes pour les deux équipes
            boolean currentStillHasLine = remainingLines.stream()
                    .anyMatch(l -> l.stream()
                            .anyMatch(c -> newBoard.get(c) != null && newBoard.get(c).getTeam() == turn));
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

        // Les 6 directions du plateau hexagonal
        Direction[] directions = Direction.values();

        for (Direction dir : directions) {
            Coordinate current = from.toDir(mode, dir);
            boolean jumpedOverPawn = false;

            while (current != null && isInField(current)) {
                Token tokenHere = board.get(current);

                if (tokenHere instanceof Ring) {
                    // Un anneau bloque le passage : on ne peut pas aller plus loin ni s'arrêter ici
                    break;
                } else if (tokenHere instanceof Pawn) {
                    // On peut sauter par-dessus des pions
                    jumpedOverPawn = true;
                    current = current.toDir(mode, dir);
                } else {
                    // Case vide
                    if (jumpedOverPawn) {
                        // Après avoir sauté des pions, on DOIT s'arrêter à la première case vide
                        result.add(current);
                        break;
                    } else {
                        // Pas encore de pion sauté : on peut s'arrêter ici ou continuer
                        result.add(current);
                        current = current.toDir(mode, dir);
                    }
                }
            }
        }

        return result;
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
     */
    @Override
    public List<Set<Coordinate>> getPawnsLines(Map<Coordinate, Token> board) {
        List<Set<Coordinate>> result = new ArrayList<>();

        // On ne parcourt que les 3 axes (on évite les doublons en ne prenant que la moitié des directions)
        Direction[][] axes = {
                {Direction.E, Direction.O},
                {Direction.NE, Direction.SO},
                {Direction.NO, Direction.SE}
        };

        // Pour chaque case contenant un pion, on vérifie les 3 axes
        Set<Coordinate> visited = new HashSet<>();

        for (Map.Entry<Coordinate, Token> entry : board.entrySet()) {
            if (!(entry.getValue() instanceof Pawn)) {
                continue;
            }
            Coordinate start = entry.getKey();

            for (Direction[] axis : axes) {
                // Construire la séquence complète de pions consécutifs sur cet axe
                List<Coordinate> sequence = buildSequence(board, start, axis[0], axis[1]);

                if (sequence.size() >= LINE_LENGTH) {
                    // Extraire toutes les sous-séquences de 5 consécutifs
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
    private List<Coordinate> buildSequence(Map<Coordinate, Token> board, Coordinate center, Direction dir1, Direction dir2) {
        // Aller vers dir2 (direction négative) pour trouver le début de la séquence
        Coordinate cursor = center;
        List<Coordinate> sequence = new ArrayList<>();

        // Reculer jusqu'au bout de la séquence continue
        while (cursor != null && isInField(cursor) && board.get(cursor) instanceof Pawn) {
            cursor = cursor.toDir(mode, dir2);
        }
        // cursor est maintenant juste avant le début, on avance d'une case
        if (cursor == null) {
            cursor = center; // cas limite : on repart du centre
        } else {
            cursor = cursor.toDir(mode, dir1);
        }

        // Avancer vers dir1 et collecter les pions consécutifs
        while (cursor != null && isInField(cursor) && board.get(cursor) instanceof Pawn) {
            sequence.add(cursor);
            cursor = cursor.toDir(mode, dir1);
        }

        return sequence;
    }

    @Override
    public IState removeToken(Coordinate c) {
        Map<Coordinate, Token> newBoard = new HashMap<>(board);
        newBoard.remove(c);
        return new State(newBoard, turn, removedRings, mode);
    }

    @Override
    public IState toggleToken(Coordinate c, Class<? extends Token> tokenClass, Team team) {
        Map<Coordinate, Token> newBoard = new HashMap<>(board);
        Token existing = newBoard.get(c);

        if (existing != null && existing.getClass().equals(tokenClass) && existing.getTeam() == team) {
            // Même classe et même équipe : on supprime
            newBoard.remove(c);
        } else {
            // Sinon on crée dynamiquement une instance via réflexion
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