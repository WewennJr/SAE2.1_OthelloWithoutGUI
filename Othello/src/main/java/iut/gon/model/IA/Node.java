package iut.gon.model.IA;

import iut.gon.model.actions.Action;
import iut.gon.model.state.IState;

public class Node {

    public IState state;

    public Node parent;

    public Action action;

    public Node(IState state, Node parent, Action action){
        this.state = state;
        this.parent = parent;
        this.action = action;
    }

    public IState getState() {
        return state;
    }

    public void setState(IState state) {
        this.state = state;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
