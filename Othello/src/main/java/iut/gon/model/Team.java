package iut.gon.model;

import java.awt.*;

public enum Team {

    BLACK,
    WHITE;

    public Color color;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
