package com.tmshv.agents.core;

import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created at 15/07/16
 *
 * @author tmshv
 */
class Boids extends Agent {
    private ArrayList<Agent> boids;

    float neighborDistanceCohesion = 50f;
    float neighborDistanceAlign = 50f;
    float desiredSeparation = 25.0f;

    float coefCohesion = 2.5f;
    float coefAlign = 1.0f;
    float coefSeparation = 1.0f;

    Boids(float maxSpeed, float maxForce, int color) {
        super("boid", maxSpeed, maxForce, color);
        boids = new ArrayList<>();
    }

    public void run() {
        flock();
//        boids.forEach(com.tmshv.agents.core.Agent::run);
    }

    void addBoid(Agent agent) {
        boids.add(agent);
    }

    int size(){
        return boids.size();
    }

    /**
     * We accumulate a new acceleration each time based on three rules
     *
     */
    void flock() {
        boids.stream().forEach(agent -> {
            PVector sep = separate(agent);
            PVector ali = align(agent);
            PVector coh = cohesion(agent);
            sep.mult(coefSeparation);
            ali.mult(coefAlign);
            coh.mult(coefCohesion);
            agent.applyForce(sep);
            agent.applyForce(ali);
            agent.applyForce(coh);
        });
    }

    /**
     * Alignment
     * For every nearby boid in the system, calculate the average velocity
     *
     * @return
     */
    private PVector align(Agent agent) {
        PVector sum = new PVector(0, 0);
        int count = 0;
        for (Agent other : boids) {
            float d = PVector.dist(agent.getLocation(), other.getLocation());
            if ((d > 0) && (d < neighborDistanceAlign)) {
                sum.add(other.velocity);
                count++;
            }
        }
        if (count > 0) {
            sum.div((float) count);
            sum.normalize();
            sum.mult(maxSpeed);
            PVector steer = PVector.sub(sum, agent.velocity);
            steer.limit(maxForce);
            return steer;
        } else {
            return new PVector(0, 0);
        }
    }

    /**
     * Separation
     * Method checks for nearby boids and steers away
     *
     * @return
     */
    private PVector separate(Agent agent) {
        PVector steer = new PVector(0, 0, 0);
        int count = 0;
        // For every boid in the system, check if it's too close
        for (Agent other : boids) {
            float d = PVector.dist(agent.location, other.location);
            // If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
            if ((d > 0) && (d < desiredSeparation)) {
                // Calculate vector pointing away from neighbor
                PVector diff = PVector.sub(agent.location, other.location);
                diff.normalize();
                diff.div(d);        // Weight by distance
                steer.add(diff);
                count++;            // Keep track of how many
            }
        }
        // Average -- divide by how many
        if (count > 0) {
            steer.div((float) count);
        }

        // As long as the vector is greater than 0
        if (steer.mag() > 0) {
            // First two lines of code below could be condensed with new PVector setMag() method
            // Not using this method until Processing.js catches up
            // steer.setMag(maxspeed);

            // Implement Reynolds: Steering = Desired - Velocity
            steer.normalize();
            steer.mult(maxSpeed);
            steer.sub(agent.velocity);
            steer.limit(maxForce);
        }
        return steer;
    }

    /**
     * Cohesion
     * For the average location (i.e. center) of all nearby boids, calculate steering vector towards that location
     *
     * @return
     */
    private PVector cohesion(Agent agent) {
        PVector sum = new PVector(0, 0);   // Start with empty vector to accumulate all locations
        int count = 0;
        for (Agent other : boids) {
            float d = PVector.dist(agent.location, other.location);
            if ((d > 0) && (d < neighborDistanceCohesion)) {
                sum.add(other.location); // Add location
                count++;
            }
        }
        if (count > 0) {
            sum.div(count);
            return getSteeringDirection(sum);  // Steer towards the location
        } else {
            return new PVector(0, 0);
        }
    }
}
