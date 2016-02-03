package com.gartz.skwer.game;

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

    protected float left, right, top, bottom;

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
        if (children != null)
            for (GameObject child : children) {
                GameObject touchedChild = child.getTouchedObject(x, y);
                if (touchedChild != null)
                    return touchedChild;
            }
        if (left < x && right > x && top < y && bottom > y)
            return this;
        return null;
    }
    public void touch() { }
    public void unTouch() { }
    public void click() { }
    public void longClick() { }

    protected void setBounds(float left, float right, float top, float bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

}
