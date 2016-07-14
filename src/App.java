import geojson.*;
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
        city.addAgent(v);

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
        rotateX(HALF_PI/2);

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
        crossroadStart = city.graph(currentGraphIndex).findNearestCrossroadTo(v);
    }

    private void setEndPoint(LatLon ll) {
        PVector v = projector.project(ll);
        crossroadFinish = city.graph(currentGraphIndex).findNearestCrossroadTo(v);
    }

    private void emitAgent() {
        Driver d = createAgent();
        Route r = d.navigate(crossroadStart, crossroadFinish);
        d.driveWith(r);
    }

    private void drawCurrentRoute() {
        strokeWeight(1);
        stroke(235);
        noFill();

        beginShape();
        for (PVector p : currentRoute) vertex(p.x, p.y);
        endShape();
    }

    private void selectNextGraph() {
        currentGraphIndex++;
        currentGraphIndex %= city.graphs.size();
    }

    private void selectPrevGraph() {
        if (currentGraphIndex > 0) currentGraphIndex--;
    }

    private void drawTrack(Track track) {
        noFill();
        // stroke(255, 255, 0, 50);
        stroke(red(track.paint), green(track.paint), blue(track.paint), 50);
        strokeWeight(1);
        beginShape();
        for (PVector v : track.history) vertex(v.x, v.y);
        endShape();
    }

    private void drawCityAgents(City city, boolean drawVehicle, boolean drawTrack) {
        for (Agent a : city.agents) {
            if (drawVehicle) drawAgent(a);
            if (drawTrack) drawTrack(a.track);
        }
    }

    private void drawAgent(Agent v) {
        stroke(v.paint);
        strokeWeight(v.size);
        point(v.location.x, v.location.y);
    }

    private void drawRoads(CityGraph cg, Road selected) {
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

    private void drawCityRoads(City city, Road selected) {
        for (CityGraph cg : city.graphs) drawRoads(cg, selected);
    }

    private void bakeGraph() {
        String dump = GraphUtils.bake(city.graphs.get(currentGraphIndex).graph);
        println(dump);
    }
}
