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
    private double R = 6378137;
    private Transform transform;

    public double scale = 1;

    public SphericalMercator(double scale) {
        this.scale = scale;
        this.transform = new Transform(1, 0, -1, 0);
    }

    public PVector project(LatLon latlon) {
        float MAX_LATITUDE = 85.0511287798f;

        float d = PI / 180;
        float lat = max(min(MAX_LATITUDE, latlon.lat), -MAX_LATITUDE);
        float sin = sin(lat * d);

        return transform.transform(new PVector(
                (float) this.R * latlon.lon * d,
                (float) this.R * log((1 + sin) / (1 - sin)) / 2
        ), this.scale);
    }

    public LatLon unproject(PVector point) {
        float d = 180 / PI;

        point = transform.untransform(point.copy(), this.scale);
        float lat = (2 * atan(exp(point.y / (float) this.R)) - (PI / 2)) * d;
        float lon = (float) (point.x * d / this.R);
        return new LatLon(lat, lon);
    }

    public PVector[] bounds() {
        float d = (float) (this.R * PI);
        PVector[] b = new PVector[2];
        b[0] = new PVector(-d, -d);
        b[1] = new PVector(d, d);
        return b;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return (float) this.scale;
    }
}
