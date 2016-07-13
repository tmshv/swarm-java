package geojson;

import processing.core.PVector;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
class Transform {
    private double a;
    private double b;
    private double c;
    private double d;

    Transform(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    PVector transform(PVector point, double scale) {
        point.x = (float) (scale * (this.a * point.x + this.b));
        point.y = (float) (scale * (this.c * point.y + this.d));
        return point;
    }

    PVector untransform(PVector point, double scale) {
        point.x = (float) ((point.x / scale - this.b) / this.a);
        point.y = (float) ((point.y / scale - this.d) / this.c);
        return point;
    }
}
