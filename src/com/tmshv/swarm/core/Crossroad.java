package com.tmshv.swarm.core;

import pathfinder.GraphNode;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Crossroad {
    ArrayList<Road> roads;
    public PVector coord;
    GraphNode node;

    public Crossroad(PVector coord, GraphNode node) {
        this.roads = new ArrayList<>();
        this.coord = coord;
        this.node = node;
    }

    public Crossroad addRoad(Road road) {
        this.roads.add(road);
        return this;
    }

    public boolean has(Road road) {
        for (Road f : this.roads) {
            if (f == road) return true;
        }
        return false;
    }

    public boolean coordIsEqual(PVector v) {
        return v.x == coord.x && v.y == coord.y;
    }

    /**
     * Find a road for moving from cr to this
     **/
    // Feature arrive(com.tmshv.swarm.core.Crossroad cr){
    //   for(Feature f : cr.roads){

    //     if (f.geometry.last().equals(cr.coord)) return f;

    //     // if(this.has(f)) return f;
    //   }
    //   return null;
    // }
}
