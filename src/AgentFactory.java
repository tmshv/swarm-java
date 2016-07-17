import geojson.IProjector;
import geojson.LatLon;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.Arrays;

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

    static String[] defaultAttractors = new String[]{};
    static String[] pedestrianAttractors = new String[]{"tweet"};
    static String[] runnerAttractors = new String[]{"tweet", "tree"};

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

    static Agent createPedestrian(LatLon loc, LatLon target) {
        navigator.setLayer("people");
        Route route = navigator.navigate(loc, target);
        if (route == null) return null;

        float maxSpeed = random(.001f, .002f);
        float maxForce = 1;
        float mass = random(2, 5);

        Follower a = createFollower("pedestrian", maxSpeed, maxForce, mass, 0xffff0000);
        a.setLifetime(300);
        Arrays.stream(pedestrianAttractors).forEach(a::addInteractionType);
        a.move(route);
        a.color = 0xFFFF0000;
        return a;
    }

    static Agent createRunner(LatLon loc, LatLon target) {
        navigator.setLayer("people");
        Route route = navigator.navigate(loc, target);
        if (route == null) return null;

        float maxSpeed = random(.002f, .003f);
        float maxForce = 1;//random(.001f, .002f);
        float mass = random(2, 5);

        Follower a = createFollower("runner", maxSpeed, maxForce, mass, 0xff00ff00);
        a.setLifetime(400);
        Arrays.stream(runnerAttractors).forEach(a::addInteractionType);
        a.move(route);
        return a;
    }

    static Agent createBike(LatLon loc, LatLon target) {
        navigator.setLayer("transport");
        Route route = navigator.navigate(loc, target);
        if (route == null) return null;

        float maxSpeed = random(.003f, .006f);
        float maxForce = 1;
        float mass = random(2, 5);

        Follower a = createFollower("bike", maxSpeed, maxForce, mass, 0xffffff00);
        a.setLifetime(300);
        a.addInteractionType("tweet");
        a.move(route);
        return a;
    }

    static Agent createTransport(LatLon loc, LatLon target) {
        navigator.setLayer("transport");
        Route route = navigator.navigate(loc, target);
        if (route == null) return null;

        float maxSpeed = random(.01f, .02f);
        float maxForce = 1;
        float mass = random(2, 5);

        Follower a = createFollower("transport", maxSpeed, maxForce, mass, 0xff00ffff);
        a.setLifetime(500);
        a.move(route);
        return a;
    }

    static Follower createFollower(String type, float maxSpeed, float maxForce, float mass, int color) {
        float predictMult = random(10, 50);
        float dirMult = random(2, 10);

        Follower v = new Follower(type, maxSpeed, maxForce, color);
        v.mass = mass;
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
        for (int i = 0; i < num; i++) {
            float angle = random(0, (float) (PI * 2));
            PVector vel = PVector.fromAngle(angle);

            Agent a = new Agent(type, maxSpeed, maxForce, c);
            a.setLifetime(400);
            a.mass = 2;
            a.location.set(loc);
            a.velocity.set(vel);
            a.addInteractionType("tree");
            boolean addingStatus = simulation.addAgent(a);
            if (addingStatus) flock.addBoid(a);
        }

        if (flock.size() > 0) simulation.addAgent(flock);

        return flock;
    }

    private static float random(float min, float max) {
        return AgentFactory.app.random(min, max);
    }
}
