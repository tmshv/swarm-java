import geojson.*;
import processing.core.PApplet;
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

    private boolean drawRoads = true;
    private boolean drawAgents = true;
    private boolean drawTracks = true;
    private boolean drawHistoryTracks = true;
    private boolean drawAttractors = true;
    private boolean drawTweets = false;
    private float cameraStep = .00125f;
    private int currentGraphIndex = 0;

    private ArrayList<PVector> currentRoute;

    private Simulation simulation;
    private Camera camera;
    private SphericalMercator projector;

    public void settings() {
        fullScreen();
        smooth();
    }

    public void setup() {
        background(0);

        projector = new SphericalMercator(0.80d);
        camera = new Camera(projector);
        simulation = new Simulation();
        navigator = new Navigator(simulation, 2, projector);

        loadRoadLayer("road-transport.geojson", 0xffdddddd);
        loadRoadLayer("road-pedestrian.geojson", 0xff444444);

        loadAttractors(new GeoJSON(loadJSONObject("trees.geojson")), "tree", 10, 0xff00ff00);
        loadTweets(loadTable("runner3.csv", "header"), 0xff0022aa);
        loadTweets(loadTable("tourist-pedestrian.csv", "header"), 0xff0022aa);

        camera.setOffset(new PVector(width / 2, height / 2));
        camera.lookAt(new LatLon(55.74433f, 37.615776f));

        setStartPoint(new LatLon(55.73898f, 37.605858f));
        setEndPoint(new LatLon(55.743206f, 37.607254f));

        setStartPoint(new LatLon(55.73898f, 37.605858f));
        setEndPoint(new LatLon(55.739960443216781f, 37.617145380088019f));
//        emitAgent();

        Route r = navigator.navigate(crossroadStart, crossroadFinish);
        if (r != null) currentRoute = r.bake();

        AgentFactory.init(this, projector, simulation, navigator);

        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));

        AgentFactory.createBikeAgent(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createBikeAgent(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createBikeAgent(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createBikeAgent(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createBikeAgent(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));

        AgentFactory.createBoids(new LatLon(55.746178f, 37.615578f), "bird", 30);

//        simulation.addAttractor(new Attractor("a", 100, projector.project(new LatLon(55.73998f, 37.616058f))));
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

    public void draw() {
        background(0);
        LatLon mouse = getLatLonCursor();

        if (mousePressed) {
            if (mouseButton == LEFT) setStartPoint(mouse);
            if (mouseButton == RIGHT) setEndPoint(mouse);

            if (crossroadStart != null && crossroadFinish != null) {
                Route r = navigator.navigate(crossroadStart, crossroadFinish);
                if (r != null) currentRoute = r.bake();
            }
        }

        pushMatrix();
        camera.update(this);
//        rotateX(HALF_PI / 2);

        simulation.update();

        if (currentRoute != null) drawCurrentRoute();
        if (drawRoads) drawCityRoads(simulation, null);
        if(drawHistoryTracks) drawHistoryTracks();
        drawCityAgents(drawAgents, drawTracks);
        if (drawAttractors) simulation.attractors.forEach(a -> {
            if (a instanceof Tweet) drawTweet((Tweet) a);
            else drawAttractor(a);
        });

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

    private LatLon getLatLonCursor() {
        return camera.getCoordAtScreen(mouseX, mouseY);
    }


    private void setStartPoint(LatLon ll) {
        PVector v = projector.project(ll);
        crossroadStart = simulation.graph(currentGraphIndex).findNearestCrossroadTo(v);
    }

    private void setEndPoint(LatLon ll) {
        PVector v = projector.project(ll);
        crossroadFinish = simulation.graph(currentGraphIndex).findNearestCrossroadTo(v);
    }

    private void emitAgent() {
//        Vehicle a = createVehicle("");
//        Route r = navigator.navigate(crossroadStart, crossroadFinish);
//        a.move(r);
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
        currentGraphIndex %= simulation.graphs.size();
    }

    private void selectPrevGraph() {
        if (currentGraphIndex > 0) currentGraphIndex--;
    }

    private void drawHistoryTracks() {
        simulation.tracks.forEach(this::drawTrack);
    }

    private void drawTrack(Track track) {
        noFill();
        // stroke(255, 255, 0, 50);
        stroke(red(track.color), green(track.color), blue(track.color), 50);
        strokeWeight(1);
        beginShape();
        track.history.forEach(v -> vertex(v.x, v.y));
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
        int w = 2;
        PVector loc = tweet.getLocation();
        stroke(tweet.getColor());
        strokeWeight(w);
        point(loc.x, loc.y);

        String text = tweet.username;
        if (drawTweets) text(text, loc.x, loc.y);
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
        if (keyCode == UP) camera.moveTarget(cameraStep, 0);
        if (keyCode == DOWN) camera.moveTarget(-cameraStep, 0);
        if (keyCode == LEFT) camera.moveTarget(0, -cameraStep);
        if (keyCode == RIGHT) camera.moveTarget(0, cameraStep);

        if (key == ' ') saveFrame("../frame-###.jpg");

        if (key == 'g') bakeGraph();
        if (key == 'e') emitAgent();
        if (key == 'm') println(getLatLonCursor());

        if (key == ']') selectNextGraph();
        if (key == '[') selectPrevGraph();

        if (key == '1') drawRoads = !drawRoads;
        if (key == '2') drawAgents = !drawAgents;
        if (key == '3') drawTracks = !drawTracks;
        if (key == '4') drawHistoryTracks = !drawHistoryTracks;
        if (key == '5') drawAttractors = !drawAttractors;
        if (key == '6') drawTweets = !drawTweets;
    }

    private void drawCityRoads(Simulation simulation, Road selected) {
        for (CityGraph cg : simulation.graphs) drawRoads(cg, selected);
    }

    private void bakeGraph() {
        String dump = GraphUtils.bake(simulation.graphs.get(currentGraphIndex).graph);
        println(dump);
    }
}
