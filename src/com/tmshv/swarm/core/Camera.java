package com.tmshv.swarm.core;

import geojson.IProjector;
import geojson.LatLon;
import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.max;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Camera {
    PVector target;
    IProjector projector;
    PVector offset;

    private int zoom = 1;

    public Camera(IProjector p) {
        target = new PVector();
        projector = p;
        offset = new PVector();
    }

    void setOffset(PVector v) {
        offset = v;
    }

    public LatLon getCoordAtScreen(float x, float y) {
        PVector coord = target.copy();
        coord.sub(offset);
        coord.x += x;
        coord.y += y;
        return projector.unproject(coord);
    }

    public void moveTarget(float x, float y) {
        this.target.x += x;
        this.target.y += y;
    }

    public void lookAt(LatLon target) {
        this.target.set(projector.project(target));
    }

    public void lookAt(PVector coord) {
        target.set(coord);
    }

    public void update(PApplet app) {
        app.translate(offset.x, offset.y);
        app.translate(-target.x, -target.y);
    }

    public void print() {
        System.out.println("camera target: " + target);
    }

    public int zoomIn() {
        zoom++;
        return zoom;
    }

    public int zoomOut() {
        zoom = max(1, --zoom);
        return zoom;
    }

    public int getZoom() {
        return zoom;
    }
}
