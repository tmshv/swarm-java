import geojson.IProjector;
import geojson.LatLon;
import processing.core.PVector;

/**
 * Created at 16/07/16
 *
 * @author tmshv
 */
public class EmitterFactory {
    public static IProjector projector;
    public static Simulation simulation;

    public static void init(IProjector projector, Simulation simulation) {
        EmitterFactory.projector = projector;
        EmitterFactory.simulation = simulation;
    }

    public static void createBoids(LatLon loc, String type, int i) {
        PVector v = projector.project(loc);
        Emitter e = new Emitter(type, v, i) {
            @Override
            void update() {
                AgentFactory.createBoids(new LatLon(55.746178f, 37.615578f), "bird", 30);
            }
        };
        simulation.addEmitter(e);
    }
}
