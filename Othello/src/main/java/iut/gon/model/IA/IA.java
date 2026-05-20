package iut.gon.model.IA;

import iut.gon.model.actions.Action;
import iut.gon.model.state.IState;

public interface IA {
    public Action chooseMove(IState state);
}
