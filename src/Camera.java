import geojson.IProjector;
import geojson.LatLon;
import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.max;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Camera {
    LatLon target;
    IProjector projector;
    PVector offset;

    private int zoom = 1;

    Camera(IProjector p) {
        target = new LatLon();
        projector = p;
        offset = new PVector();
    }

    void setOffset(PVector v) {
        offset = v;
    }

    public LatLon getCoordAtScreen(float x, float y) {
        PVector coord = projector.project(target);
        coord.sub(offset);
        coord.x += x;
        coord.y += y;
        return projector.unproject(coord);
    }

    public void moveTarget(float lat, float lon) {
        this.target.lat += lat;
        this.target.lon += lon;
    }

    public void lookAt(LatLon target) {
        this.target.setLatLon(target);
    }

    public void update(PApplet app) {
        app.translate(offset.x, offset.y);
        PVector coord = projector.project(target);
        app.translate(-coord.x, -coord.y);
    }

    public void print() {
        System.out.println("camera target: " + target);
    }

    public int zoomIn() {
        zoom++;
        return zoom;
    }

    public int zoomOut() {
        zoom = max(1, --zoom);
        return zoom;
    }

    public int getZoom() {
        return zoom;
    }
}
