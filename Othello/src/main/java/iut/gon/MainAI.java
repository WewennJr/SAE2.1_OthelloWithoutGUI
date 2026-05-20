package iut.gon;

import iut.gon.Coordinate.Coordinate;
import iut.gon.Coordinate.Point;
import iut.gon.model.Model;
import iut.gon.model.Team;
import iut.gon.model.IA.AI;
import iut.gon.model.IA.MinimaxAI;
import iut.gon.model.actions.Action;
import iut.gon.model.actions.Move;
import iut.gon.model.actions.RemoveLine;
import iut.gon.model.factory.FactoryCube;
import iut.gon.model.state.IState;
import iut.gon.model.state.State;
import iut.gon.model.tokens.Ring;
import iut.gon.model.tokens.Token;

import java.util.*;

/**
 * Point d'entrée principal du jeu en mode joueur contre IA Minimax.
 * Gère l'affichage, les tours de jeu et l'intégration de l'IA.
 */
public class MainAI {

    /**
     * Affiche le plateau de jeu dans la console sous forme de grille fixe.
     * Les coordonnées restent à position constante, même après suppression de pions.
     *
     * @param model modèle contenant l'état courant du jeu
     */
    private static void printBoard(Model model) {
        Map<Coordinate, Token> board = model.getBoard();

        int maxRow = 10;
        int maxCol = 18;

        Token[][] tokenGrid = new Token[maxRow + 1][maxCol + 1];
        String[][] labelGrid = new String[maxRow + 1][maxCol + 1];

        // Remplissage direct sans recalcul dynamique
        for (Map.Entry<Coordinate, Token> entry : board.entrySet()) {
            Coordinate coord = entry.getKey();
            Token token = entry.getValue();

            Point p = coord.to2DCoordinate();
            int row = p.y();
            int col = p.x();

            if (row >= 0 && row <= maxRow && col >= 0 && col <= maxCol) {
                tokenGrid[row][col] = token;
                labelGrid[row][col] = coordToLabel(coord);
            }
        }

        int CELL = 9;

        for (int row = 0; row <= maxRow; row++) {

            StringBuilder lineA = new StringBuilder();
            StringBuilder lineB = new StringBuilder();

            for (int col = 0; col <= maxCol; col++) {

                // Case inexistante du plateau
                if (labelGrid[row][col] == null) {
                    lineA.append(" ".repeat(CELL));
                    lineB.append(" ".repeat(CELL));
                    continue;
                }

                Token t = tokenGrid[row][col];

                String sym = (t == null) ? "_" : t.charRepr();

                lineA.append(center(sym, CELL));
                lineB.append(center(labelGrid[row][col], CELL));
            }

            System.out.println(lineA);
            System.out.println(lineB);
        }
    }

    /**
     * Convertit une coordonnée du jeu en représentation textuelle.
     *
     * @param coord coordonnée à convertir
     * @return chaîne lisible représentant la coordonnée
     */
    private static String coordToLabel(Coordinate coord) {
        if (coord instanceof iut.gon.Coordinate.CoordinateCube cc) {
            return cc.getQ() + " " + cc.getR() + " " + cc.getS();
        } else if (coord instanceof iut.gon.Coordinate.CoordinateDoubled cd) {
            return cd.getY() + " " + cd.getX();
        }
        return coord.toString();
    }

    /**
     * Convertit une collection de coordonnées en chaîne lisible.
     *
     * @param coords ensemble de coordonnées
     * @return représentation textuelle
     */
    private static String labelList(Collection<Coordinate> coords) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Coordinate c : coords) {
            if (!first) {
                sb.append(" ; ");
            }
            sb.append(coordToLabel(c));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Centre une chaîne de caractères dans une largeur donnée.
     *
     * @param s chaîne à centrer
     * @param width largeur totale
     * @return chaîne centrée
     */
    private static String center(String s, int width) {
        if (s.length() >= width) {
            return s;
        }
        int total = width - s.length();
        int left = total / 2;
        int right = total - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }

    /**
     * Crée un état initial aléatoire avec placement de quelques anneaux.
     *
     * @return état initial du jeu
     */
    private static IState createRandomInitialState() {
        FactoryCube factory = new FactoryCube();
        IState empty = factory.emptyState();
        Map<Coordinate, Token> board = new HashMap<>(empty.board());
        Map<Team, Integer> removedRings = new HashMap<>();
        removedRings.put(Team.BLACK, 0);
        removedRings.put(Team.WHITE, 0);

        List<Coordinate> emptyCells = new ArrayList<>();
        for (Map.Entry<Coordinate, Token> e : board.entrySet()) {
            if (e.getValue() == null) {
                emptyCells.add(e.getKey());
            }
        }

        Random rng = new Random();
        Collections.shuffle(emptyCells, rng);

        int placed = 0;
        for (Coordinate c : emptyCells) {
            if (placed < 5) {
                board.put(c, new Ring(Team.WHITE));
            }
            else if (placed < 10) {
                board.put(c, new Ring(Team.BLACK));
            }
            else {
                break;
            }
            placed++;
        }

        iut.gon.Coordinate.Mode mode = ((State) empty).getMode();
        return new State(board, Team.BLACK, removedRings, mode);
    }

    /**
     * Lit une coordonnée entrée par l'utilisateur dans la console.
     *
     * @param sc scanner d'entrée
     * @param model modèle du jeu
     * @return coordonnée valide ou null si invalide
     */
    private static Coordinate readCoordinate(Scanner sc, Model model) {
        System.out.print("  Entrez une coordonnée (q r s) : ");
        try {
            String line = sc.nextLine().trim();
            String[] parts = line.split("\\s+");
            if (parts.length == 3) {
                int q = Integer.parseInt(parts[0]);
                int r = Integer.parseInt(parts[1]);
                int s = Integer.parseInt(parts[2]);
                iut.gon.Coordinate.CoordinateCube cc = new iut.gon.Coordinate.CoordinateCube(q, r, s);
                if (model.isInField(cc)) {
                    return cc;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        System.out.println("  Coordonnée invalide.");
        return null;
    }

    /**
     * Gère le tour d'un joueur humain.
     *
     * @param sc scanner
     * @param model modèle du jeu
     */
    private static void humanTurn(Scanner sc, Model model) {
        Team turn = model.getTurn();

        List<Set<Coordinate>> lines = model.getPawnsLines();
        while (!lines.isEmpty()) {
            System.out.println("  Une ligne de 5 pions doit être supprimée !");
            System.out.println("  Lignes disponibles :");
            for (int i = 0; i < lines.size(); i++) {
                System.out.println("    [" + i + "] " + labelList(lines.get(i)));
            }

            System.out.print("  Choisissez une ligne (indice) : ");
            int idx = -1;
            try {
                idx = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception ignored) {
                // gnore
            }
            if (idx < 0 || idx >= lines.size()) {
                System.out.println("  Indice invalide, réessayez.");
                continue;
            }
            Set<Coordinate> chosenLine = lines.get(idx);

            System.out.println("  Vos anneaux : " + labelList(model.getRings(turn)));
            Coordinate ringCoord = readCoordinate(sc, model);
            if (ringCoord == null) {
                continue;
            }

            try {
                model.removeLine(chosenLine, ringCoord);
                System.out.println("  ✓ Ligne et anneau supprimés.");
            } catch (Exception e) {
                System.out.println("  Erreur : " + e.getMessage());
                continue;
            }

            if (model.getWinner() != null) {
                return;
            }
            lines = model.getPawnsLines();
            printBoard(model);
        }

        if (model.getWinner() != null) {
            return;
        }

        while (true) {
            System.out.println("  Vos anneaux : " + labelList(model.getRings(turn)));
            System.out.println("  Coordonnée de départ (q r s) :");
            Coordinate from = readCoordinate(sc, model);
            if (from == null) {
                continue;
            }

            Set<Coordinate> moves = model.movesFrom(from);
            if (moves.isEmpty()) {
                System.out.println("  Aucun mouvement possible depuis cette case, choisissez un autre anneau.");
                continue;
            }
            System.out.println("  Destinations possibles : " + labelList(moves));
            System.out.println("  Coordonnée de destination (q r s) :");
            Coordinate to = readCoordinate(sc, model);
            if (to == null) {
                continue;
            }

            try {
                model.moveRing(from, to);
                break;
            } catch (Exception e) {
                System.out.println("  Mouvement invalide : " + e.getMessage());
            }
        }
    }

    /**
     * Gère le tour de l'IA.
     *
     * @param model modèle du jeu
     * @param ai intelligence artificielle
     */
    private static void aiTurn(Model model, AI ai) {
        System.out.println("  L'IA réfléchit...");
        IState currentState = model.getCurrentState();

        Action action = ai.chooseMove(currentState);

        if (action == null) {
            System.out.println("  L'IA n'a trouvé aucun coup valide.");
            return;
        }

        if (action instanceof Move move) {
            System.out.println("  L'IA déplace un anneau : " + coordToLabel(move.getFrom()) + "  →  " + coordToLabel(move.getTo()));
            model.moveRing(move.getFrom(), move.getTo());
        } else if (action instanceof RemoveLine rl) {
            System.out.println("  L'IA supprime une ligne : " + labelList(rl.getLine()));
            System.out.println("  L'IA retire son anneau en : " + coordToLabel(rl.getRing()));
            model.removeLine(rl.getLine(), rl.getRing());
        }

        List<Set<Coordinate>> remaining = model.getPawnsLines();
        while (!remaining.isEmpty() && model.getWinner() == null) {
            action = ai.chooseMove(model.getCurrentState());
            if (action instanceof RemoveLine rl) {
                System.out.println("  L'IA supprime encore une ligne : " + labelList(rl.getLine()));
                System.out.println("  L'IA retire son anneau en : " + coordToLabel(rl.getRing()));
                try {
                    model.removeLine(rl.getLine(), rl.getRing());
                } catch (Exception e) {
                    System.out.println("  (IA - erreur suppression) : " + e.getMessage());
                    break;
                }
            } else {
                break;
            }
            remaining = model.getPawnsLines();
        }
    }

    /**
     * Programme principal lançant une partie joueur contre IA.
     *
     * @param args arguments CLI
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println(">-----< Player vs MinimaxAI >-----<");

        Team humanTeam = null;
        while (humanTeam == null) {
            System.out.print("Voulez-vous jouer avec les NOIRS ou les BLANCS ? (n/b) : ");
            String choice = sc.nextLine().trim().toLowerCase();
            if (choice.equals("n")) {
                humanTeam = Team.BLACK;
            }
            else if (choice.equals("b")) {
                humanTeam = Team.WHITE;
            }
            else {
                System.out.println("  Répondez 'n' pour Noir ou 'b' pour Blanc.");
            }
        }

        Team aiTeam = (humanTeam == Team.BLACK) ? Team.WHITE : Team.BLACK;

        System.out.println("Appuyez sur Entrée pour commencer ...");
        sc.nextLine();

        IState initial = createRandomInitialState();
        Model model = new Model(initial);
        AI ai = new MinimaxAI(aiTeam);

        while (model.getWinner() == null) {
            printBoard(model);

            if (model.getTurn() == humanTeam) {
                humanTurn(sc, model);
            } else {
                aiTurn(model, ai);
            }
        }

        printBoard(model);
        System.out.println(">-----< PARTIE TERMINÉE >-----<");
    }
}