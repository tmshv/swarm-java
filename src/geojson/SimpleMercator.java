package geojson;

import processing.core.PVector;

/**
 * Created at 14/07/16
 * EPSG:4326 CRS
 *
 * @author tmshv
 */
class SimpleMercator implements IProjector {
    Transform t;

    public SimpleMercator() {
        t = new Transform(1 / 180, 1, -1 / 180, 0.5f);
    }

    public PVector project(LatLon latlon) {
        return t.transform(new PVector(latlon.lon, latlon.lat), 1);
    }

    public LatLon unproject(PVector point) {
        return new LatLon(point.y, point.x);
    }

    public PVector[] bounds() {
        return null;
    }

    public void setScale(float scale) {

    }

    public float getScale() {
        return 1;
    }
}