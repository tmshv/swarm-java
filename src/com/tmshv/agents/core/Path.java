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

    private double radius;

    public Path() {
        this(new ArrayList<>(), 0);
    }

    public Path(double radius) {
        this(new ArrayList<>(), radius);
    }

    public Path(ArrayList<PVector> coords, double radius) {
        this.coords = coords;
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
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
        Path p = new Path(getRadius());
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

    public PVector first() {
        return this.coords.get(0);
    }

    public PVector last() {
        return this.coords.get(this.coords.size() - 1);
    }
}
