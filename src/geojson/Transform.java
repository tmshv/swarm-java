package geojson;

import processing.core.PVector;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Transform {
    public float a;
    public float b;
    public float c;
    public float d;

    public Transform(float a, float b, float c, float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public PVector transform(PVector point, float scale) {
        point.x = scale * (this.a * point.x + this.b);
        point.y = scale * (this.c * point.y + this.d);
        return point;
    }

    public PVector untransform(PVector point, float scale) {
        point.x = (point.x / scale - this.b) / this.a;
        point.y = (point.y / scale - this.d) / this.c;
        return point;
    }
}
