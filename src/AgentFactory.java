import geojson.IProjector;
import geojson.LatLon;
import processing.core.PApplet;
import processing.core.PVector;

import static java.lang.Math.PI;

/**
 * Created at 15/07/16
 *
 * @author tmshv
 */
class AgentFactory {
    private static PApplet app;
    private static IProjector projector;
    private static Simulation simulation;
    private static Navigator navigator;

    static void init(PApplet app, IProjector projector, Simulation simulation, Navigator navigator) {
        AgentFactory.app = app;
        AgentFactory.projector = projector;
        AgentFactory.simulation = simulation;
        AgentFactory.navigator = navigator;
    }

    static RandomWalker createRandomWalker(LatLon loc, String type, float mass, int color) {
        float maxSpeed = random(1, 10);
        float maxForce = random(1, 4);

        RandomWalker a = new RandomWalker(type, maxSpeed, maxForce, color);
        a.location.set(projector.project(loc));
        a.mass = mass;
        simulation.addAgent(a);
        return a;
    }

    static Agent createFlyAgent(LatLon latLon) {
        Agent a = createRandomWalker(latLon, "fly", 1f, 0xFFCCCCCC);
        a.setLifetime(600);
        a.addInteractionType("tree");
        return a;
    }

    static Agent createPedestrianAgent(LatLon loc, LatLon target) {
        Route route = navigator.navigate(loc, target);
        if (route == null) return null;

        float maxSpeed = random(1, 2);
        float maxForce = random(1, 3);

        Vehicle a = createVehicle("pedestrian", maxSpeed, maxForce);
        a.setLifetime(1000);
        a.addInteractionType("tweet");
        a.move(route);
        a.color = 0xFFFF0000;
        return a;
    }

    static Agent createBikeAgent(LatLon loc, LatLon target) {
        Route route = navigator.navigate(loc, target);
        if (route == null) return null;

        float maxSpeed = random(3, 5);
        float maxForce = random(3, 5);

        Vehicle a = createVehicle("bike", maxSpeed, maxForce);
        a.setLifetime(1000);
        a.addInteractionType("tweet");
        a.move(route);
        a.color = 0xFFFFFF00;
        return a;
    }

    static Vehicle createVehicle(String type, float maxSpeed, float maxForce) {
        int[] colors = new int[]{0xffff0000, 0xffffff00, 0xff00ff00, 0xff00ffff, 0xffff00ff, 0xffffffff, 0xffcccccc};
        int c = colors[(int) (random(0, colors.length - 1))];
        int size = (int) random(2, 5);

        float predictMult = random(10, 50);
        float dirMult = random(2, 10);

        Vehicle v = new Vehicle(type, maxSpeed, maxForce, c);
        v.mass = size;
        v.predictMult = predictMult;
        v.dirMult = dirMult;
        simulation.addAgent(v);

        return v;
    }

    static Boids createBoids(LatLon latLon, String type, int num) {
        int c = 0xff666666;

        PVector loc = projector.project(latLon);
        float maxSpeed = random(1, 10);
        float maxForce = random(1, 4);

        Boids flock = new Boids(maxSpeed, maxForce, c);
        simulation.addAgent(flock);

        for (int i = 0; i < num; i++) {
            float angle = random(0, (float) (PI * 2));
            PVector vel = PVector.fromAngle(angle);

            Agent a = new Agent(type, maxSpeed, maxForce, c);
            a.setLifetime(400);
            a.mass = 2;
            a.location.set(loc);
            a.velocity.set(vel);
            a.addInteractionType("tree");
            flock.addBoid(a);
            simulation.addAgent(a);
        }

        return flock;
    }

    private static float random(float min, float max) {
        return AgentFactory.app.random(min, max);
    }
}
