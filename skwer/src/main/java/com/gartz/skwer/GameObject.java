package com.gartz.skwer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gartz on 2/1/16.
 *
 * The base game object.
 *
 */
public abstract class GameObject {
    List<GameObject> children;

    public void draw(float[] mvpMatrix) {
        onDraw(mvpMatrix);
        if (children != null)
            for (GameObject child : children)
                child.draw(mvpMatrix);
    }

    public abstract void onDraw(float[] mvpMatrix);

    public void addChild(GameObject child) {
        if (children == null)
            children = new ArrayList<>();
        children.add(child);
    }

}
