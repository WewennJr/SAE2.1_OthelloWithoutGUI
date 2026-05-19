package iut.gon.model.factory;

import iut.gon.model.state.IState;

public interface IFactory {

    public IState testState();
    public IState stateForBlackLineTest();
    public IState stateForWhiteLinteTest();
    public IState emptyState();
    public IState doubleLineStateTest();
}
