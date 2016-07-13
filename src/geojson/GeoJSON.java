package geojson;

import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public     class GeoJSON implements IFeatureCollection {
    ArrayList<Feature> features = new ArrayList<Feature>();

    public GeoJSON(JSONObject json) {
        this.loadFeatures(
                json.getJSONArray("features")
        );
    }

    public ArrayList<Feature> getFeatures() {
        return this.features;
    }

    private void loadFeatures(JSONArray features) {
        for (int i = 0; i < features.size(); i++) {
            JSONObject f = features.getJSONObject(i);

            this.features.add(new Feature(f));
        }
    }

    public LatLon[] bounds() {
        float min_lat = Float.MAX_VALUE;
        float max_lat = 0;
        float min_lon = Float.MAX_VALUE;
        float max_lon = 0;

        for (Feature f : features) {
            for (LatLon ll : f.geometry.coords) {
                if (min_lat > ll.lat) min_lat = ll.lat;
                if (min_lon > ll.lon) min_lon = ll.lon;
                if (max_lat < ll.lat) max_lat = ll.lat;
                if (max_lon < ll.lon) max_lon = ll.lon;
            }
        }

        LatLon[] b = new LatLon[2];
        b[0] = new LatLon(min_lat, min_lon);
        b[1] = new LatLon(max_lat, max_lon);
        return b;
    }
}