import processing.core.PVector;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Vehicle {
    float r;
    public float maxForce;    // Maximum steering force
    public float maxSpeed;    // Maximum speed

    boolean moving = true;

    public float goalDistance = 5;

    PVector finishPoint;

    public PVector location = new PVector();
    public PVector velocity = new PVector();
    public PVector acceleration = new PVector();

    public PVector predictLocation = new PVector();
    public PVector normalLocation = new PVector();
    public PVector targetLocation = new PVector();

    float predictMult = 50;
    float dirMult = 5;

    private ArrayList<PVector> route;

    int paint;
    int size = 5;
    Track track;

    public Vehicle(float speed, float force, int c) {
        this.maxSpeed = speed;
        this.maxForce = force;

        paint = c;
        track = new Track(c);
    }

    public void run() {
        if (finishPoint != null && (location.dist(finishPoint) < goalDistance)) {
            finishPoint = null;
        } else {
            follow();
            update();
            track.write(this.location.copy());
        }
    }

    public void move(Route route) {
        this.route = route.bake();
        finishPoint = this.route.get(this.route.size() - 1);
        this.location.set(this.route.get(0));
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

    // This function implements Craig Reynolds' path following algorithm
    // http://www.red3d.com/cwr/steer/PathFollow.html
    public void follow() {
        float roadRadius = 5;

        // Predict location 50 (arbitrary choice) frames ahead
        // This could be based on speed
        PVector predict = velocity.copy();
        predict.normalize();
        predict.mult(predictMult);
        predictLocation = PVector.add(location, predict);

        // Now we must find the normal to the path from the predicted location
        // We look at the normal for each line segment and pick out the closest one

        PVector normal = null;
        PVector target = null;
        float worldRecord = 10000000;  // Start with a very high record distance that can easily be beaten

        PVector normalPoint = new PVector();

        // Loop through all points of the path
        for (int i = 0; i < route.size() - 1; i++) {
            // Look at a line segment
            PVector a = route.get(i);
            PVector b = route.get(i + 1);

            // Get the normal point to that line
            normalPoint = getNormalPoint(predictLocation, a, b);

            // Check if normal is on line segment
            PVector dir = PVector.sub(b, a);

            // If it's not within the line segment, consider the normal to just be the end of the line segment (point b)
            //if (da + db > line.mag()+1) {
            if (normalPoint.x < min(a.x, b.x) || normalPoint.x > max(a.x, b.x) || normalPoint.y < min(a.y, b.y) || normalPoint.y > max(a.y, b.y)) {
                normalPoint = b.copy();
                // If we're at the end we really want the next line segment for looking ahead
                // a = p.points.get((i+1)%p.points.size());
                // b = p.points.get((i+2)%p.points.size());  // Path wraps around
                // dir = PVector.sub(b, a);
            }

            // How far away are we from the path?
            float distance = predictLocation.dist(normalPoint);

            // Did we beat the record and find the closest line segment?
            if (distance < worldRecord) {
                worldRecord = distance;
                // If so the target we want to steer towards is the normal
                normal = normalPoint;

                // Look at the direction of the line segment so we can seek a little bit ahead of the normal
                // PVector dir = PVector.sub(b, a);
                dir.normalize();
                // This is an oversimplification
                // Should be based on distance to path & velocity
                dir.mult(dirMult);
                target = normal.copy();
                target.add(dir);
            }
        }

        normalLocation = normalPoint;
        targetLocation = target;

        // println("var: "+var);

        // Only if the distance is greater than the path's radius do we bother to steer
        // if (worldRecord > roadRadius) {
        seek(target);
        // }
    }

    // A function to get the normal point from a point (p) to a line segment (a-b)
    // This function could be optimized to make fewer new Vector objects
    public PVector getNormalPoint(PVector p, PVector a, PVector b) {
        // Vector from a to p
        PVector ap = PVector.sub(p, a);
        // Vector from a to b
        PVector ab = PVector.sub(b, a);
        ab.normalize(); // Normalize the line
        // Project vector "diff" onto line by using the dot product
        ab.mult(ap.dot(ab));
        PVector normalPoint = PVector.add(a, ab);
        return normalPoint;
    }
}
