import com.tmshv.swarm.core.*;
import com.tmshv.swarm.utils.ColorUtil;
import com.tmshv.swarm.utils.GraphUtils;
import geojson.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.Table;

import java.util.ArrayList;
import java.util.Objects;

public class App extends PApplet {
    private Crossroad crossroadStart = null;
    private Crossroad crossroadFinish = null;

    private boolean drawRoads = true;
    private boolean drawAgents = true;
    private boolean drawAgentInfo = false;
    private boolean drawTracks = true;
    private boolean drawHistoryTracks = true;
    private boolean drawAttractors = true;
    private boolean drawTweets = false;
    private boolean drawPointCloud = false;
    private float cameraStep = 10;
    private int currentGraphIndex = 0;

    private ArrayList<PVector> currentRoute;

    UI ui;
    Simulation simulation;
    Camera camera;
    SphericalMercator projector;
    Navigator navigator;

    LatLon cursorCoord;
    PVector cursor;
    LatLon centerCoord;

    private ArrayList<PVector> pointCloud;

    private PGraphics bakedRoads;
    private PImage img;

    public void settings() {
        size(1280, 800, P3D);
//        fullScreen(P3D);
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

        centerCoord = new LatLon();
        cursorCoord = new LatLon();
        cursor = new PVector();

        pointCloud = new ArrayList<>();
    }

    public void draw() {
        background(0);

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

        if (drawPointCloud) drawPointCloud(ui.pointCloudScale, cursor, ui.pointCloudRotation);
        if (drawRoads) simulation.graphs.forEach(this::drawGraph);
        if (currentRoute != null) drawCurrentRoute();
//        if (drawRoads) simulation.graphs.forEach(this::drawGraph);
        if (drawHistoryTracks) simulation.tracks.forEach(this::drawTrack);
        drawCityAgents(drawAgents, drawTracks);
        if (drawAttractors) simulation.attractors.forEach(a -> {
            if (a instanceof Tweet) drawTweet((Tweet) a);
            else drawAttractor(a);
        });

        if(drawAgentInfo){
            IAgent nearest = simulation.getNearestAgent(cursor);
            if (nearest != null) drawAgentInfo(nearest);
        }

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

    private void drawUI() {
        hint(DISABLE_DEPTH_TEST);
        ui.draw();
        hint(ENABLE_DEPTH_TEST);
    }

    void loadData(Table table) {
        int m = 1000;
        int minPeriod = 10000;

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

            if (pedestrian > 0) EmitterFactory.createPedestrian(coord, max(minPeriod, pedestrian * m));
            if (runner > 0) EmitterFactory.createRunner(coord, max(minPeriod, runner * m));
            if (bike > 0) EmitterFactory.createBike(coord, max(minPeriod, bike * m));
            if (transport > 0) EmitterFactory.createTransport(coord, max(minPeriod, transport * m));
        });
    }

    private CityGraph loadFeatures(IFeatureCollection features) {
        CityGraph graph = new CityGraph();
        for (Feature f : features.getFeatures()) {
            Path path = new Path();
            f.geometry.coords
                    .stream()
                    .map(projector::project)
                    .forEach(path::add);
            graph.createRoad(path);
        }
//        features.getFeatures()
//                .forEach(f -> {
//                    Path path = new Path();
//                    f.geometry.coords
//                            .stream()
//                            .map(projector::project)
//                            .forEach(path::add);
//                    graph.createRoad(path);
//                });
        return graph;
    }

    void loadRoadLayer(String filename, String name, int color, int thickness) {
        IFeatureCollection geo;
        geo = new GeoJSON(loadJSONObject(filename));
        geo = new FeatureExploder(geo);
        geo = new FeatureOptimizer(geo);
        CityGraph graph = loadFeatures(geo);
        graph.strokeColor = color;
        graph.strokeThickness = thickness;
        simulation.addGraphLayer(graph, name);
    }

    void loadAttractors(GeoJSON fc, String type, float minMass, float maxMass, int color) {
        ArrayList<Feature> features = fc.getFeatures();
        features.stream()
                .filter(feature -> Objects.equals(feature.geometry.type, "Point"))
                .forEach(feature -> {
                    float mass = random(minMass, maxMass);
                    LatLon ll = feature.geometry.coords.get(0);
                    PVector p = projector.project(ll);
                    Attractor a = new Attractor(type, mass, p);
                    a.setColor(color);
                    simulation.addAttractor(a);
                });
    }

    void loadTweets(int color, Table table) {
        table.rows().forEach(row -> {
            float lat = row.getFloat("lat");
            float lon = row.getFloat("lon");
            int followers = row.getInt("followers");

            PVector loc = projector.project(new LatLon(lat, lon));
            Tweet a = new Tweet(loc, row.getString("tweet"), row.getString("username"), followers);
            a.setColor(color);
            simulation.addAttractor(a);
        });
    }

    void loadPointCloud(Table table) {
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


    void setStartPoint(LatLon ll) {
        PVector v = projector.project(ll);
        crossroadStart = simulation.graph(currentGraphIndex).findNearestCrossroadTo(v);
    }

    void setEndPoint(LatLon ll) {
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
        pointCloud.forEach(v -> vertex(v.x * scale, v.y * scale, v.z * scale));
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
                .forEach(a -> {
                    if (drawTrack) drawTrack(a.getTrack());
                    if (drawVehicle) drawAgent(a);
                });
    }

    protected void drawAgent(IAgent agent) {
        int color = agent.getColor();
        stroke(color);
        strokeWeight(2);
        PVector loc = agent.getLocation();
        point(loc.x, loc.y, loc.z);
    }

    private void drawAttractor(Attractor attractor) {
        PVector loc = attractor.getLocation();

        if (Objects.equals(attractor.getType(), "tree")) {
            float h = 10;
            stroke(attractor.getColor());
            strokeWeight(1);
            line(loc.x, loc.y, loc.z, loc.x, loc.y, loc.z + h);

            float r = map(attractor.getMass(), 5, 15, 1, 3);

            fill(ColorUtil.setAlpha(attractor.getColor(), 30));
            pushMatrix();
            translate(loc.x, loc.y, loc.z + h);
            ellipse(0, 0, r, r);
//            sphere(r);
            popMatrix();

        } else {
            stroke(attractor.getColor());
            strokeWeight(2);
            point(loc.x, loc.y, loc.z);
        }
    }

    private void drawAgentInfo(IAgent agent) {
        int color = agent.getColor();
        PVector loc = agent.getLocation();
        pushStyle();
        stroke(color);
        noFill();
//        stroke(0xcc000000);
        strokeWeight(1);
//        joint(agent.getLocation(), cursor);
        agent.getCurrentAttractors().forEach(a -> joint(loc, a.getLocation()));

        strokeWeight(1);
        stroke(ColorUtil.setAlpha(color, 50));
        float s = agent.getInterestDistance() * 2;
        ellipse(loc.x, loc.y, s, s);
        popStyle();
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
        if (drawTweets) text(text, loc.x, loc.y);
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
        graph.roads.forEach(path -> drawPath(path.getPath(), graph.strokeColor));
    }

    private void drawPath(Path road, int color) {
        pushStyle();
        noFill();

        float weight = (float) road.getRadius();
        if (weight > 0) {
            stroke(ColorUtil.setAlpha(color, 10));
            strokeWeight(weight);
            beginShape();
            road.coords.forEach(v -> vertex(v.x, v.y, v.z));
            endShape();
        }

        stroke(color);
        strokeWeight(1);
        beginShape();
        road.coords.forEach(v -> vertex(v.x, v.y, v.z));
        endShape();

        popStyle();
    }

    private void joint(PVector c1, PVector c2) {
//        PVector c = com.tmshv.agents.utils.GeometryUtils.interpolate(c1, c2, 0.5f);
//        float radius = c.mag() / 2;

        float radius = c1.dist(c2);

        PVector h = new PVector(0, 0, -radius);
        PVector a1 = PVector.add(c1, h);
        PVector a2 = PVector.add(c2, h);

        curve(
                a1.x, a1.y, a1.z,
                c1.x, c1.y, c1.z,
                c2.x, c2.y, c2.z,
                a2.x, a2.y, a2.z
        );
    }

    public void keyPressed() {
        if (keyCode == UP) camera.moveTarget(0, -cameraStep);
        if (keyCode == DOWN) camera.moveTarget(0, cameraStep);
        if (keyCode == LEFT) camera.moveTarget(-cameraStep, 0);
        if (keyCode == RIGHT) camera.moveTarget(cameraStep, 0);

        if (key == ' ') saveFrame("app.jpg");

        if (key == 'g') bakeGraph();
        if (key == 'm') println(getLatLonCursor());
        if (key == 'u') ui.print();
        if (key == 'c') camera.print();

        if (key == ']') selectNextGraph();
        if (key == '[') selectPrevGraph();

        if (key == '1') drawRoads = !drawRoads;
        if (key == '2') drawAgents = !drawAgents;
        if (key == '3') drawTracks = !drawTracks;
        if (key == '4') drawHistoryTracks = !drawHistoryTracks;
        if (key == '5') drawAttractors = !drawAttractors;
        if (key == '6') drawTweets = !drawTweets;
        if (key == '7') drawPointCloud = !drawPointCloud;
        if (key == '8') drawAgentInfo = !drawAgentInfo;
    }

    private void bakeGraph() {
        String dump = GraphUtils.bake(simulation.graphs.get(currentGraphIndex).graph);
        println(dump);
    }
}
