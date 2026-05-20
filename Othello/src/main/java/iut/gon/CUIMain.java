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

/**
 * Interface en ligne de commande pour jouer à YINSH.
 *
 * Représentation du terrain :
 *   o = anneau blanc
 *   O = anneau noir
 *   _ = case vide
 *   . = pion blanc
 *   x = pion noir
 *
 * Les coordonnées sont saisies au format "q,r" (CoordinateCube) ou "row,col" (CoordinateDoubled).
 */
public class CUIMain {

    // -----------------------------------------------------------------------
    // Affichage du terrain
    // -----------------------------------------------------------------------

    /**
     * Convertit l'état courant en tableau 2D de caractères via les coordonnées Doubled,
     * puis l'affiche.
     */
    private static void printBoard(Model model) {
        Map<Coordinate, Token> board = model.getBoard();

        int maxRow = 10;
        int maxCol = 18;

        Token[][] tokenGrid  = new Token[maxRow + 1][maxCol + 1];
        boolean[][] inField  = new boolean[maxRow + 1][maxCol + 1];
        String[][] coordLabel = new String[maxRow + 1][maxCol + 1];

        for (Map.Entry<Coordinate, Token> entry : board.entrySet()) {
            Coordinate coord = entry.getKey();
            Token token = entry.getValue();
            Point p = coord.to2DCoordinate();
            int row = p.y();
            int col = p.x();
            if (row >= 0 && row <= maxRow && col >= 0 && col <= maxCol) {
                inField[row][col]    = true;
                tokenGrid[row][col]  = token;
                // Extraire q,r,s par réflexion (CoordinateCube peut ne pas surcharger toString)
                coordLabel[row][col] = coordToLabel(coord);
            }
        }

        // Largeur par case : assez large pour "−5,1,4" (6 chars) + marges
        int CELL = 9;
        int maxCases = 10; // rangée la plus large (row 4 et 6)

        for (int row = 0; row <= maxRow; row++) {
            int casesInRow = 0;
            for (int col = 0; col <= maxCol; col++) if (inField[row][col]) casesInRow++;
            int indentChars = ((maxCases - casesInRow) * CELL) / 2;
            String pad = " ".repeat(indentChars);

            StringBuilder lineA = new StringBuilder(pad); // symboles
            StringBuilder lineB = new StringBuilder(pad); // coordonnées

            for (int col = 0; col <= maxCol; col++) {
                if (!inField[row][col]) continue;
                Token t = tokenGrid[row][col];
                String sym = (t == null) ? "_" : t.charRepr();
                String lbl = coordLabel[row][col];
                lineA.append(center(sym, CELL));
                lineB.append(center(lbl, CELL));
            }
            System.out.println(lineA);
            System.out.println(lineB);
        }
    }


    /**
     * Extrait les coordonnées lisibles d'une Coordinate.
     */
    private static String coordToLabel(Coordinate coord) {
        if (coord instanceof iut.gon.Coordinate.CoordinateCube cc) {
            return cc.getQ() + "," + cc.getR() + "," + cc.getS();
        } else if (coord instanceof CoordinateDoubled cd) {
            return cd.getY() + "," + cd.getX();
        }
        return coord.toString();
    }


    private static String labelList(java.util.Collection<Coordinate> coords) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Coordinate c : coords) {
            if (!first) sb.append(", ");
            sb.append(coordToLabel(c));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private static String center(String s, int width) {
        if (s.length() >= width) return s;
        int total = width - s.length();
        int left  = total / 2;
        int right = total - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }

    // -----------------------------------------------------------------------
    // Initialisation aléatoire
    // -----------------------------------------------------------------------

    /**
     * Crée un état initial avec 5 anneaux par équipe placés aléatoirement sur des cases vides.
     */
    private static IState createRandomInitialState() {
        FactoryCube factory = new FactoryCube();
        IState empty = factory.emptyState();
        Map<Coordinate, Token> board = new HashMap<>(empty.board());
        Map<Team, Integer> removedRings = new HashMap<>();
        removedRings.put(Team.BLACK, 0);
        removedRings.put(Team.WHITE, 0);

        // Liste des cases vides (null)
        List<Coordinate> emptyCells = new ArrayList<>();
        for (Map.Entry<Coordinate, Token> e : board.entrySet()) {
            if (e.getValue() == null) emptyCells.add(e.getKey());
        }

        Random rng = new Random();
        Collections.shuffle(emptyCells, rng);

        // Placer 5 anneaux blancs puis 5 anneaux noirs
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

        // Récupérer le mode depuis l'état vide
        iut.gon.Coordinate.Mode mode = ((State) empty).getMode();
        return new State(board, Team.BLACK, removedRings, mode);
    }

    // -----------------------------------------------------------------------
    // Parsing des coordonnées
    // -----------------------------------------------------------------------

    /**
     * Lit une coordonnée depuis stdin au format "q r s" ou "row col".
     * Retourne null si l'entrée est invalide.
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
                // Chercher dans le board la coordonnée correspondante
                for (Coordinate c : model.getBoard().keySet()) {
                    if (c.toString().equals("(" + q + "," + r + "," + s + ")")) return c;
                    // Fallback : CoordinateCube directement
                    iut.gon.Coordinate.CoordinateCube cc = new iut.gon.Coordinate.CoordinateCube(q, r, s);
                    if (model.isInField(cc)) return cc;
                }
                iut.gon.Coordinate.CoordinateCube cc = new iut.gon.Coordinate.CoordinateCube(q, r, s);
                if (model.isInField(cc)) return cc;
            }
        } catch (Exception e) {
            // ignore
        }
        System.out.println("  Coordonnée invalide.");
        return null;
    }

    // -----------------------------------------------------------------------
    // Boucle principale
    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Interface en ligne de commande ===");
        System.out.println("Légende : o=anneau blanc  O=anneau noir  _=vide  .=pion blanc  x=pion noir");
        System.out.println();

        IState initial = createRandomInitialState();
        Model model = new Model(initial);

        // Boucle de jeu
        while (model.getWinner() == null) {
            printBoard(model);
            System.out.println();
            Team turn = model.getTurn();
            System.out.println("Tour : " + (turn == Team.BLACK ? "NOIR (O/x)" : "BLANC (o/.)"));

            // --- Gestion des lignes à supprimer ---
            List<Set<Coordinate>> lines = model.getPawnsLines();
            while (!lines.isEmpty()) {
                System.out.println("Une ligne de 5 pions doit être supprimée !");
                System.out.println("Lignes disponibles :");
                for (int i = 0; i < lines.size(); i++) {
                    System.out.println("  [" + i + "] " + lines.get(i));
                }

                // Choisir une ligne
                System.out.print("Choisissez une ligne (indice) : ");
                int idx = -1;
                try { idx = Integer.parseInt(sc.nextLine().trim()); } catch (Exception ignored) {}
                if (idx < 0 || idx >= lines.size()) {
                    System.out.println("Indice invalide.");
                    continue;
                }
                Set<Coordinate> chosenLine = lines.get(idx);

                // Choisir un anneau à retirer
                System.out.println("Choisissez un anneau à retirer (coordonnées q r s) :");
                List<Coordinate> myRings = model.getRings(turn);
                System.out.println("  Vos anneaux : " + labelList(myRings));
                Coordinate ringCoord = readCoordinate(sc, model);
                if (ringCoord == null) continue;

                try {
                    model.removeLine(chosenLine, ringCoord);
                    System.out.println("Ligne et anneau supprimés.");
                } catch (Exception e) {
                    System.out.println("Erreur : " + e.getMessage());
                }

                if (model.getWinner() != null) break;
                lines = model.getPawnsLines();
                printBoard(model);
            }

            if (model.getWinner() != null) break;

            // --- Déplacement d'anneau ---
            System.out.println("Déplacez un anneau. Vos anneaux : " + labelList(model.getRings(turn)));
            System.out.println("Coordonnée de départ (q r s) :");
            Coordinate from = readCoordinate(sc, model);
            if (from == null) continue;

            Set<Coordinate> moves = model.movesFrom(from);
            if (moves.isEmpty()) {
                System.out.println("Aucun mouvement possible depuis cette case.");
                continue;
            }
            System.out.println("Destinations possibles : " + labelList(moves));
            System.out.println("Coordonnée de destination (q r s) :");
            Coordinate to = readCoordinate(sc, model);
            if (to == null) continue;

            try {
                model.moveRing(from, to);
            } catch (Exception e) {
                System.out.println("Mouvement invalide : " + e.getMessage());
            }

            System.out.println();
        }

        // Fin de partie
        printBoard(model);
        System.out.println();
        System.out.println("=== PARTIE TERMINÉE ===");
        Team winner = model.getWinner();
        System.out.println("Gagnant : " + (winner == Team.BLACK ? "NOIR" : "BLANC") + " !");
    }
}