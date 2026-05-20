package iut.gon;

import java.util.Scanner;

/**
 * Point d'entrée principal du programme.
 * Permet de choisir entre une partie Joueur vs Joueur ou Joueur vs IA.
 */
public class Main {

    /**
     * Lance le menu principal du jeu et redirige vers le mode choisi.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println(">-----< Jeu Othello >-----<");
        System.out.println();
        System.out.println("  [1] Joueur vs Joueur");
        System.out.println("  [2] Joueur vs IA");
        System.out.println();

        int choice = 0;
        while (choice != 1 && choice != 2) {
            System.out.print("Votre choix (1/2) : ");
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                // ignore
            }
            if (choice != 1 && choice != 2) System.out.println("  Répondez 1 ou 2.");
        }

        System.out.println();

        if (choice == 1) {
            CUIMain.main(args);
        } else {
            MainAI.main(args);
        }
    }
}