package geojson;

import processing.core.PVector;

import static java.lang.Math.PI;
import static java.lang.Math.tan;
import static processing.core.PApplet.*;

/**
 * Created at 14/07/16
 * EPSG:3395 CRS
 *
 * @author tmshv
 */

class Mercator implements IProjector {
    float R = 6378137;
    float R_MINOR = 6356752.314245179f;

    public PVector project(LatLon latlon) {
        float d = (float) (PI / 180);
        float r = this.R;
        float y = latlon.lat * d;
        float tmp = this.R_MINOR / r;
        float e = sqrt(1 - tmp * tmp);
        float con = e * sin(y);

        float ts = (float) (tan(PI / 4 - y / 2) / pow((1 - con) / (1 + con), e / 2));
        float y2 = -r * log(max(ts, (float) 1e-10f));

        return new PVector(latlon.lon * d * r, y2);
    }

    public LatLon unproject(PVector point) {
        //var d = 180 / Math.PI,
        //    r = this.R,
        //    tmp = this.R_MINOR / r,
        //    e = Math.sqrt(1 - tmp * tmp),
        //    ts = Math.exp(-point.y / r),
        //    phi = Math.PI / 2 - 2 * Math.atan(ts);

        //for (var i = 0, dphi = 0.1, con; i < 15 && Math.abs(dphi) > 1e-7; i++) {
        //  con = e * Math.sin(phi);
        //  con = Math.pow((1 - con) / (1 + con), e / 2);
        //  dphi = Math.PI / 2 - 2 * Math.atan(ts * con) - phi;
        //  phi += dphi;
        //}

        //return new L.LatLng(phi * d, point.x * d / r);
        //float d = 180 / PI;

        //return new LatLon(
        //  (2 * atan(exp(point.y / this.R)) - (PI / 2)) * d,
        //  point.x * d / this.R);
        //}

        return new LatLon();
    }

    public PVector[] bounds() {
        return null;
    }

    public void setScale(float scale) {

    }

    public float getScale() {
        return 0;
    }
}