package com.tmshv.agents.core;

import geojson.IProjector;
import geojson.LatLon;
import processing.core.PApplet;
import processing.core.PVector;
import com.tmshv.agents.utils.ColorUtil;

import java.util.Arrays;

import static java.lang.Math.PI;

/**
 * Created at 15/07/16
 *
 * @author tmshv
 */
public class AgentFactory {
    private static PApplet app;
    private static IProjector projector;
    private static Simulation simulation;
    private static Navigator navigator;

    static String[] defaultAttractors = new String[]{};
//    static String[] pedestrianAttractors = new String[]{"tweet", "pedestrian"};
    static String[] pedestrianAttractors = new String[]{};
    static String[] runnerAttractors = new String[]{"tweet", "tree"};
    static String[] bikeAttractors = new String[]{"tweet"};

    public static void init(PApplet app, IProjector projector, Simulation simulation, Navigator navigator) {
        AgentFactory.app = app;
        AgentFactory.projector = projector;
        AgentFactory.simulation = simulation;
        AgentFactory.navigator = navigator;
    }

    public static RandomWalker createRandomWalker(LatLon loc, String type, float maxSpeed, float maxForce, float mass, int color) {
        RandomWalker a = new RandomWalker(type, maxSpeed, maxForce, color);
        a.location.set(projector.project(loc));
        a.setMass(mass);
        simulation.addAgent(a);
        return a;
    }

    public static Agent createFlyAgent(LatLon latLon) {
        float maxSpeed = random(1, 10);
        float maxForce = random(1, 4);

        Agent a = createRandomWalker(latLon, "fly", maxSpeed, maxForce, 1f, 0xFFCCCCCC);
        a.setLifetime(600);
        a.addInteractionType("tree");
        return a;
    }

    public static Agent createPedestrian(LatLon loc, LatLon target) {
        navigator.setLayer("people");
        Route route = navigator.navigate(loc, target);
        if (route == null) return null;

        float maxSpeed = random(.001f, .002f);
        float maxForce = 1;
        float mass = random(2, 5);

        Agent a;
        if(random(0, 1) < .1){
            a = createRandomWalker(loc, "pedestrian", maxSpeed, maxForce, mass, 0xffff0000);
        }else{
            Follower f = createFollower("pedestrian", maxSpeed, maxForce, mass, 0xffff0000);
            f.move(route);
            a = f;
        }
        a.setLifetime(1000);
        Arrays.stream(pedestrianAttractors).forEach(a::addInteractionType);
        return a;
    }

    public static Agent createRunner(LatLon loc, LatLon target) {
        navigator.setLayer("people");
        Route route = navigator.navigate(loc, target);
        if (route == null) return null;

        float maxSpeed = random(.002f, .003f);
        float maxForce = 1;//random(.001f, .002f);
        float mass = random(2, 5);

        Follower a = createFollower("runner", maxSpeed, maxForce, mass, 0xff0043ff);
        a.setLifetime(1000);
        a.setInterestDistance(random(130, 150));
        Arrays.stream(runnerAttractors).forEach(a::addInteractionType);
        a.move(route);
        return a;
    }

    public static Agent createBike(LatLon loc, LatLon target) {
        navigator.setLayer("transport");
        Route route = navigator.navigate(loc, target);
        if (route == null) return null;

        float maxSpeed = random(.003f, .006f);
        float maxForce = 1;
        float mass = random(2, 5);

        Follower a = createFollower("bike", maxSpeed, maxForce, mass, 0xffdd00ff);
        a.setLifetime(1000);
        a.setInterestDistance(random(140, 170));
        Arrays.stream(bikeAttractors).forEach(a::addInteractionType);
        a.move(route);
        return a;
    }

    public static Agent createTransport(LatLon loc, LatLon target) {
        navigator.setLayer("transport");
        Route route = navigator.navigate(loc, target);
        if (route == null) return null;

        float maxSpeed = random(.01f, .02f);
        float maxForce = 1;
        float mass = random(2, 5);

        Follower a = createFollower("transport", maxSpeed, maxForce, mass, 0xffffe655);
        a.setInterestDistance(random(150, 350));
        a.setLifetime(500);
        a.move(route);
        return a;
    }

    public static Follower createFollower(String type, float maxSpeed, float maxForce, float mass, int color) {
        float predictMult = random(10, 50);
        float dirMult = random(2, 10);

        Follower v = new Follower(type, maxSpeed, maxForce, color);
        v.setMass(mass);
        v.predictMult = predictMult;
        v.dirMult = dirMult;
        simulation.addAgent(v);

        return v;
    }

    public static Boids createBoids(LatLon latLon, String type, int num) {
        int c = 0xff11aa00;

        PVector loc = projector.project(latLon);
        float maxSpeed = random(1, 10);
        float maxForce = random(1, 4);

        Boids flock = new Boids(maxSpeed, maxForce, c);
        for (int i = 0; i < num; i++) {
            maxSpeed = random(.1f, 1f);
            maxForce = 1;
            float mass = random(4, 20);

            float angle = random(0, (float) (PI * 2));
            PVector vel = PVector.fromAngle(angle);

            Agent a = new Agent(type, maxSpeed, maxForce, c);
            a.getTrack().color = ColorUtil.setAlpha(c, 40);
            a.setLifetime(400);
            a.setMass(mass);
            a.interestMultiplier = 0.975f;
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
