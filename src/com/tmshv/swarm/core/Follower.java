package com.tmshv.swarm.core;

import com.tmshv.swarm.utils.GeometryUtils;
import processing.core.PVector;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Follower extends Agent {
    public float goalDistance = 20;

    PVector finishPoint;

    public PVector predictLocation = new PVector();
    public PVector normalLocation = new PVector();
    public PVector targetLocation = new PVector();

    public double predictMult = 50;
    double dirMult = 5;

    public ArrayList<PVector> route;

    public Follower(String type, float speed, float force, int c) {
        super(type, speed, force, c);
    }

    @Override
    public void run() {
//        if (finishPoint != null && (location.dist(finishPoint) < goalDistance)) {
//            finishPoint = null;
//        } else {
//            follow();
//        }
        follow();
        super.run();
    }

    public void move(Route route) {
        this.move(route.bake());
    }

    public void move(ArrayList<PVector> route) {
        this.route = route;
        if (this.route.size() > 0) {
//            finishPoint = this.route.get(this.route.size() - 1);
//            this.location.set(this.route.get(0));
        }
    }

    // This function implements Craig Reynolds' path following algorithm
    // http://www.red3d.com/cwr/steer/PathFollow.html
    public void follow() {
        if (route == null) return;

        float roadRadius = 20;

        PVector predict = velocity.copy();
        predict.normalize();
        predict.mult((float) predictMult);
        predictLocation = PVector.add(location, predict);

        // Now we must find the normal to the path from the predicted location
        // We look at the normal for each line segment and pick out the closest one

        double minDistance = Double.MAX_VALUE;

        PVector currentNormal = new PVector();

        // Loop through all points of the path
        for (int i = 0; i < route.size() - 1; i++) {
            // Look at a line segment
            PVector a = route.get(i);
            PVector b = route.get(i + 1);

            // Get the normal point to that line
//            currentNormal = getNormalPoint(predictLocation, a, b);
            currentNormal = GeometryUtils.projectVertexOnLine(predictLocation, a, b);

            if (!GeometryUtils.isInsideLineSegment(currentNormal, a, b)) {
                currentNormal = GeometryUtils.nearestTo(currentNormal, new PVector[]{a, b});
//                currentNormal = b.copy();
            }

            // Check if normal is on line segment

            // If it's not within the line segment, consider the normal to just be the end of the line segment (point b)
            //if (da + db > line.mag()+1) {
//            if (currentNormal.x < min(a.x, b.x) || currentNormal.x > max(a.x, b.x) || currentNormal.y < min(a.y, b.y) || currentNormal.y > max(a.y, b.y)) {
//                currentNormal = b.copy();
                // If we're at the end we really want the next line segment for looking ahead
                // a = p.points.get((i+1)%p.points.mass());
                // b = p.points.get((i+2)%p.points.mass());  // com.tmshv.swarm.core.Path wraps around
                // dir = PVector.sub(b, a);
//            }

            double distance = predictLocation.dist(currentNormal);
            if (distance < minDistance) {
                minDistance = distance;
                normalLocation = currentNormal;

                // Look at the direction of the line segment so we can seek a little bit ahead of the normal
                PVector dir = PVector.sub(b, a);
                dir.normalize();
                dir.mult((float) dirMult);

                // This is an oversimplification
                // Should be based on distance to path & velocity
                targetLocation = normalLocation.copy();
//                targetLocation.add(dir);
            }
        }

        // Only if the distance is greater than the path's radius do we bother to steer
        if (minDistance > roadRadius) {
            seek(targetLocation);
        }

//        seek(targetLocation);
    }
}
