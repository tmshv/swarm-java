import pathfinder.GraphEdge;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Road extends Path {
    Crossroad cr1;
    Crossroad cr2;

    GraphEdge edge;

    public Road(ArrayList<PVector> coords, Crossroad cr1, Crossroad cr2, GraphEdge edge) {
        super(coords);

        this.cr1 = cr1;
        this.cr2 = cr2;
        this.edge = edge;

        cr1.addRoad(this);
        cr2.addRoad(this);
    }
}
