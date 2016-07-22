package com.tmshv.agents.core;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Path {
    public ArrayList<PVector> coords;

    public Path() {
        this(new ArrayList<>());
    }

    public Path(ArrayList<PVector> cs) {
        this.coords = cs;
    }

    public void add(PVector p) {
        coords.add(p);
    }

    public ArrayList<PVector> getInnerCoords() {
        ArrayList<PVector> c = new ArrayList<>();
        for (int i = 1; i < coords.size() - 1; i++) {
            c.add(coords.get(i));
        }
        return c;
    }

    public Path clone() {
        Path p = new Path();
        for (PVector v : coords) {
            p.add(v.copy());
        }
        return p;
    }

    public Path reverse() {
        Path c = clone();
        Collections.reverse(c.coords);
        return c;
    }
}
