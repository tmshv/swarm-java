package geojson;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class FeatureOptimizer implements IFeatureCollection {
    ArrayList<Feature> featuresList;
    IFeatureCollection source;

    public FeatureOptimizer(IFeatureCollection fc) {
        this.source = fc;

        ArrayList<Feature> sourceFeatures = fc.getFeatures();
        featuresList = optimize(sourceFeatures);
    }

    public ArrayList<Feature> getFeatures() {
        return featuresList;
    }

    public LatLon[] bounds() {
        return this.source.bounds();
    }

    private ArrayList<Feature> optimize(ArrayList<Feature> features) {
        //Create an index data
        HashMap<PVector, Feature[]> data = new HashMap<PVector, Feature[]>();
        for (Feature f : features) {
            PVector first = f.geometry.first().toPVector();
            PVector last = f.geometry.last().toPVector();
            insertVertex(data, first, f);
            insertVertex(data, last, f);
        }

        PVector anchor = getJoinableCrossroad(data);
        if (anchor == null) {
            //Done if no joinable crossroads
            return features;
        }

        //Join single crossroad
        Feature[] joiningFeatures = (Feature[]) data.get(anchor);
        Feature joint = joinCrossroad(anchor, joiningFeatures);

        //Assembly new features list
        ArrayList<Feature> result = new ArrayList<Feature>();
        for (Feature f : features) {
            if (!has(joiningFeatures, f)) {
                result.add(f);
            }
        }
        result.add(joint);

        //Repeat again
        return optimize(result);

        //Create two sets: skipping features & joining features
        // ArrayList<Feature> skipList = new ArrayList<Feature>();
        // ArrayList<Feature> joinList = new ArrayList<Feature>();
        // ArrayList<PVector> joinAnchors = new ArrayList<PVector>();

        //Add to join list all features of crossroad who contains two items
        // for (Map.Entry crossroad : data.entrySet()){
        // 	PVector v = (PVector) crossroad.getKey();
        // 	Feature[] fs = (Feature[]) crossroad.getValue();
        // 	if(fs.length == 2){
        // 		for(Feature f : fs){
        // 			if(!joinList.contains(f)){
        // 				joinList.add(f);
        // 				joinAnchors.add(v);
        // 			}
        // 		}
        // 	}
        // }

        //Add to skip list another features
        // for(Feature f : features){
        // 	if(!joinList.contains(f)) skipList.add(f);
        // }

        //Baking
        // ArrayList<Feature> result = new ArrayList<Feature>();
        // result.addAll(skipList);
        // result.addAll(join(joinAnchors));
        // return result;
    }

    private boolean has(Feature[] fs, Feature target) {
        for (Feature f : fs) if (f == target) return true;
        return false;
    }

    private PVector getJoinableCrossroad(HashMap<PVector, Feature[]> data) {
        for (Map.Entry crossroad : data.entrySet()) {
            PVector v = (PVector) crossroad.getKey();
            Feature[] fs = (Feature[]) crossroad.getValue();
            if (fs.length == 2) return v;
        }
        return null;
    }

    private void insertVertex(HashMap<PVector, Feature[]> data, PVector v, Feature feature) {
        Feature[] fs = data.get(v);
        if (fs != null) fs = appendFeature(fs, feature);
        else {
            fs = new Feature[1];
            fs[0] = feature;
        }
        data.put(v, fs);
    }

    private Feature[] appendFeature(Feature[] features, Feature f) {
        int last = features.length;
        Feature[] nf = new Feature[last + 1];
        for (int i = 0; i < features.length; i++) nf[i] = features[i];
        nf[last] = f;
        return nf;
    }

    private Feature joinCrossroad(PVector anchor, Feature[] features) {
        LatLon cr = new LatLon(anchor.x, anchor.y);

        Feature f1;
        Feature f2;

        //sort
        //last coord of f1 should be the first coord of f2
        if (cr.isEqual(features[0].geometry.last())) {
            f1 = features[0];
            f2 = features[1];
        } else {
            f2 = features[0];
            f1 = features[1];
        }

        //assembly
        ArrayList<LatLon> newPath = new ArrayList<LatLon>();
        newPath.add(f1.geometry.first());
        newPath.addAll(f1.geometry.getInnerCoords());
        newPath.add(f2.geometry.first());
        newPath.addAll(f2.geometry.getInnerCoords());
        newPath.add(f2.geometry.last());

        Geometry g = new Geometry();
        g.type = "LineString";
        g.coords = newPath;
        return new Feature(g);
    }
}
