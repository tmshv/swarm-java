package geojson;

import java.util.ArrayList;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class FeatureExploder implements IFeatureCollection {
    ArrayList<Feature> featuresList;
    IFeatureCollection source;

    public FeatureExploder(IFeatureCollection fc) {
        this.source = fc;

        ArrayList<Feature> sourceFeatures = fc.getFeatures();
        ArrayList<Feature> explodedFeatures = new ArrayList<Feature>();
        for (Feature f : sourceFeatures) {
            explodedFeatures.addAll(explode(f));
        }

        featuresList = explodedFeatures;
    }

    public ArrayList<Feature> getFeatures() {
        return featuresList;
    }

    public LatLon[] bounds() {
        return this.source.bounds();
    }

    private ArrayList<Feature> explode(Feature feature) {
        ArrayList<Feature> result = new ArrayList<Feature>();
        Geometry source = feature.geometry;

        int i = 0;
        while (true) {
            ArrayList<LatLon> coords = new ArrayList<LatLon>();
            coords.add(source.coords.get(i));
            coords.add(source.coords.get(i + 1));
            Geometry g = new Geometry();
            g.coords = coords;
            Feature f = new Feature(g);
            result.add(f);

            i++;
            if (i >= source.coords.size() - 1) break;
        }

        return result;
    }
}

