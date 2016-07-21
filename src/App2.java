import geojson.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.Table;

import java.util.ArrayList;
import java.util.Objects;

public class App2 extends PApplet {
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"App2"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }

    private Crossroad crossroadStart = null;
    private Crossroad crossroadFinish = null;

    private Navigator navigator;

    private UI ui;

    private boolean drawRoads = true;
    private boolean drawAgents = true;
    private boolean drawTracks = true;
    private boolean drawHistoryTracks = true;
    private boolean drawPointCloud = true;
    private float cameraStep = .000125f;
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
    private PImage img;

    public void settings() {
        fullScreen(P3D);
//        smooth();
    }

    public void setup() {
        ui = new UI(this, 10, 10);

        projector = new SphericalMercator(0.80d);
        camera = new Camera(projector);
        simulation = new Simulation();
        simulation.setAgentsLimit(300);
        navigator = new Navigator(simulation, 2, projector);

        EmitterFactory.init(projector, simulation);
        AgentFactory.init(this, projector, simulation, navigator);

        centerCoord = new LatLon(55.74141f, 37.614784f);
        cursorCoord = new LatLon(55.74317f, 37.61516f);
        cursor = new PVector();

        pointCloud = new ArrayList<>();
        loadPointCloud(loadTable("data/udarnik-20p.csv", "header"));

        loadRoadLayer("data/geo/road-transport.geojson", "transport", 0xff999999, 2);
        loadRoadLayer("data/geo/road-pedestrian.geojson", "people", 0xffdddddd, 1);

        camera.lookAt(new LatLon(55.74166f, 37.614407f));

//        setStartPoint(new LatLon(55.73898f, 37.605858f));
//        setEndPoint(new LatLon(55.739960443216781f, 37.617145380088019f));

//        Route r = navigator.navigate(crossroadStart, crossroadFinish);
//        if (r != null) currentRoute = r.bake();

//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));

//        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));

//        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));

//        AgentFactory.createBoids(new LatLon(55.746178f, 37.615578f), "bird", 30);

        EmitterFactory.createBoids(new LatLon(55.741917f, 37.614784f), "bird", 20, 30 * 1000, 3 * 1000);
        EmitterFactory.createBoids(new LatLon(55.7415f, 37.614048f), "bird2", 20, 30 * 1000, 0);

        EmitterFactory.createBoids(new LatLon(55.741856f, 37.61407f), "bird", 20, 30 * 1000, 10 * 1000);
        EmitterFactory.createBoids(new LatLon(55.741528f, 37.61468f), "bird2", 20, 30 * 1000, 12 * 1000);

//        EmitterFactory.createBike(new LatLon(55.746178f, 37.615578f), 10 * 1000);

//        EmitterFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), 10 * 1000);

//        simulation.addAttractor(new Attractor("a", 100, projector.project(new LatLon(55.73998f, 37.616058f))));

//        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrian(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createTransport(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
    }

    public void draw() {
        background(255);

        ui.update();

        cursorCoord.lat = centerCoord.lat + ui.cursorLat;
        cursorCoord.lon = centerCoord.lon + ui.cursorLon;
        cursor = projector.project(cursorCoord);
        cursor.z = ui.cursorHeight;

        pushMatrix();
        rotateX(ui.rotation);
        rotateZ(ui.rotationZ);
        camera.update(this);
        translate(0, 0, ui.positionZ);
        translate(width / 2, height / 2);

        simulation.update();

        PVector pcv = projector.project(new LatLon(55.741604f, 37.61447f));
//        PVector pcv = cursor;
        if (drawPointCloud) drawPointCloud(ui.pointCloudScale, pcv, ui.pointCloudRotation);
        if (drawRoads) simulation.graphs.forEach(this::drawGraph);
        if (drawHistoryTracks) drawHistoryTracks();
        drawCityAgents(drawAgents, drawTracks);
//        if (drawAttractors) simulation.attractors.forEach(a -> {
//            if (a instanceof Tweet) drawTweet((Tweet) a);
//            else drawAttractor(a);
//        });

        //Draw cursor
        pushStyle();
        strokeWeight(1);
        stroke(0xffff0000);
        cross(10, cursor.x, cursor.y, cursor.z);
        popStyle();

        popMatrix();
        drawUI();
    }

    private void cross(int i, float x, float y, float z) {
        line(x - i, y, z, x + i, y, z);
        line(x, y - i, z, x, y + i, z);
    }

    void drawUI() {
        hint(DISABLE_DEPTH_TEST);
        ui.draw();
        hint(ENABLE_DEPTH_TEST);
    }

    private void loadRoadLayer(String filename, String name, int color, int thickness) {
        IFeatureCollection geo;
        geo = new GeoJSON(loadJSONObject(filename));
        geo = new FeatureExploder(geo);
        geo = new FeatureOptimizer(geo);
        CityGraph graph = new CityGraph();
        graph.loadFeatures(geo, projector);
        graph.strokeColor = color;
        graph.strokeThickness = thickness;
        simulation.addGraphLayer(graph, name);
    }

    private void loadPointCloud(Table table) {
        table.rows().forEach(row -> {
            float x = row.getFloat("x");
            float y = row.getFloat("y");
            float z = row.getFloat("z");
            pointCloud.add(new PVector(x, y, z));
        });
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

    private void drawPointCloud(float scale, PVector pos, float rotationZ) {
        pushStyle();
        pushMatrix();
        translate(pos.x, pos.y, pos.z);
        rotateZ(rotationZ);
        stroke(0);
        strokeWeight(1);
        beginShape(POINTS);
        pointCloud
                .stream()
                .forEach(v -> vertex(v.x * scale, v.y * scale, v.z * scale));
        endShape();
        popMatrix();
        popStyle();
    }

    private void drawCityAgents(boolean drawVehicle, boolean drawTrack) {
        simulation.agents
                .stream()
                .map(a -> (Agent) a)
                .filter(a -> a != null)
                .forEach(a -> {
                    if (drawVehicle) drawAgent(a);
                    if (drawTrack) drawTrack(a.getTrack());
                });
    }

    private void drawAgent(Agent agent) {
        stroke(agent.getColor());
        strokeWeight(2);
        point(agent.location.x, agent.location.y);
    }

    private void drawAttractor(Attractor attractor) {
        PVector loc = attractor.getLocation();

        if (Objects.equals(attractor.getType(), "tree")) {
            float h = 10;
            stroke(attractor.getColor());
            strokeWeight(1);
            line(loc.x, loc.y, loc.z, loc.x, loc.y, loc.z + h);
        } else {
            stroke(attractor.getColor());
            strokeWeight(2);
            point(loc.x, loc.y, loc.z);
        }
    }

    private void drawTweet(Tweet tweet) {
        PVector loc = tweet.getLocation();
        stroke(tweet.getColor());

        float r = tweet.getMass() * 2;
        if (r > 50) {
            strokeWeight(1);
            noFill();
            ellipse(loc.x, loc.y, r, r);
        }

        String text = tweet.username;
    }

    private void drawHistoryTracks() {
        simulation.tracks.forEach(this::drawTrack);
    }

    private void drawTrack(Track track) {
        pushStyle();
        noFill();
//        stroke(red(track.color), green(track.color), blue(track.color), 50);
        stroke(track.color);
        strokeWeight(1);
        beginShape();
        track.history.forEach(v -> vertex(v.x, v.y, v.z));
        endShape();
        popStyle();
    }

    private void drawGraph(CityGraph graph) {
        pushStyle();
        stroke(graph.strokeColor);
        strokeWeight(graph.strokeThickness);
        graph.roads.forEach(this::renderRoad);
        popStyle();
    }

    private void renderRoad(Path road) {
        beginShape();
        road.coords.forEach(v -> vertex(v.x, v.y, v.z));
        endShape();
    }

    public void keyPressed() {
        if (keyCode == UP) camera.moveTarget(cameraStep, 0);
        if (keyCode == DOWN) camera.moveTarget(-cameraStep, 0);
        if (keyCode == LEFT) camera.moveTarget(0, -cameraStep);
        if (keyCode == RIGHT) camera.moveTarget(0, cameraStep);

        if (key == 'm') println(getLatLonCursor());
        if (key == 'c') camera.print();

        if (key == ']') selectNextGraph();
        if (key == '[') selectPrevGraph();

        if (key == '1') drawRoads = !drawRoads;
        if (key == '2') drawAgents = !drawAgents;
        if (key == '3') drawTracks = !drawTracks;
        if (key == '4') drawHistoryTracks = !drawHistoryTracks;
        if (key == '7') drawPointCloud = !drawPointCloud;
    }
}
