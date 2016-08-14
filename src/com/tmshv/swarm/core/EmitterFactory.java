package com.tmshv.swarm.core;

import geojson.IProjector;
import geojson.LatLon;
import processing.core.PVector;

/**
 * Created at 16/07/16
 *
 * @author tmshv
 */
public class EmitterFactory {
    public static IProjector projector;
    public static Simulation simulation;

    public static void init(IProjector projector, Simulation simulation) {
        EmitterFactory.projector = projector;
        EmitterFactory.simulation = simulation;
    }

    public static void createBoids(LatLon loc, String type, int count, int i, int delay) {
        PVector v = projector.project(loc);
        Emitter e = new Emitter(type, v, i, delay) {
            @Override
            public void update() {
                AgentFactory.createBoids(loc, "bird", count);
            }
        };
        simulation.addEmitter(e);
//        e.update();
    }

    public static void createPedestrian(LatLon loc, int i) {
        PVector v = projector.project(loc);
        Emitter e = new Emitter("pedestrian", v, i) {
            @Override
            public void update() {
                Attractor target = simulation.getRandomAttractor(AgentFactory.pedestrianAttractors);
                if (target != null) {
                    LatLon t = projector.unproject(target.getLocation());
                    AgentFactory.createPedestrian(loc, t);
                }
            }
        };
        simulation.addEmitter(e);
    }

    public static void createRunner(LatLon loc, int i) {
        PVector v = projector.project(loc);
        Emitter e = new Emitter("runner", v, i) {
            @Override
            public void update() {
                Attractor target = simulation.getRandomAttractor(AgentFactory.defaultAttractors);
                if (target != null) {
                    LatLon t = projector.unproject(target.getLocation());
                    AgentFactory.createRunner(loc, t);
                }
            }
        };
        simulation.addEmitter(e);
    }

    public static void createBike(LatLon loc, int i) {
        PVector v = projector.project(loc);
        Emitter e = new Emitter("bike", v, i) {
            @Override
            public void update() {
                Attractor target = simulation.getRandomAttractor(AgentFactory.defaultAttractors);
                if (target != null) {
                    LatLon t = projector.unproject(target.getLocation());
                    AgentFactory.createBike(loc, t);
                }
            }
        };
        simulation.addEmitter(e);
    }

    public static void createTransport(LatLon loc, int i) {
        PVector v = projector.project(loc);
        Emitter e = new Emitter("transport", v, i) {
            @Override
            public void update() {
                Attractor target = simulation.getRandomAttractor(AgentFactory.defaultAttractors);
                if (target != null) {
                    LatLon t = projector.unproject(target.getLocation());
                    AgentFactory.createTransport(loc, t);
                }
            }
        };
        simulation.addEmitter(e);
    }
}
