package iut.gon;

import iut.gon.Coordinate.Coordinate;
import iut.gon.Coordinate.CoordinateDoubled;
import iut.gon.Coordinate.Point;
import iut.gon.model.Model;
import iut.gon.model.Team;
import iut.gon.model.factory.FactoryCube;
import iut.gon.model.state.IState;
import iut.gon.model.state.State;
import iut.gon.model.tokens.Pawn;
import iut.gon.model.tokens.Ring;
import iut.gon.model.tokens.Token;

import java.util.*;

public class CUIMain {

    private static void printBoard(Model model) {
        Map<Coordinate, Token> board = model.getBoard();

        int maxRow = 10;
        int maxCol = 18;

        Token[][] tokenGrid = new Token[maxRow + 1][maxCol + 1];
        String[][] coordLabel = new String[maxRow + 1][maxCol + 1];

        // On garde TOUTES les cases du plateau fixes
        for (Map.Entry<Coordinate, Token> entry : board.entrySet()) {
            Coordinate coord = entry.getKey();
            Token token = entry.getValue();

            Point p = coord.to2DCoordinate();
            int row = p.y();
            int col = p.x();

            if (row >= 0 && row <= maxRow && col >= 0 && col <= maxCol) {
                tokenGrid[row][col] = token;
                coordLabel[row][col] = coordToLabel(coord);
            }
        }

        int CELL = 9;

        for (int row = 0; row <= maxRow; row++) {

            StringBuilder lineA = new StringBuilder();
            StringBuilder lineB = new StringBuilder();

            for (int col = 0; col <= maxCol; col++) {

                // Si cette position ne correspond à aucune case du plateau
                if (coordLabel[row][col] == null) {
                    lineA.append(" ".repeat(CELL));
                    lineB.append(" ".repeat(CELL));
                    continue;
                }

                Token t = tokenGrid[row][col];

                // Case vide -> "_"
                String sym = (t == null) ? "_" : t.charRepr();

                lineA.append(center(sym, CELL));
                lineB.append(center(coordLabel[row][col], CELL));
            }

            System.out.println(lineA);
            System.out.println(lineB);
        }
    }

    private static String coordToLabel(Coordinate coord) {
        if (coord instanceof iut.gon.Coordinate.CoordinateCube cc) {
            return cc.getQ() + " " + cc.getR() + " " + cc.getS();
        } else if (coord instanceof CoordinateDoubled cd) {
            return cd.getY() + " " + cd.getX();
        }
        return coord.toString();
    }

    private static String labelList(java.util.Collection<Coordinate> coords) {
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

        // Liste des cases vides
        List<Coordinate> emptyCells = new ArrayList<>();
        for (Map.Entry<Coordinate, Token> e : board.entrySet()) {
            if (e.getValue() == null) {
                emptyCells.add(e.getKey());
            }
        }

        Random rng = new Random();
        Collections.shuffle(emptyCells, rng);

        // Placer 5 anneaux blancs puis 5 noirs
        int placed = 0;
        for (Coordinate c : emptyCells) {
            if (placed < 5) {
                board.put(c, new Ring(Team.WHITE));
            } else if (placed < 10) {
                board.put(c, new Ring(Team.BLACK));
            } else {
                break;
            }
            placed++;
        }

        // État vide
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
                // Chercher dans le board la bonne coordonnée
                for (Coordinate c : model.getBoard().keySet()) {
                    if (c.toString().equals("(" + q + "," + r + "," + s + ")")) {
                        return c;
                    }
                    iut.gon.Coordinate.CoordinateCube cc = new iut.gon.Coordinate.CoordinateCube(q, r, s);
                    if (model.isInField(cc)) {
                        return cc;
                    }
                }
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

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println(">-----< Player vs Player >-----<");
        System.out.println();
        System.out.println("Règles du jeu :");
        System.out.println("  ▸ Forme des lignes de 5 pions pour gagner des anneaux (3 retirés = victoire).");
        System.out.println("  ▸ Les anneaux se déplacent sur les 6 directions hexagonales selon les obstacles.");
        System.out.println("  ▸ La case de départ devient un pion et les pions survolés changent de couleur.");
        System.out.println("  ▸ Chaque ligne de 5 pions permet de retirer un anneau et la ligne correspondante.");
        System.out.println();
        System.out.println("Légende :   o : Anneau Blanc    . : Pion Blanc");
        System.out.println("            O : Anneau Noir     x : Pion Noir");
        System.out.println("            _ : Case vide");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Appuyez sur Entrée pour commencer ...");
        scanner.nextLine();

        IState initial = createRandomInitialState();
        Model model = new Model(initial);

        while (model.getWinner() == null) {
            printBoard(model);
            System.out.println();
            Team turn = model.getTurn();
            System.out.println("Tour : " + (turn == Team.BLACK ? "NOIR (O/x)" : "BLANC (o/.)"));

            List<Set<Coordinate>> lines = model.getPawnsLines();
            while (!lines.isEmpty()) {
                System.out.println("Une ligne de 5 pions doit être supprimée !");
                System.out.println("Lignes disponibles :");
                for (int i = 0; i < lines.size(); i++) {
                    System.out.println("  [" + i + "] " + labelList(lines.get(i)));
                }

                System.out.print("Choisissez une ligne (indice) : ");
                int idx = -1;
                try {
                    idx = Integer.parseInt(sc.nextLine().trim());
                } catch (Exception ignored) {
                    // ignore
                }
                if (idx < 0 || idx >= lines.size()) {
                    System.out.println("Indice invalide.");
                    continue;
                }
                Set<Coordinate> chosenLine = lines.get(idx);

                System.out.println("Choisissez un anneau à retirer (coordonnées q r s) :");
                List<Coordinate> myRings = model.getRings(turn);
                System.out.println("  Vos anneaux : " + labelList(myRings));
                Coordinate ringCoord = readCoordinate(sc, model);
                if (ringCoord == null) {
                    continue;
                }

                try {
                    model.removeLine(chosenLine, ringCoord);
                    System.out.println("Ligne et anneau supprimés.");
                } catch (Exception e) {
                    System.out.println("Erreur : " + e.getMessage());
                }

                if (model.getWinner() != null) {
                    break;
                }
                lines = model.getPawnsLines();
                printBoard(model);
            }

            if (model.getWinner() != null) break;

            System.out.println("Vos anneaux : " + labelList(model.getRings(turn)));
            System.out.println("Coordonnée de départ (q r s) :");
            Coordinate from = readCoordinate(sc, model);
            if (from == null) {
                continue;
            }

            Set<Coordinate> moves = model.movesFrom(from);
            if (moves.isEmpty()) {
                System.out.println("Aucun mouvement possible depuis cette case.");
                continue;
            }
            System.out.println("Destinations possibles : " + labelList(moves));
            System.out.println("Coordonnée de destination (q r s) :");
            Coordinate to = readCoordinate(sc, model);
            if (to == null) {
                continue;
            }

            try {
                model.moveRing(from, to);
            } catch (Exception e) {
                System.out.println("Mouvement invalide : " + e.getMessage());
            }

            System.out.println();
        }

        printBoard(model);
        System.out.println();
        System.out.println(">-----< PARTIE TERMINÉE >-----<");
        Team winner = model.getWinner();
        System.out.println("Gagnant : " + (winner == Team.BLACK ? "NOIR" : "BLANC") + " !");
    }
}