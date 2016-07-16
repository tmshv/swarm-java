import geojson.*;
import processing.core.PApplet;
import processing.core.PVector;
import processing.data.Table;

import java.util.ArrayList;
import java.util.Iterator;
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
    private LatLon centerCoord;

    private ArrayList<PVector> pointCloud;

    public void settings() {
        fullScreen(P3D);
        smooth();
    }

    public void setup() {
        background(0);

        ui = new UI(this, 10, 10);

        projector = new SphericalMercator(0.80d);
        camera = new Camera(projector);
        simulation = new Simulation();
        navigator = new Navigator(simulation, 2, projector);

        centerCoord = new LatLon(55.74317f, 37.61516f);
        cursorCoord = new LatLon(55.74317f, 37.61516f);

        pointCloud = new ArrayList<>();
        loadPointCloud(loadTable("udarnik.csv", "header"));

        loadRoadLayer("road-transport.geojson", 0xffdddddd);
        loadRoadLayer("road-pedestrian.geojson", 0xff444444);

        loadAttractors(new GeoJSON(loadJSONObject("trees.geojson")), "tree", 10, 0xff00ff00);
        loadTweets(loadTable("runner3.csv", "header"), 0xff0022aa);
        loadTweets(loadTable("tourist-pedestrian.csv", "header"), 0xff0022aa);

//        int cameraZ = -500;
//        camera.setOffset(new PVector(width / 2, height / 2, cameraZ));
        camera.lookAt(new LatLon(55.74433f, 37.615776f));
        camera.lookAt(new LatLon(55.74317f, 37.61516f));

        setStartPoint(new LatLon(55.73898f, 37.605858f));
        setEndPoint(new LatLon(55.743206f, 37.607254f));

        setStartPoint(new LatLon(55.73898f, 37.605858f));
        setEndPoint(new LatLon(55.739960443216781f, 37.617145380088019f));

        Route r = navigator.navigate(crossroadStart, crossroadFinish);
        if (r != null) currentRoute = r.bake();

        EmitterFactory.init(projector, simulation);
        AgentFactory.init(this, projector, simulation, navigator);

//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//
//        AgentFactory.createBikeAgent(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createBikeAgent(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createBikeAgent(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createBikeAgent(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createBikeAgent(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//
//        AgentFactory.createPedestrianAgent(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrianAgent(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrianAgent(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrianAgent(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrianAgent(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));

//        AgentFactory.createBoids(new LatLon(55.746178f, 37.615578f), "bird", 30);
//        EmitterFactory.createBoids(new LatLon(55.746178f, 37.615578f), "bird", 10 * 1000);

        simulation.addAttractor(new Attractor("a", 100, projector.project(new LatLon(55.73998f, 37.616058f))));
    }

    public void draw() {
        background(0);
        LatLon mouse = getLatLonCursor();

        ui.update();

        cursorCoord.lat = centerCoord.lat + ui.cursorLat;
        cursorCoord.lon = centerCoord.lon + ui.cursorLon;

        if (mousePressed) {
            if (mouseButton == LEFT) setStartPoint(mouse);
            if (mouseButton == RIGHT) setEndPoint(mouse);

            if (crossroadStart != null && crossroadFinish != null) {
                Route r = navigator.navigate(crossroadStart, crossroadFinish);
                if (r != null) currentRoute = r.bake();
            }
        }

        float rx = map(mouseY, 0, height, 0, HALF_PI);
        pushMatrix();
//        camera(mouseX, height/2, (height/2) / tan(PI/6), width/2, height/2, 0, 0, 1, 0);
//        scale(ui.scale);
        rotateX(ui.rotation);
        rotateZ(ui.rotationZ);
        camera.update(this);
        translate(0, 0, ui.positionZ);
        translate(width / 2, height / 2);

        simulation.update();

        if(drawPointCloud) {
            PVector pcloc = projector.project(new LatLon(55.74317f, 37.61516f));
            drawPointCloud(ui.pointCloudScale, pcloc);
        }

        if (currentRoute != null) drawCurrentRoute();
        if (drawRoads) drawCityRoads(simulation);
        if (drawHistoryTracks) drawHistoryTracks();
        drawCityAgents(drawAgents, drawTracks);
        if (drawAttractors) simulation.attractors.forEach(a -> {
            if (a instanceof Tweet) drawTweet((Tweet) a);
            else drawAttractor(a);
        });

        //Draw cursor
        pushStyle();
        strokeWeight(5);
        stroke(0xffff0000);
        PVector cursor = projector.project(cursorCoord);
        point(cursor.x, cursor.y, cursor.z);
        popStyle();

        popMatrix();
        drawUI();
    }

    void drawUI() {
        hint(DISABLE_DEPTH_TEST);
        // camera.beginHUD();
        ui.draw();
        // camera.endHUD();
        hint(ENABLE_DEPTH_TEST);
    }

    private void loadRoadLayer(String filename, int color) {
        IFeatureCollection geo = new GeoJSON(loadJSONObject(filename));
        FeatureExploder fx = new FeatureExploder(geo);
        CityGraph streets = new CityGraph(fx, projector);
        streets.strokeColor = color;
        simulation.addGraphLayer(streets);
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

    private void loadTweets(Table table, int color) {
        table.rows().forEach(row -> {
            float lat = row.getFloat("lat");
            float lon = row.getFloat("lon");
            int followers = row.getInt("followers");

            followers *= 100;

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
        stroke(255);
        strokeWeight(1);
        pointCloud.stream()
                .map(v -> {
                    v.mult(scale);
                    return v;
                })
                .forEach(v -> point(v.x, v.y, v.z));
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
//        Iterator<IAgent> i = simulation.agents.iterator();
//        while (i.hasNext()) {
//            Agent a = (Agent) i.next();
//
//            if (a != null) {
//                if (drawVehicle) drawAgent(a);
//                if (drawTrack) drawTrack(a.getTrack());
//            }
//        }

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
        int w = 2;
        PVector loc = tweet.getLocation();
        stroke(tweet.getColor());
        strokeWeight(w);
        point(loc.x, loc.y);

        String text = tweet.username;
        if (drawTweets) text(text, loc.x, loc.y);
    }

    private void drawHistoryTracks() {
        simulation.tracks.forEach(this::drawTrack);
    }

    private void drawTrack(Track track) {
        pushStyle();
        noFill();
        // stroke(255, 255, 0, 50);
        stroke(track.color);
//        stroke(red(track.color), green(track.color), blue(track.color), 50);
        strokeWeight(1);
        beginShape();
        track.history.forEach(v -> vertex(v.x, v.y, v.z));
        endShape();
        popStyle();
    }

    private void drawRoads(CityGraph cg) {
        pushStyle();
        noFill();
        for (Road road : cg.roads) {
//            stroke(cg.strokeColor);
            stroke(255);
//            strokeWeight(cg.strokeThickness);
            strokeWeight(1);

            beginShape();
            road.coords.forEach(v -> vertex(v.x, v.y, v.z));
            endShape();
        }
        popStyle();
    }

    private void drawCityRoads(Simulation simulation) {
        for (CityGraph cg : simulation.graphs) drawRoads(cg);
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
