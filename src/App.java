import geojson.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.Table;

import java.util.ArrayList;
import java.util.Objects;

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

    private Navigator navigator;

    private UI ui;

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
        navigator = new Navigator(simulation, 2, projector);

        EmitterFactory.init(projector, simulation);
        AgentFactory.init(this, projector, simulation, navigator);

        centerCoord = new LatLon(55.74317f, 37.61516f);
        cursorCoord = new LatLon(55.74317f, 37.61516f);
        cursor = new PVector();

        pointCloud = new ArrayList<>();
        loadPointCloud(loadTable("data/udarnik-10p.csv", "header"));

        loadRoadLayer("data/geo/road-transport.geojson", 0xff000000);
        loadRoadLayer("data/geo/road-pedestrian.geojson", 0xff444444);

        loadAttractors(new GeoJSON(loadJSONObject("data/geo/trees.geojson")), "tree", 10, 0xff00ff00);
        loadTweets(0xFFCBD7FF, loadTable("data/tweets/pedestrian1.csv", "header"));
        loadTweets(0xFFCBD7FF, loadTable("data/tweets/pedestrian2.csv", "header"));
        loadTweets(0xFFC7FFBD, loadTable("data/tweets/runner.csv", "header"));
        loadTweets(0xFFC7FFBD, loadTable("data/tweets/runner2.csv", "header"));
        loadTweets(0xFFC7FFBD, loadTable("data/tweets/runner3.csv", "header"));
        loadTweets(0xFFCBD7FF, loadTable("data/tweets/tourist-pedestrian.csv", "header"));
        loadTweets(0xFFCBD7FF, loadTable("data/tweets/tourist1.csv", "header"));
        loadTweets(0xFFCBD7FF, loadTable("data/tweets/tourist2.csv", "header"));

        loadData(loadTable("data/ae-temp.csv", "header"));

//        int cameraZ = -500;
//        camera.setOffset(new PVector(width / 2, height / 2, cameraZ));
        camera.lookAt(centerCoord);

//        bakedRoads = renderRoads(simulation.graph(0));

//        println(bakedRoads.width);
//        println(bakedRoads.height);

//        setStartPoint(new LatLon(55.73898f, 37.605858f));
//        setEndPoint(new LatLon(55.739960443216781f, 37.617145380088019f));

//        Route r = navigator.navigate(crossroadStart, crossroadFinish);
//        if (r != null) currentRoute = r.bake();

        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));

        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));

        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));

//        AgentFactory.createBoids(new LatLon(55.746178f, 37.615578f), "bird", 30);
        EmitterFactory.createBoids(new LatLon(55.746178f, 37.615578f), "bird", 20, 30 * 1000);
        EmitterFactory.createBoids(new LatLon(55.742428f, 37.612133f), "bird", 10, 20 * 1000);
//        EmitterFactory.createBike(new LatLon(55.746178f, 37.615578f), 10 * 1000);

//        EmitterFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), 10 * 1000);

//        simulation.addAttractor(new Attractor("a", 100, projector.project(new LatLon(55.73998f, 37.616058f))));

//        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrian(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createTransport(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
    }

    public void draw() {
        background(255);
        LatLon mouse = getLatLonCursor();

        ui.update();

        cursorCoord.lat = centerCoord.lat + ui.cursorLat;
        cursorCoord.lon = centerCoord.lon + ui.cursorLon;
        cursor = projector.project(cursorCoord);
        cursor.z = ui.cursorHeight;

//        if (mousePressed) {
//            if (mouseButton == LEFT) setStartPoint(mouse);
//            if (mouseButton == RIGHT) setEndPoint(mouse);
//
//            if (crossroadStart != null && crossroadFinish != null) {
//                Route r = navigator.navigate(crossroadStart, crossroadFinish);
//                if (r != null) currentRoute = r.bake();
//            }
//        }

//        float rx = map(mouseY, 0, height, 0, HALF_PI);
        pushMatrix();
//        camera(mouseX, height/2, (height/2) / tan(PI/6), width/2, height/2, 0, 0, 1, 0);
//        scale(ui.scale);
        rotateX(ui.rotation);
        rotateZ(ui.rotationZ);
        camera.update(this);
        translate(0, 0, ui.positionZ);
        translate(width / 2, height / 2);

        simulation.update();

        if (drawPointCloud) drawPointCloud(ui.pointCloudScale, cursor);
        if (drawRoads) simulation.graphs.forEach(this::drawGraph);
        if (currentRoute != null) drawCurrentRoute();
//        if (drawRoads) simulation.graphs.forEach(this::drawGraph);
        if (drawHistoryTracks) drawHistoryTracks();
        drawCityAgents(drawAgents, drawTracks);
        if (drawAttractors) simulation.attractors.forEach(a -> {
            if (a instanceof Tweet) drawTweet((Tweet) a);
            else drawAttractor(a);
        });

        //Draw cursor
        pushStyle();
        strokeWeight(1);
        stroke(0xffff0000);
        cross(10, cursor.x, cursor.y, cursor.z);
        popStyle();

        popMatrix();
        drawUI();

//        image(bakedRoads, 0, 0, 500, 500);
    }

    private void cross(int i, float x, float y, float z) {
        line(x - i, y, z, x + i, y, z);
        line(x, y - i, z, x, y + i, z);
    }

    void drawUI() {
        hint(DISABLE_DEPTH_TEST);
        // camera.beginHUD();
        ui.draw();
        // camera.endHUD();
        hint(ENABLE_DEPTH_TEST);
    }

    private void loadData(Table table) {
        int m = 1000;

        table.rows().forEach(row -> {
            float lat = row.getFloat("lat");
            float lon = row.getFloat("lon");
            int runner = row.getInt("runner");
            int pedestrian = row.getInt("pedestrian");
            int bike = row.getInt("bike");
            int transport = row.getInt("transport");
            String type = row.getString("type");

            float attractionMass = 100;
            int i = 0xffffff00;
            LatLon coord = new LatLon(lat, lon);
            PVector loc = projector.project(coord);

            Attractor a = new Attractor(type, attractionMass, loc);
            a.setColor(i);
            simulation.addAttractor(a);

            int period;
            period = min(10000, pedestrian * m);
            EmitterFactory.createPedestrian(coord, period);
//            EmitterFactory.createRunner(coord, (int) (runner * m));
//            EmitterFactory.createBike(coord, (int) (bike * m));
//            EmitterFactory.createTransport(coord, (int) (transport * m));
        });
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
        simulation.addGraphLayer(graph);
    }

    private void loadAttractors(GeoJSON fc, String type, float mass, int color) {
        ArrayList<Feature> features = fc.getFeatures();
        features.stream()
                .filter(feature -> Objects.equals(feature.geometry.type, "Point"))
                .forEach(feature -> {
                    LatLon ll = feature.geometry.coords.get(0);
                    PVector p = projector.project(ll);
                    Attractor a = new Attractor(type, mass, p);
                    a.setColor(color);
                    simulation.addAttractor(a);
                });
    }

    private void loadTweets(int color, Table table) {
        table.rows().forEach(row -> {
            float lat = row.getFloat("lat");
            float lon = row.getFloat("lon");
            int followers = row.getInt("followers");

//            followers *= 100;

            PVector loc = projector.project(new LatLon(lat, lon));
            Tweet a = new Tweet(loc, row.getString("tweet"), row.getString("username"), followers);
            a.setColor(color);
            simulation.addAttractor(a);
        });
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
//        return camera.getCoordAtScreen(mouseX, mouseY);
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

    private void drawPointCloud(float scale, PVector pos) {
        pushStyle();
        pushMatrix();
        translate(pos.x, pos.y, pos.z);
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

    private void drawCurrentRoute() {
        strokeWeight(1);
        stroke(235);
        noFill();
        beginShape();
        for (PVector p : currentRoute) vertex(p.x, p.y);
        endShape();
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
        stroke(agent.color);
        strokeWeight(agent.mass);
        point(agent.location.x, agent.location.y);
    }

    private void drawAttractor(Attractor attractor) {
        int w = 2;//map(attractor.getMass(), 0, 1, 0, 10)
        PVector loc = attractor.getLocation();
        stroke(attractor.getColor());
        strokeWeight(w);
        point(loc.x, loc.y);
    }

    private void drawTweet(Tweet tweet) {
//        int w = 2;
        PVector loc = tweet.getLocation();
        stroke(tweet.getColor());
//        strokeWeight(w);
//        point(loc.x, loc.y);

        float r = tweet.getMass() * 2;
        if (r > 50) {
            strokeWeight(1);
            noFill();
            ellipse(loc.x, loc.y, r, r);
        }

        String text = tweet.username;
        if (drawTweets) text(text, loc.x, loc.y);
    }

    private void drawHistoryTracks() {
        simulation.tracks.forEach(this::drawTrack);
    }

    private void drawTrack(Track track) {
        pushStyle();
        noFill();
        stroke(red(track.color), green(track.color), blue(track.color), 50);
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
