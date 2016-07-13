package geojson;

import processing.core.PVector;

import static java.lang.Math.max;
import static processing.core.PApplet.*;
import static processing.core.PConstants.PI;


/**
 * Created at 14/07/16
 * EPSG:3857 CRS
 *
 * @author tmshv
 */

public class SphericalMercator implements IProjector {
    private float R = 6378137;
    private Transform t;

    public float scale = 1;

    public SphericalMercator() {
        //float s = 0.5 / (PI * this.R);
        //this.t = new Transform(s, 0.5, -s, 0.5);
        this.t = new Transform(1, 0, -1, 0);
    }

    public PVector project(LatLon latlon) {
        float MAX_LATITUDE = 85.0511287798f;

        float d = PI / 180;
        float lat = max(min(MAX_LATITUDE, latlon.lat), -MAX_LATITUDE);
        float sin = sin(lat * d);

        return t.transform(new PVector(
                this.R * latlon.lon * d,
                this.R * log((1 + sin) / (1 - sin)) / 2
        ), this.scale);
    }

    public LatLon unproject(PVector point) {
        float d = 180 / PI;

        point = t.untransform(point.copy(), this.scale);
        return new LatLon(
                (2 * atan(exp(point.y / this.R)) - (PI / 2)) * d,
                point.x * d / this.R
        );
    }

    public PVector[] bounds() {
        float d = this.R * PI;
        PVector[] b = new PVector[2];
        b[0] = new PVector(-d, -d);
        b[1] = new PVector(d, d);
        return b;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return this.scale;
    }
}
