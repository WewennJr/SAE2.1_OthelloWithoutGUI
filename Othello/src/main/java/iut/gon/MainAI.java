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

public class MainAI {

    private static void printBoard(Model model) {
        Map<Coordinate, Token> board = model.getBoard();

        int maxRow = 10;
        int maxCol = 18;

        Token[][] tokenGrid = new Token[maxRow + 1][maxCol + 1];
        boolean[][] inField = new boolean[maxRow + 1][maxCol + 1];

        for (Map.Entry<Coordinate, Token> entry : board.entrySet()) {
            Coordinate coord = entry.getKey();
            Token token = entry.getValue();
            Point p = coord.to2DCoordinate();
            int row = p.y();
            int col = p.x();
            if (row >= 0 && row <= maxRow && col >= 0 && col <= maxCol) {
                inField[row][col]   = true;
                tokenGrid[row][col] = token;
            }
        }

        int CELL = 9;
        int maxCases = 10;

        for (int row = 0; row <= maxRow; row++) {
            int casesInRow = 0;
            for (int col = 0; col <= maxCol; col++) {
                if (inField[row][col]) {
                    casesInRow++;
                }
            }
            int indentChars = ((maxCases - casesInRow) * CELL) / 2;
            String pad = " ".repeat(indentChars);

            StringBuilder lineA = new StringBuilder(pad);
            StringBuilder lineB = new StringBuilder(pad);

            for (int col = 0; col <= maxCol; col++) {
                if (!inField[row][col]) continue;
                Token t = tokenGrid[row][col];
                String sym = (t == null) ? "_" : t.charRepr();
                // Retrouver la coordonnée pour l'afficher
                String lbl = "";
                for (Coordinate c : board.keySet()) {
                    Point p2 = c.to2DCoordinate();
                    if (p2.y() == row && p2.x() == col) {
                        lbl = coordToLabel(c);
                        break;
                    }
                }
                lineA.append(center(sym, CELL));
                lineB.append(center(lbl, CELL));
            }
            System.out.println(lineA);
            System.out.println(lineB);
        }
    }

    private static String coordToLabel(Coordinate coord) {
        if (coord instanceof iut.gon.Coordinate.CoordinateCube cc) {
            return cc.getQ() + " " + cc.getR() + " " + cc.getS();
        } else if (coord instanceof iut.gon.Coordinate.CoordinateDoubled cd) {
            return cd.getY() + " " + cd.getX();
        }
        return coord.toString();
    }

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

    private static String center(String s, int width) {
        if (s.length() >= width) {
            return s;
        }
        int total = width - s.length();
        int left = total / 2;
        int right = total - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }

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

        // Déplacement d'anneau
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

    private static void aiTurn(Model model, AI ai) {
        System.out.println("  L'IA réfléchit...");
        IState currentState = model.getCurrentState();

        // L'IA peut avoir plusieurs actions à jouer si des lignes s'enchaînent
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

        // Supprimer les lignes restantes si nécessaire (l'IA continue)
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

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println(">-----< Player vs MinimaxAI >-----<");
        System.out.println();
        System.out.println("Règles du jeu :");
        System.out.println("  ▸ Formez des lignes de 5 pions pour gagner des anneaux (3 retirés = victoire).");
        System.out.println("  ▸ Les anneaux se déplacent sur les 6 directions hexagonales selon les obstacles.");
        System.out.println("  ▸ La case de départ devient un pion et les pions survolés changent de couleur.");
        System.out.println("  ▸ Chaque ligne de 5 pions permet de retirer un anneau et la ligne correspondante.");
        System.out.println();
        System.out.println("Légende :   o : Anneau Blanc    . : Pion Blanc");
        System.out.println("            O : Anneau Noir     x : Pion Noir");
        System.out.println("            _ : Case vide");
        System.out.println();

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
        System.out.println();
        System.out.println("  Vous jouez : " + (humanTeam == Team.BLACK ? "NOIR (O/x)" : "BLANC (o/.)"));
        System.out.println("  L'IA joue  : " + (aiTeam    == Team.BLACK ? "NOIR (O/x)" : "BLANC (o/.)"));
        System.out.println();
        System.out.println("Appuyez sur Entrée pour commencer ...");
        sc.nextLine();

        IState initial = createRandomInitialState();
        Model model = new Model(initial);
        AI ai = new MinimaxAI(aiTeam);

        while (model.getWinner() == null) {
            printBoard(model);
            System.out.println();

            Team turn = model.getTurn();

            if (turn == humanTeam) {
                System.out.println(">>> Votre tour (" + (humanTeam == Team.BLACK ? "NOIR" : "BLANC") + ")");
                humanTurn(sc, model);
            } else {
                System.out.println(">>> Tour de l'IA (" + (aiTeam == Team.BLACK ? "NOIR" : "BLANC") + ")");
                aiTurn(model, ai);
            }

            System.out.println();
        }

        printBoard(model);
        System.out.println();
        System.out.println(">-----< PARTIE TERMINÉE >-----<");
        Team winner = model.getWinner();
        String winnerLabel = (winner == Team.BLACK ? "NOIR" : "BLANC");
        if (winner == humanTeam) {
            System.out.println("Félicitations, vous avez gagné ! (" + winnerLabel + ")");
        } else {
            System.out.println("L'IA a gagné. (" + winnerLabel + ") Bonne chance la prochaine fois !");
        }
    }
}