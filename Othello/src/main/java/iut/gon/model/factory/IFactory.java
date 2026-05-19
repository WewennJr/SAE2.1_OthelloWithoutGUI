package iut.gon.model.factory;

import iut.gon.model.state.IState;

public interface IFactory {

    /** Retourne un état de test général (testState). */
    IState testState();

    /** Retourne un état avec une ligne de pions blancs prête à être retirée. */
    IState stateForWhiteLineTest();

    /** Retourne un état avec une ligne de pions noirs prête à être retirée. */
    IState stateForBlackLineTest();

    /** Retourne un terrain vierge, sans pion ni anneau. */
    IState emptyState();

    /** Retourne un état avec deux lignes simultanées. */
    IState doubleLineStateTest();
}