import geojson.*;
import pathfinder.GraphNode;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class App extends PApplet {
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"App"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }

    private Crossroad crossroadStart = null;
    private Crossroad crossroadFinish = null;

    private Driver defaultDriver;

    private boolean drawRoads = true;
    private boolean drawAgents = true;
    private boolean drawTracks = true;
    private boolean drawGraphMode = true;

    private int currentGraphIndex = 0;

    private ArrayList<PVector> currentRoute;

    private City city;
    private Camera camera;
    private SphericalMercator projector;

    public void settings() {
        fullScreen();
        smooth();
    }

    public void setup() {
        background(0);

        projector = new SphericalMercator(0.5d);

        camera = new Camera(projector);

        IFeatureCollection geo;

        // geo = new GeoJSON("../sample.geojson");
        geo = new GeoJSON(loadJSONObject("sample_roads-EPSG4326.geojson"));
        FeatureExploder fx = new FeatureExploder(geo);
        CityGraph streets = new CityGraph(fx, projector);
        streets.strokeColor = 0xffdddddd;

        // geo = new GeoJSON("../osm_sample.geojson");
        // FeatureExploder fx = new FeatureExploder(geo);
        // FeatureOptimizer fo = new FeatureOptimizer(fx);
        // CityGraph streets = new CityGraph(fo);

        city = new City();
        city.addCityGraph(streets);

        camera.setOffset(new PVector(width / 2, height / 2));
        camera.lookAt(new LatLon(55.73898f, 37.605858f));

        setStartPoint(new LatLon(55.73898f, 37.605858f));
        setEndPoint(new LatLon(55.743206f, 37.607254f));

        defaultDriver = new Driver(city, 2, projector);
        Route r = defaultDriver.navigate(crossroadStart, crossroadFinish);
        if (r != null) currentRoute = r.bake();

        emitAgent();
    }

    private Driver createAgent() {
        float maxSpeed = random(1, 10);
        float maxForce = random(1, 4);

        int[] colors = new int[]{0xffff0000, 0xffffff00, 0xff00ff00, 0xff00ffff, 0xffff00ff, 0xffffffff, 0xffcccccc};
        int c = colors[(int) (random(colors.length - 1))];
        int size = (int) random(2, 5);

        float predictMult = random(10, 50);
        float dirMult = random(2, 10);

        Vehicle v = new Vehicle(maxSpeed, maxForce, c);
        v.size = size;
        v.predictMult = predictMult;
        v.dirMult = dirMult;
        city.addVehicle(v);

        Driver driver = new Driver(city, 2, projector);
        driver.drive(v);
        return driver;
    }

    public void draw() {
        background(0);
        LatLon mouse = camera.getCoordAtScreen(mouseX, mouseY);

        if (mousePressed) {
            if (mouseButton == LEFT) setStartPoint(mouse);
            if (mouseButton == RIGHT) setEndPoint(mouse);

            if (crossroadStart != null && crossroadFinish != null) {
                Route r = defaultDriver.navigate(crossroadStart, crossroadFinish);
                if (r != null) currentRoute = r.bake();
            }
        }

        pushMatrix();
        camera.update(this);
        city.update();

        if (currentRoute != null) {
            drawCurrentRoute();
        }

        if (drawRoads) drawCityRoads(city, null);
        drawCityAgents(city, drawAgents, drawTracks);

        // if(crossroadStart != null && crossroadFinish != null){
        //   noFill();
        //   stroke(0, 0, 255);
        //   strokeWeight(1);
        //   ellipseMode(CENTER);
        //   PVector c;
        //   c = projector.project(crossroadStart.coord);
        //   ellipse(c.x, c.y, 5, 5);

        //   c = projector.project(crossroadFinish.coord);
        //   ellipse(c.x, c.y, 10, 10);
        // }

        popMatrix();
    }

    private void setStartPoint(LatLon ll) {
        PVector v = projector.project(ll);
        Crossroad n = city.graph(0).findNearestCrossroadTo(v);
        crossroadStart = n;
    }

    private void setEndPoint(LatLon ll) {
        PVector v = projector.project(ll);
        Crossroad n = city.graph(0).findNearestCrossroadTo(v);
        crossroadFinish = n;
    }

    private void emitAgent() {
        Driver d = createAgent();
        Route r = d.navigate(crossroadStart, crossroadFinish);
        d.driveWith(r);
    }

    public void drawGraphRoute(GraphNode[] route) {
        pushStyle();

        for (int i = 0; i < route.length - 1; i++) {
            GraphNode n1 = route[i];
            GraphNode n2 = route[i + 1];

            PVector p1 = city.graphs.get(currentGraphIndex).getGraphNodeCoord(n1);
            PVector p2 = city.graphs.get(currentGraphIndex).getGraphNodeCoord(n2);

            noFill();
            stroke(0xffaa0000);
            strokeWeight(2);
            line(p1.x, p1.y, p2.x, p2.y);

            pushMatrix();
            PVector diff = PVector.sub(p2, p1);
            float a = atan2(diff.y, diff.x);
            translate(p2.x, p2.y);
            rotate(a - HALF_PI);

            float s = 9;
            float sr = 3;
            strokeWeight(1);
            fill(0xffaa0000);
            triangle(-s / sr, -s, s / sr, -s, 0, 0);

            popMatrix();
        }
        popStyle();
    }

    public void drawRoute(Route route) {
        pushStyle();
        ArrayList<PVector> coords = route.bake();

        for (int i = 0; i < coords.size() - 1; i++) {
            PVector p1 = coords.get(i);
            PVector p2 = coords.get(i + 1);

            noFill();
            stroke(0xff00ff00);
            strokeWeight(1);
            line(p1.x, p1.y, p2.x, p2.y);

            pushMatrix();
            PVector diff = PVector.sub(p2, p1);
            float a = atan2(diff.y, diff.x);
            translate(p2.x, p2.y);
            rotate(a - HALF_PI);

            float s = 9;
            float sr = 3;
            strokeWeight(1);
            fill(0xff00ff00);
            triangle(-s / sr, -s, s / sr, -s, 0, 0);

            popMatrix();
        }
        popStyle();
    }

    public void drawCurrentRoute() {
        strokeWeight(1);
        stroke(235);
        noFill();

        beginShape();
        for (PVector p : currentRoute) vertex(p.x, p.y);
        endShape();
    }

    public void selectNextGraph() {
        currentGraphIndex++;
        currentGraphIndex %= city.graphs.size();
    }

    public void selectPrevGraph() {
        if (currentGraphIndex > 0) currentGraphIndex--;
    }

    public void drawTrack(Track track) {
        noFill();
        // stroke(255, 255, 0, 50);
        stroke(red(track.paint), green(track.paint), blue(track.paint), 50);
        strokeWeight(1);
        beginShape();
        for (PVector v : track.history) vertex(v.x, v.y);
        endShape();
    }

    public void drawCityAgents(City city, boolean drawVehicle, boolean drawTrack) {
        for (Vehicle a : city.vehicles) {
            if (drawVehicle) drawVehicle(a);
            if (drawTrack) drawTrack(a.track);
        }
    }

    public void drawVehicle(Vehicle v) {
        stroke(v.paint);
        strokeWeight(v.size);
        point(v.location.x, v.location.y);
    }

    public void drawRoads(CityGraph cg, Road selected) {
        pushStyle();
        noFill();
        for (Road road : cg.roads) {
            stroke(cg.strokeColor, 50);
            strokeWeight(cg.strokeThickness);

            if (road == selected) {
                stroke(0xff009966);
                strokeWeight(2);
            }

            beginShape();
            for (PVector xy : road.coords) vertex(xy.x, xy.y);
            endShape();
        }
        popStyle();
    }

    public void keyPressed() {
        // float step = map(camera.zoom, 0, 1, 100, 10);
        // float step = .01 / camera.getZoom();
        float step = .0025f;
        if (keyCode == UP) camera.moveTarget(step, 0);
        if (keyCode == DOWN) camera.moveTarget(-step, 0);
        if (keyCode == LEFT) camera.moveTarget(0, -step);
        if (keyCode == RIGHT) camera.moveTarget(0, step);

        // if (key == '-') camera.zoomOut();
        // if (key == '=') camera.zoomIn();
        // if (key == '0') camera.lookAt(center);
        if (key == ' ') saveFrame("../frame-###.jpg");

        if (key == 'g') bakeGraph();
        if (key == 'r') drawGraphMode = !drawGraphMode;

        if (key == 'e') emitAgent();

        if (key == ']') selectNextGraph();
        if (key == '[') selectPrevGraph();

        if (key == '1') drawRoads = !drawRoads;
        if (key == '2') drawAgents = !drawAgents;
        if (key == '3') drawTracks = !drawTracks;
//        if (key == '4') drawNodes = !drawNodes;
//        if (key == '5') drawEdges = !drawEdges;
//        if (key == '6') drawGraphRoute = !drawGraphRoute;
//        if (key == '7') drawRoute = !drawRoute;
//        if (key == '8') drawNearest = !drawNearest;
    }

    public void drawCityRoads(City city, Road selected) {
        for (CityGraph cg : city.graphs) drawRoads(cg, selected);
    }

    public void bakeGraph() {
        String dump = GraphUtils.bake(city.graphs.get(currentGraphIndex).graph);
        println(dump);
    }
}
