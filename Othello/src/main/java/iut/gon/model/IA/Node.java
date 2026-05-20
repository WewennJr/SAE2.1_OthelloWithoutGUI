package iut.gon.model.IA;

import iut.gon.model.actions.Action;
import iut.gon.model.state.IState;

/**
 * Représente un nœud dans un arbre de recherche utilisé par une IA.
 * <p>
 * Chaque nœud contient un état de jeu, une action ayant mené à cet état,
 * ainsi qu’un lien vers son nœud parent.
 * </p>
 */
public class Node {

    /**
     * État du jeu associé à ce nœud.
     */
    public IState state;

    /**
     * Nœud parent dans l'arbre de recherche.
     */
    public Node parent;

    /**
     * Action ayant conduit à cet état.
     */
    public Action action;

    /**
     * Crée un nouveau nœud.
     *
     * @param state état du jeu
     * @param parent nœud parent
     * @param action action ayant mené à cet état
     */
    public Node(IState state, Node parent, Action action){
        this.state = state;
        this.parent = parent;
        this.action = action;
    }

    /**
     * Retourne l'état du jeu.
     *
     * @return état du nœud
     */
    public IState getState() {
        return state;
    }

    /**
     * Modifie l'état du jeu.
     *
     * @param state nouvel état
     */
    public void setState(IState state) {
        this.state = state;
    }

    /**
     * Retourne le nœud parent.
     *
     * @return parent du nœud
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Modifie le nœud parent.
     *
     * @param parent nouveau parent
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Retourne l'action associée au nœud.
     *
     * @return action ayant mené à cet état
     */
    public Action getAction() {
        return action;
    }

    /**
     * Modifie l'action associée au nœud.
     *
     * @param action nouvelle action
     */
    public void setAction(Action action) {
        this.action = action;
    }
}