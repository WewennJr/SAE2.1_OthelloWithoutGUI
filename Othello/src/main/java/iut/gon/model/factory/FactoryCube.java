package iut.gon.model.factory;

import iut.gon.model.state.IState;

public class FactoryCube implements IFactory {
    @Override
    public IState testState() {
        return null;
    }

    @Override
    public IState stateForBlackLineTest() {
        return null;
    }

    @Override
    public IState stateForWhiteLinteTest() {
        return null;
    }

    @Override
    public IState emptyState() {
        return null;
    }

    @Override
    public IState doubleLineStateTest() {
        return null;
    }
}
