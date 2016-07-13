package geojson;

import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Geometry {
    public String type;

    public ArrayList<LatLon> coords;

    public Geometry(ArrayList<LatLon> coords) {
        this.coords = coords;
        ;
        this.type = "Polygon";
    }

    public Geometry(JSONObject geometry) {
        this(new ArrayList<LatLon>());
        this.type = geometry.getString("type");

        JSONArray geo_coords = geometry.getJSONArray("coordinates");
        for (int i = 0; i < geo_coords.size(); i++) {
            JSONArray g = geo_coords.getJSONArray(i);
            float lat = g.getFloat(1);
            float lon = g.getFloat(0);
            this.coords.add(new LatLon(lat, lon));
        }
    }

    public ArrayList<LatLon> getInnerCoords() {
        ArrayList<LatLon> c = new ArrayList<LatLon>();
        for (int i = 1; i < coords.size() - 1; i++) {
            c.add(coords.get(i));
        }
        return c;
    }

    public LatLon first() {
        return this.coords.get(0);
    }

    public LatLon last() {
        return this.coords.get(this.coords.size() - 1);
    }

    public Geometry clone() {
        ArrayList<LatLon> cs = new ArrayList<LatLon>();
        for (LatLon c : coords) {
            cs.add(c.clone());
        }
        return new Geometry(cs);
    }
}