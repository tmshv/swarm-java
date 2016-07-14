import processing.core.PVector;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Agent {
    PVector location = new PVector();
    PVector velocity = new PVector();
    PVector acceleration = new PVector();

    public float maxForce;    // Maximum steering force
    public float maxSpeed;    // Maximum speed

    Track track;
    int color;
    int size = 5;

    boolean moving = true;

    public Agent(float maxForce, float maxSpeed, int color) {
        this.maxForce = maxForce;
        this.maxSpeed = maxSpeed;
        this.color = color;
        this.track = new Track(color);
    }

    public void run() {
        update();
        track.write(this.location.copy());
    }

    // A method that calculates and applies a steering force towards a target
    // STEER = DESIRED MINUS VELOCITY
    public void seek(PVector target) {
        PVector desired = PVector.sub(target, location);

        // If the magnitude of desired equals 0, skip out of here
        // (We could optimize this to check if x and y are 0 to avoid mag() square root
        if (desired.mag() == 0) return;

        // Normalize desired and scale to maximum speed
        desired.normalize();
        desired.mult(maxSpeed);

        // Steering = Desired minus Velocity
        PVector steer = PVector.sub(desired, velocity);
        steer.limit(maxForce);  // Limit to maximum steering force

        applyForce(steer);
    }

    // Method to update location
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
}