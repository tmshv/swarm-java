package geojson;

import processing.data.JSONObject;

import java.util.ArrayList;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Feature {
    public Geometry geometry;
    public String type;

    //private JSONObject props;

    public Feature(Geometry geometry) {
        this.type = "Feature";
        this.geometry = geometry;
    }

    public Feature(JSONObject feature) {
        this(new Geometry(feature.getJSONObject("geometry")));
        this.type = feature.getString("type");
        //this.props = feature.getJSONObject("properties");
    }
}