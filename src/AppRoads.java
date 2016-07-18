import geojson.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import utils.GraphUtils;

import java.util.ArrayList;

public class AppRoads extends PApplet {
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"AppRoads"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }

    private Crossroad crossroadStart = null;
    private Crossroad crossroadFinish = null;

    private Navigator navigator;

    private boolean drawRoads = true;
    private boolean drawAgents = true;
    private boolean drawTracks = true;
    private boolean drawHistoryTracks = true;
    private boolean drawAttractors = true;
    private boolean drawTweets = false;
    private boolean drawPointCloud = false;
    private float cameraStep = .00125f;
    private int currentGraphIndex = 0;

    private ArrayList<PVector> currentRoute;

    private Simulation simulation;
    private Camera camera;
    private SphericalMercator projector;

    private LatLon cursorCoord;
    private PVector cursor;
    private LatLon centerCoord;

    private ArrayList<PVector> pointCloud;

    private PGraphics bakedRoads;

    private Path randomRoad;
    private CityGraph randomGraph;

    public void settings() {
        fullScreen(P3D);
//        fullScreen();
//        smooth();
    }

    public void setup() {
        projector = new SphericalMercator(0.80d);
        camera = new Camera(projector);
        simulation = new Simulation();
        navigator = new Navigator(simulation, 2, projector);

        EmitterFactory.init(projector, simulation);
        AgentFactory.init(this, projector, simulation, navigator);

        centerCoord = new LatLon(55.74317f, 37.61516f);
        cursorCoord = new LatLon(55.74317f, 37.61516f);
        cursor = new PVector();

        pointCloud = new ArrayList<>();

//        loadRoadLayer("data/geo/road-transport.geojson", 0xff000000);
        loadRoadLayer("data/geo/road-pedestrian.geojson", 0xff444444);

        camera.lookAt(centerCoord);

        randomGraph = new CityGraph();
        randomRoad = new Path();
        for (int i=0; i<1000; i++) {
            LatLon c = cursorCoord.clone();
            c.lat += random(-0.001f, 0.001f);
            c.lon += random(-0.001f, 0.001f);

            randomRoad.add(projector.project(c));
        }
        randomGraph.addRoad(new Road(randomRoad.coords, null, null, null));

//        bakedRoads = renderRoads(simulation.graph(0));
    }

    public void draw() {
        background(255);
        LatLon mouse = getLatLonCursor();

        cursor = projector.project(cursorCoord);
//        cursor = projector.project(mouse);

        float rotation = 0;
        float rotationZ = 0;

        pushMatrix();
//        rotateX(rotation);
//        rotateZ(rotationZ);
        camera.update(this);
        translate(width / 2, height / 2);

        simulation.update();

        if (drawRoads) {
            drawGraph(simulation.graph(0));
//            image(bakedRoads, cursor.x, cursor.y);
        }

        if (currentRoute != null) drawCurrentRoute();

//        pushStyle();
//        stroke(0xff00ff00);
//        strokeWeight(1);
//        noFill();
//        beginShape();
//        randomRoad.coords.forEach(v -> vertex(v.x, v.y, v.z));
////        randomRoad.forEach(v -> vertex(v.x, v.y));
//        endShape();
//        popStyle();

//        drawRoad(randomRoad);
        drawGraph(randomGraph);

        //Draw cursor
        pushStyle();
        strokeWeight(1);
        stroke(0xffff0000);
        cross(10, cursor.x, cursor.y, cursor.z);
        popStyle();

        popMatrix();
//        image(bakedRoads, 0, 0, 500, 500);
    }

    private void cross(int i, float x, float y, float z) {
//        line(x - i, y, z, x + i, y, z);
//        line(x, y - i, z, x, y + i, z);
        line(x - i, y, x + i, y);
        line(x, y - i, x, y + i);
    }

    private void loadRoadLayer(String filename, int color) {
        IFeatureCollection geo;
        geo = new GeoJSON(loadJSONObject(filename));
        geo = new FeatureExploder(geo);
        geo = new FeatureOptimizer(geo);
        CityGraph graph = new CityGraph();
        graph.loadFeatures(geo, projector);
        graph.strokeColor = color;
        graph.strokeThickness = 1;
        simulation.addGraphLayer(graph, "");
    }

    private LatLon getLatLonCursor() {
        return cursorCoord;
    }


    private void setStartPoint(LatLon ll) {
        PVector v = projector.project(ll);
        crossroadStart = simulation.graph(currentGraphIndex).findNearestCrossroadTo(v);
    }

    private void setEndPoint(LatLon ll) {
        PVector v = projector.project(ll);
        crossroadFinish = simulation.graph(currentGraphIndex).findNearestCrossroadTo(v);
    }

    private void selectNextGraph() {
        currentGraphIndex++;
        currentGraphIndex %= simulation.graphs.size();
    }

    private void selectPrevGraph() {
        if (currentGraphIndex > 0) currentGraphIndex--;
    }

    private void drawCurrentRoute() {
        strokeWeight(1);
        stroke(235);
        noFill();
        beginShape();
        for (PVector p : currentRoute) vertex(p.x, p.y);
        endShape();
    }

    private void drawGraph(CityGraph graph) {
        graph.roads.forEach(this::drawRoad);
//        for (Path path : graph.roads) {
//            stroke(graph.strokeColor);
//            strokeWeight(graph.strokeThickness);
////
//            beginShape();
////            path.coords.forEach(v -> vertex(v.x, v.y, v.z));
//            for (PVector v : path.coords) {
//                vertex(v.x, v.y, v.z);
//            }
//            endShape();
//        }
    }

    private void drawRoad(Path road) {
        pushStyle();
        noFill();
        stroke(0);
        strokeWeight(1);
//        beginShape(POINTS);
        beginShape();
//        road.coords.forEach(v -> vertex(v.x, v.y));
        road.coords.forEach(v -> vertex(v.x, v.y, v.z));
        endShape();

//        road.coords.forEach(v -> point(v.x, v.y, v.z));

        popStyle();
    }

    private PGraphics renderRoads(CityGraph graph) {
        PVector graphSize = PVector.sub(graph.rightBottom, graph.topLeft);
        int width = (int) abs(graphSize.x);
        int height = (int) abs(graphSize.y);

        PGraphics g = createGraphics(width, height);
        g.beginDraw();
        g.background(0);
        g.strokeWeight(1);
        g.stroke(255);
        g.noFill();
        g.translate(-graph.topLeft.x, -graph.topLeft.y);
        graph.roads.forEach(road -> {
            g.beginShape();
            road.coords.forEach(v -> {
//                PVector c = PVector.sub(v, graph.topLeft);
                PVector c = v;
                g.vertex(c.x, c.y);
            });
            g.endShape();
        });
        g.endDraw();

        return g;
    }

    public void keyPressed() {
        if (keyCode == UP) camera.moveTarget(cameraStep, 0);
        if (keyCode == DOWN) camera.moveTarget(-cameraStep, 0);
        if (keyCode == LEFT) camera.moveTarget(0, -cameraStep);
        if (keyCode == RIGHT) camera.moveTarget(0, cameraStep);

        if (key == ' ') saveFrame("../frame-###.jpg");

        if (key == 'g') bakeGraph();
        if (key == 'm') println(getLatLonCursor());

        if (key == ']') selectNextGraph();
        if (key == '[') selectPrevGraph();

        if (key == '1') drawRoads = !drawRoads;
        if (key == '2') drawAgents = !drawAgents;
        if (key == '3') drawTracks = !drawTracks;
        if (key == '4') drawHistoryTracks = !drawHistoryTracks;
        if (key == '5') drawAttractors = !drawAttractors;
        if (key == '6') drawTweets = !drawTweets;
        if (key == '7') drawPointCloud = !drawPointCloud;
    }

    private void bakeGraph() {
        String dump = GraphUtils.bake(simulation.graphs.get(currentGraphIndex).graph);
        println(dump);
    }
}
