import processing.core.PVector;

import java.util.HashMap;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Agent implements IInterest {
    PVector location = new PVector();
    protected PVector velocity = new PVector();
    protected PVector acceleration = new PVector();

    public float maxForce;    // Maximum steering force
    public float maxSpeed;    // Maximum speed
    public float interestDistance = 50;
    public float interestThreshold = 0.001f;
    public float interestMultiplier = 0.9f;

    HashMap<IInterest, Float> interestValues;

    Track track;
    int color;
    int mass = 5;

    boolean moving = true;

    public Agent(float maxForce, float maxSpeed, int color) {
        this.maxForce = maxForce;
        this.maxSpeed = maxSpeed;
        this.color = color;
        this.track = new Track(color);
        interestValues = new HashMap<>();
    }

    public float getInteractionPower(Agent other) {
        return 10;
    }

    public void interact(Agent other) {
        float power = getInteractionPower(other);
        if (power != 0) {
            PVector v = getSteeringDirection(other.location);
            v.normalize();
            v.mult(power);
            applyForce(v);
        }
    }

    public void interact(Attractor attractor) {
//        float g = 1;

        PVector v = PVector.sub(location, attractor.getLocation());
        float r2 = v.magSq();
        float force = getInterestValueFor(attractor);
//        float force = g * (mass * agent.mass) / r2;

//        System.out.println(force);

        float dist = PVector.dist(location, attractor.getLocation());
        if (dist < interestDistance) {
            PVector dir = getSteeringDirection(attractor.getLocation());
            dir.normalize();
            dir.mult(force);
            applyForce(dir);
        }
    }

    public void run() {
        update();
        track.write(this.location.copy());
    }

    void seek(PVector target) {
        PVector steer = getSteeringDirection(target);
        applyForce(steer);
    }


    /**
     * A method that calculates and applies a steering force towards a target
     * STEER = DESIRED MINUS VELOCITY
     *
     * @param target
     * @return
     */
    PVector getSteeringDirection(PVector target) {
        PVector desired = PVector.sub(target, location);

        // If the magnitude of desired equals 0, skip out of here
        // (We could optimize this to check if x and y are 0 to avoid mag() square root
        if (desired.mag() == 0) return new PVector();

        // Normalize desired and scale to maximum speed
        desired.normalize();
        desired.mult(maxSpeed);

        // Steering = Desired minus Velocity
        PVector steer = PVector.sub(desired, velocity);
        steer.limit(maxForce);  // Limit to maximum steering force

        return steer;
    }

    /**
     * Method to update location
     */
    public void update() {
        // println("velocity: "+velocity);

        // Update velocity
        velocity.add(acceleration);
        // Limit speed
        velocity.limit(maxSpeed);
        location.add(velocity);
        // Reset accelertion to 0 each cycle
        acceleration.mult(0);
    }

    public void applyForce(PVector force) {
        // We could add mass here if we want A = F / M
        acceleration.add(force);
    }

    @Override
    public float getValue() {
        return mass;
    }

    @Override
    public float getInterestValueFor(IInterest other) {
        float v = other.getValue();
        if (interestValues.containsKey(other)) {
            v = interestValues.get(other);
            v *= interestMultiplier;
            if(v < interestThreshold) v = 0;
        }
        interestValues.put(other, v);
        return v;
    }
}
