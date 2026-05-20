package iut.gon.model.factory;

import iut.gon.model.state.IState;

/**
 * Interface de fabrique permettant de générer différents états de jeu.
 * <p>
 * Elle définit les méthodes de création de divers états prédéfinis,
 * utilisés notamment pour les tests, les scénarios de jeu ou l'initialisation.
 * </p>
 */
public interface IFactory {

    /**
     * Retourne un état de test général.
     *
     * @return un état de test contenant une configuration prédéfinie
     */
    IState testState();

    /**
     * Retourne un état avec une ligne de pions blancs prête à être retirée.
     *
     * @return état contenant une ligne blanche complète
     */
    IState stateForWhiteLineTest();

    /**
     * Retourne un état avec une ligne de pions noirs prête à être retirée.
     *
     * @return état contenant une ligne noire complète
     */
    IState stateForBlackLineTest();

    /**
     * Retourne un terrain vierge, sans pion ni anneau.
     *
     * @return état vide du jeu
     */
    IState emptyState();

    /**
     * Retourne un état avec deux lignes simultanées.
     *
     * @return état contenant plusieurs lignes actives
     */
    IState doubleLineStateTest();
}