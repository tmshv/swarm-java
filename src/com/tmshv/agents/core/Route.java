package com.tmshv.agents.core;

import pathfinder.GraphEdge;
import pathfinder.GraphNode;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Route {
    ArrayList<Road> roads;

    public Route(CityGraph cg, GraphNode[] route) {
        this(new ArrayList<Road>());

        for (int i = 0; i < route.length - 1; i++) {
            GraphNode n1 = route[i];
            GraphNode n2 = route[i + 1];

            GraphEdge edge = cg.graph.getEdge(n1.id(), n2.id());
            Road road = cg.getRoad(edge);

            if (road != null) roads.add(road);
        }
    }

    public Route(ArrayList<Road> route) {
        this.roads = route;
    }

    public ArrayList<PVector> bake() {
        ArrayList<PVector> route = new ArrayList<PVector>();

        if (roads.size() > 0) {
            route.add(roads.get(0).cr1.coord);

            for (Road r : roads) {
                route.addAll(r.getInnerCoords());
                route.add(r.cr2.coord);
            }
        }

        return route;
    }
}