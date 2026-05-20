package iut.gon.model.tokens;

import iut.gon.model.Team;

/**
 * Classe abstraite représentant une unité du jeu.
 * <p>
 * Un Token appartient à une équipe et peut être représenté sous forme
 * textuelle. Il sert de base pour les différents types de pièces du jeu
 * (comme les pions ou les anneaux).
 * </p>
 */
public abstract class Token {

    /**
     * Équipe à laquelle appartient le token.
     */
    public Team team;

    /**
     * Crée un token appartenant à une équipe donnée.
     *
     * @param team équipe du token
     */
    public Token(Team team) {
        this.team = team;
    }

    /**
     * Retourne l'équipe du token.
     *
     * @return l'équipe du token
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Modifie l'équipe du token.
     *
     * @param team nouvelle équipe du token
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Retourne une représentation textuelle du token.
     *
     * @return caractère représentant le token
     */
    public abstract String charRepr();

    /**
     * Crée une copie du token.
     *
     * @return une nouvelle instance identique du token
     */
    public abstract Token clone();
}