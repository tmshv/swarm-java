package com.tmshv.swarm.core;

import pathfinder.GraphEdge;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Road{
    Crossroad cr1;

    Crossroad cr2;

    private GraphEdge edge;

    private Path path;
    public Road(Path path, Crossroad cr1, Crossroad cr2, GraphEdge edge) {
        this.path = path;
        this.cr1 = cr1;
        this.cr2 = cr2;
        this.edge = edge;

        if (cr1 != null) cr1.addRoad(this);
        if (cr2 != null) cr2.addRoad(this);
    }

    public Path getPath() {
        return path;
    }

    public GraphEdge getEdge() {
        return edge;
    }
}
