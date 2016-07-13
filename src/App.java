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

    Crossroad crStart = null;
    Crossroad crFinish = null;

    Driver defaultDriver;
    Road nearestRoad;

    public boolean drawRoads = false;
    public boolean drawAgents = true;
    public boolean drawTracks = true;
    public boolean drawCrossroads = false;
    public boolean drawNodes = true;

    public boolean drawEdges = true;

    public boolean drawGraphRoute = true;
    public boolean drawGraphMode = true;
    public boolean drawRoute = true;
    public boolean drawNearest = true;

    private int currentGraphIndex = 0;

    ArrayList<PVector> currentRoute;

    City city;
    Camera camera;

    SphericalMercator projector;

    public void settings() {
        fullScreen();
        smooth();
    }

    public void setup() {
        background(0);

        projector = new SphericalMercator();
        projector.scale = .5f;

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
        Route r = defaultDriver.navigate(crStart, crFinish);
        if (r != null) currentRoute = r.bake();

        emitAgent();
    }

    Driver createAgent() {
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
        // nearestRoad = city.graph(0).findNearestRoadTo(mouse);

        // // Route route = null;
        // GraphNode[] route = null;
        // if(drawGraphRoute && crFinish != null){
        //   int nearestCrossroadId = city.getCrossroadIndex(n);
        //   int finishCrossroadId = city.getCrossroadIndex(crFinish);
        //   route = defaultDriver.findRoute(nearestCrossroadId, finishCrossroadId);
        // }

        if (mousePressed) {
            if (mouseButton == LEFT) setStartPoint(mouse);
            if (mouseButton == RIGHT) setEndPoint(mouse);

            if (crStart != null && crFinish != null) {
                Route r = defaultDriver.navigate(crStart, crFinish);
                if (r != null) currentRoute = r.bake();
            }
        }

        pushMatrix();
        camera.update(this);
        city.update();

        // city.drawCrossroads();
        drawCityRoads(city, null);

        if (currentRoute != null) {
            drawCurrentRoute();
        }

        // // stroke(255);
        // // noFill();
        // // PVector pmouse = projector.project(mouse);
        // // ellipseMode(CENTER);
        // // ellipse(pmouse.x, pmouse.y, 10, 10);

        // if (drawNodes) drawGraphNodes(city.graph.getNodeArray());
        // if (drawEdges) drawGraphEdges(city.graph.getAllEdgeArray());
        // if (drawRoads) city.drawRoads(drawNearest ? nearestRoad : null);
        // if (drawCrossroads) city.drawCrossroads();
        drawCityAgents(city, drawAgents, drawTracks);

        // // drawEdgeProj(nearestRoad, mouse);

        // int routelength = 0;
        // if(route != null){
        //   if(drawRoute){
        //     if(drawGraphMode) drawGraphRoute(route);
        //     else drawRoute(new Route(city, route));
        //   }
        // }

        // if(crStart != null && crFinish != null){
        //   noFill();
        //   stroke(0, 0, 255);
        //   strokeWeight(1);
        //   ellipseMode(CENTER);
        //   PVector c;
        //   c = projector.project(crStart.coord);
        //   ellipse(c.x, c.y, 5, 5);

        //   c = projector.project(crFinish.coord);
        //   ellipse(c.x, c.y, 10, 10);
        // }

        popMatrix();
    }

    public void setStartPoint(LatLon ll) {
        PVector v = projector.project(ll);
        Crossroad n = city.graph(0).findNearestCrossroadTo(v);
        crStart = n;
    }

    public void setEndPoint(LatLon ll) {
        PVector v = projector.project(ll);
        Crossroad n = city.graph(0).findNearestCrossroadTo(v);
        crFinish = n;
    }

    public void emitAgent() {
        Driver d = createAgent();
        Route r = d.navigate(crStart, crFinish);
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

// void drawEdgeProj(GraphEdge ge, LatLon ll){
//   if(ge == null) return;

//   PVector v = projector.project(ll);

//   LatLon fromCoord = city.graphs.get(currentGraphIndex).getGraphNodeCoord(ge.from());
//   LatLon toCoord = city.graphs.get(currentGraphIndex).getGraphNodeCoord(ge.to());

//   PVector p1 = projector.project(fromCoord);
//   PVector p2 = projector.project(toCoord);

//   PVector p = GeometryUtils.projectVertexOnLine(v, p1, p2);

//   stroke(#ff0000);
//   fill(#990000);
//   ellipseMode(CENTER);
//   ellipse(p.x, p.y, 5, 5);
// }

// void drawGraphNodes(GraphNode[] nodes){
//   pushStyle();
//   float s = 10;
//   for(GraphNode node : nodes){
//     LatLon nodeCoord = city.graphs.get(currentGraphIndex).getGraphNodeCoord(node);
//     PVector p = projector.project(nodeCoord);

//     noStroke();
//     fill(0, 255, 0, 75);
//     ellipse(p.x, p.y, s, s);

//     noStroke();
//     fill(255, 75);
//     text(str(node.xf()) + ";" + str(node.yf()), p.x, p.y);
//   }
//   popStyle();
// }

// void drawGraphEdges(GraphEdge[] edges){
//   pushStyle();
//   noFill();

//   GraphEdge nearest = null;
//   if(nearestRoad != null) nearest = nearestRoad.edge;

//   for(GraphEdge ge : edges){
//     stroke(200, 50);
//     if(drawNearest && ge == nearest) stroke(#ffff00);

//     LatLon fromCoord = city.graphs.get(currentGraphIndex).getGraphNodeCoord(ge.from());
//     LatLon toCoord = city.graphs.get(currentGraphIndex).getGraphNodeCoord(ge.to());
//     PVector p1 = projector.project(fromCoord);
//     PVector p2 = projector.project(toCoord);

//     line(p1.x, p1.y, p2.x, p2.y);

//     pushMatrix();
//     PVector diff = PVector.sub(p2, p1);
//     float a = atan2(diff.y, diff.x);
//     translate(p2.x, p2.y);
//     rotate(a - HALF_PI);

//     float s = 15;
//     float sr = 3;
//     triangle(-s/sr, -s, s/sr, -s, 0, 0);

//     popMatrix();
//   }
//   popStyle();
// }

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
        if (key == '3') drawCrossroads = !drawCrossroads;
        if (key == '4') drawNodes = !drawNodes;
        if (key == '5') drawEdges = !drawEdges;
        if (key == '6') drawGraphRoute = !drawGraphRoute;
        if (key == '7') drawRoute = !drawRoute;
        if (key == '8') drawNearest = !drawNearest;
        if (key == '9') drawTracks = !drawTracks;
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

    public void drawCityAgents(City city, boolean drawVehicle, boolean drawTrack) {
        for (Vehicle a : city.vehicles) {
            if (drawVehicle) drawVehicle(a);
            if (drawTrack) drawTrack(a.track);
        }
    }

    //    public void drawCrossroads() {
//        // for (CityGraph cg : graphs) cg.drawCrossroads();
//    }


    public void drawCityRoads(City city, Road selected) {
        for (CityGraph cg : city.graphs) drawRoads(cg, selected);
    }

    public void bakeGraph() {
        String dump = GraphUtils.bake(city.graphs.get(currentGraphIndex).graph);
        println(dump);
    }
}
