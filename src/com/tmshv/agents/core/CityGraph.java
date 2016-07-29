package com.tmshv.agents.core;

import geojson.Feature;
import geojson.IFeatureCollection;
import geojson.IProjector;
import geojson.LatLon;
import pathfinder.Graph;
import pathfinder.GraphEdge;
import pathfinder.GraphNode;
import processing.core.PVector;
import com.tmshv.agents.utils.GeometryUtils;

import java.util.ArrayList;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class CityGraph {
    ArrayList<Crossroad> crossroads;
    public ArrayList<Road> roads;
    public Graph graph;

    public int strokeColor = 0xffffffff;
    public int strokeThickness = 1;

//    private LatLon[] bound;

    PVector topLeft;
    PVector rightBottom;
    private String name;

    public CityGraph() {
        rightBottom = new PVector();
        topLeft = new PVector();
        roads = new ArrayList<>();
        crossroads = new ArrayList<>();
        graph = new Graph();
    }

    public void createRoad(Path path) {
        GraphEdge edge;

        Crossroad cr1 = getCrossroadAt(path.first());
        Crossroad cr2 = getCrossroadAt(path.last());
        float weight = cr1.coord.dist(cr2.coord);

        edge = addGraphEdge(cr1.node.id(), cr2.node.id(), weight);
        addRoad(new Road(path, cr1, cr2, edge));

        edge = addGraphEdge(cr2.node.id(), cr1.node.id(), weight);
        addRoad(new Road(path.reverse(), cr2, cr1, edge));
    }

    public Road getRoad(GraphEdge edge) {
        for (Road r : roads) {
            if (r.cr1.node.id() == edge.from().id() && r.cr2.node.id() == edge.to().id()) return r;
        }
        return null;
    }

    public PVector getGraphNodeCoord(GraphNode node) {
        Crossroad cr = crossroads.get(node.id());
        return cr.coord;
    }

    private void addRoad(Road road) {
        roads.add(road);
    }

    private GraphNode addGraphNode(int id, float x, float y) {
        GraphNode node = new GraphNode(id, x, y);
        graph.addNode(node);
        return node;
    }

    private GraphEdge addGraphEdge(int from, int to, float weight) {
        graph.addEdge(from, to, weight);
        return graph.getEdge(from, to);
    }

    public Crossroad getCrossroadAt(PVector ll) {
        for (Crossroad cr : crossroads) {
            if (cr.coordIsEqual(ll)) {
                return cr;
            }
        }

        int id = crossroads.size();
        GraphNode n = addGraphNode(id, ll.x, ll.y);
        Crossroad cr = new Crossroad(ll, n);
        crossroads.add(cr);
        return cr;
    }

    public int getCrossroadIndex(PVector ll) {
        int length = this.crossroads.size();
        for (int i = 0; i < length; i++) {
            if (this.crossroads.get(i).coordIsEqual(ll)) return i;
        }
        return -1;
    }

    public int getCrossroadIndex(Crossroad cr) {
        return getCrossroadIndex(cr.coord);
    }

    public LatLon getCenter() {
        return new LatLon(
//                (bound[0].lat + bound[1].lat) / 2,
//                (bound[0].lon + bound[1].lon) / 2
        );
    }

    public Crossroad findNearestCrossroadTo(PVector v) {
        float max_dist = 6378137; //<>//

        Crossroad choise = null;
        for (Crossroad cr : this.crossroads) {
            float dist = cr.coord.dist(v);
            if (dist < max_dist) {
                max_dist = dist;
                choise = cr;
            }
        }
        return choise;
    }

    public GraphEdge findNearestGraphEdgeTo(PVector v) {
        GraphEdge nearest = null;
        float minDist = Float.MAX_VALUE;

        GraphEdge[] edges = graph.getAllEdgeArray();
        for (GraphEdge ge : edges) {
            PVector p1 = getGraphNodeCoord(ge.from());
            PVector p2 = getGraphNodeCoord(ge.to());

            PVector projectionVertex = GeometryUtils.projectVertexOnLine(v, p1, p2);
            String pointClass = GeometryUtils.classify(projectionVertex, p1, p2);

            if (pointClass != "between") continue;

            float d = PVector.dist(v, projectionVertex);
            if (d < minDist) {
                minDist = d;
                nearest = ge;
            }
        }

        return nearest;
    }

    public Road findNearestRoadTo(PVector v) {
        GraphEdge nearest = findNearestGraphEdgeTo(v);
        if (nearest != null) {
            for (Road r : roads) {
                if (r.getEdge() == nearest) return r;
            }
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
