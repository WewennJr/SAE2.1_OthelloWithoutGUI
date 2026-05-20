package iut.gon.model.IA;

import iut.gon.model.actions.Action;
import iut.gon.model.state.IState;

/**
 * Interface représentant une intelligence artificielle capable de choisir une action de jeu.
 * <p>
 * Une implémentation de cette interface doit analyser un état de jeu et proposer
 * une action valide à jouer.
 * </p>
 */
public interface AI {

    /**
     * Choisit un mouvement à effectuer en fonction de l'état actuel du jeu.
     *
     * @param state état actuel du jeu
     * @return action choisie par l'IA
     */
    public Action chooseMove(IState state);
}