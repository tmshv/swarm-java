package geojson;

import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Geometry {
    public String type;

    public ArrayList<LatLon> coords;

    Geometry() {

    }

    public Geometry(JSONObject geometry) {
        this.type = geometry.getString("type");
        this.coords = new ArrayList<LatLon>();

        switch (this.type){
            case "LineString":
                this.loadLineString(geometry);
                break;

            case "Point":
                this.loadPoint(geometry);
                break;
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
        Geometry g = new Geometry();
        g.type = this.type;
        g.coords = coords.stream().map(LatLon::clone).collect(Collectors.toCollection(ArrayList::new));
        return g;
    }

    private void loadLineString(JSONObject geometry){
        JSONArray cs = geometry.getJSONArray("coordinates");
        for (int i = 0; i < cs.size(); i++) {
            JSONArray g = cs.getJSONArray(i);
            float lat = g.getFloat(1);
            float lon = g.getFloat(0);
            this.coords.add(new LatLon(lat, lon));
        }
    }

    private void loadPoint(JSONObject geometry){
        JSONArray cs = geometry.getJSONArray("coordinates");
        float lat = cs.getFloat(1);
        float lon = cs.getFloat(0);
        this.coords.add(new LatLon(lat, lon));
    }
}
