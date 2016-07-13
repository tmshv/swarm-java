package geojson;

import processing.core.PVector;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public     interface IProjector {
    public PVector project(LatLon latlon);

    public LatLon unproject(PVector point);

    public PVector[] bounds();

    public void setScale(float scale);

    public float getScale();
}
