import com.tmshv.agents.core.*;
import geojson.GeoJSON;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created at 22/07/16
 *
 * @author tmshv
 */
public class FollowPathApp extends App {
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"FollowPathApp"};
        PApplet.main(concat(appletArgs, passedArgs));
    }

    private Path path;
    private Agent agent;

    @Override
    public void setup() {
        super.setup();

        simulation.setAgentsLimit(100);

        CityGraph graph = new CityGraph();
        graph.strokeColor = 0x99999999;
        graph.strokeThickness = 1;
        simulation.addGraphLayer(graph, "road");

        path = new Path(40.0);
        path.add(new PVector());
        path.add(new PVector(200f, 0f));
//        path.add(new PVector(200f, 200f));
        path.add(new PVector(300f, 100f));
        path.add(new PVector(600f, -100f));
        path.add(new PVector(300f, -100f));
        graph.createRoad(path);
        simulation.addGraphLayer(graph, "road");
    }

    @Override
    public void draw() {
        camera.lookAt(new PVector(100f, 0f));

        super.draw();

//        if(agent != null) drawAgent(agent);
    }

    @Override
    public void keyPressed() {
        super.keyPressed();

        if (key == 'a') emitAgent();
    }

    private void emitAgent() {
        Follower a = AgentFactory.createFollower("agent", .3f, .01f, 0, 50f, 10f, 0xffff0000);
        a.setLifetime(10000);
        simulation.addAgent(a);

        a.predictMult = 30f;
        a.move(path.coords);
        a.getLocation().set(new PVector(-120f, 100f));

//        Route r = navigator.navigate(path);
//        a.move(r);

        agent = a;
    }

    @Override
    protected void drawAgent(IAgent agent) {
        PVector loc;
        Follower f = (Follower) agent;

        if (f != null) {
            pushStyle();
            noFill();
            stroke(0x33ffffff);
            strokeWeight(1);
            beginShape();
            f.route.forEach(v -> vertex(v.x, v.y, v.z));
            endShape();
            popStyle();

            pushStyle();
            strokeWeight(3);
            noFill();

            stroke(f.getColor());
            loc = f.getLocation();
            point(loc.x, loc.y, loc.z + 5);

            stroke(0xffffffff);
            loc = f.predictLocation;
            point(loc.x, loc.y, loc.z + 5);

            stroke(0xff00ffff);
            loc = f.normalLocation;
            point(loc.x, loc.y, loc.z + 5);

            stroke(0xffffff00);
            loc = f.targetLocation;
            point(loc.x, loc.y, loc.z + 5);

            strokeWeight(1);
            stroke(0x44ffffff);
            line(f.normalLocation.x, f.normalLocation.y, f.predictLocation.x, f.predictLocation.y);

            strokeWeight(1);
            stroke(0x44ff0000);
            line(f.getLocation().x, f.getLocation().y, f.predictLocation.x, f.predictLocation.y);

            popStyle();
        }
    }
}
