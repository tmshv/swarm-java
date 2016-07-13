package geojson;

import java.util.ArrayList;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public interface IFeatureCollection {
    public ArrayList<Feature> getFeatures();

    public LatLon[] bounds();
}
