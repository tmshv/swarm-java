import processing.core.PVector;
import utils.ColorUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Agent implements IInterest, IAgent {
    private static int defaultLifetime = 1000;
    private static int defaultTrackAlpha = 25;

    protected PVector location = new PVector();
    protected PVector velocity = new PVector();
    protected PVector acceleration = new PVector();

    public float maxForce;    // Maximum steering force
    public float maxSpeed;    // Maximum speed

    private float interestDistance = 50;
    public float interestThreshold = 0.001f;
    public float interestMultiplier = 0.9f;

    private int color;

    private float mass = 5;

    private HashMap<IInterest, Float> interestValues;

    private Track track;
    private ArrayList<String> interactTypes;
    private String type;
    private boolean moving;
    private int lifetime;
    private ArrayList<Attractor> currentAttractors;

    public Agent(String type, float maxForce, float maxSpeed, int color) {
        int trackColor = ColorUtil.setAlpha(color, defaultTrackAlpha);

        this.type = type;
        this.maxForce = maxForce;
        this.maxSpeed = maxSpeed;
        this.color = color;
        this.track = new Track(trackColor);
        interestValues = new HashMap<>();
        interactTypes = new ArrayList<>();
        lifetime = Agent.defaultLifetime;
        moving = true;
        currentAttractors = new ArrayList<>();
    }

    public void run() {
        if (moving) {
            update();
            track.write(this.location.copy());

            lifetime--;
            if (lifetime == 0) {
                moving = false;
            }
        }

        if (currentAttractors.size() > 0) currentAttractors = new ArrayList<>();
    }

    @Override
    public float getMass() {
        return mass;
    }

    @Override
    public void setMass(float value) {
        mass = value;
    }

    @Override
    public ArrayList<Attractor> getCurrentAttractors() {
        return currentAttractors;
    }

    @Override
    public float getInterestDistance() {
        return interestDistance;
    }

    @Override
    public void setInterestDistance(float value) {
        interestDistance = value;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public Track getTrack() {
        return track;
    }

    @Override
    public PVector getLocation() {
        return this.location;
    }

    @Override
    public int getLifetime() {
        return lifetime;
    }

    @Override
    public void setLifetime(int value) {
        lifetime = value;
    }

    public void interact(IAgent other) {
        if (!interactTypes.contains(other.getType())) return;

        IInterest ii = (IInterest) other;
        if(ii == null) return;

        float power = getInterestValueFor(ii);
        if (power != 0) {
            PVector v = getSteeringDirection(other.getLocation());
            v.normalize();
            v.mult(power);
            applyForce(v);
        }
    }

    public void interact(Attractor attractor) {
        if (!interactTypes.contains(attractor.getType())) return;

        float dist = PVector.dist(location, attractor.getLocation());
        if (dist < interestDistance) {
            float force = getInterestValueFor(attractor);

            PVector dir = getSteeringDirection(attractor.getLocation());
            dir.normalize();
            dir.mult(force);
            applyForce(dir);

            currentAttractors.add(attractor);
        }
    }

    @Override
    public boolean isMoving() {
        return this.moving;
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
        if (target == null) return new PVector();

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

    public String getType() {
        return type;
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
            if (v < interestThreshold) v = 0;
        }
        interestValues.put(other, v);
        return v;
    }

    public void addInteractionType(String type) {
        interactTypes.add(type);
    }
}
