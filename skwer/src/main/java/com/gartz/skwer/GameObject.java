package com.gartz.skwer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gartz on 2/1/16.
 *
 * The base game object.
 *
 */
public class GameObject {
    protected Game game;
    protected List<GameObject> children;

    public GameObject(Game game) {
        this.game = game;
    }

    // Children tree
    public void addChild(GameObject child) {
        if (children == null)
            children = new ArrayList<>();
        children.add(child);
    }

    // Draw
    public void draw(float[] mvpMatrix) {
        onDraw(mvpMatrix);
        if (children != null)
            for (GameObject child : children)
                child.draw(mvpMatrix);
    }
    public void onDraw(float[] mvpMatrix) { }

    // Touch
    public GameObject getTouchedObject(float x, float y) {
        // TODO add bounds
        // TODO keep bounds accurate
        // TODO Determine based on bounds
        // TODO project bounds??
        if (children != null)
            for (GameObject child : children) {
                GameObject touchedChild = child.getTouchedObject(x, y);
                if (touchedChild != null)
                    return touchedChild;
            }
        return this;
    }
    public void touch() { }
    public void click() { }
    public void longClick() { }

}
