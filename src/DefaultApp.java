import com.tmshv.swarm.core.Agent;
import com.tmshv.swarm.core.AgentFactory;
import geojson.GeoJSON;
import geojson.LatLon;
import processing.core.PApplet;

/**
 * Created at 22/07/16
 *
 * @author tmshv
 */
public class DefaultApp extends App {
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"DefaultApp"};
        PApplet.main(concat(appletArgs, passedArgs));
    }

    @Override
    public void setup() {
        super.setup();

        centerCoord.setLatLon(new LatLon(55.741013f, 37.617157f));
        centerCoord.setLatLon(new LatLon(55.743732f, 37.60762f));

        loadPointCloud(loadTable("data/udarnik-10p.csv", "header"));

        loadRoadLayer("data/geo/road-transport.geojson", "transport", 0x22ffffff, 3);
        loadRoadLayer("data/geo/road-pedestrian.geojson", "people", 0x11ffffff, 2);

        loadAttractors(new GeoJSON(loadJSONObject("data/geo/trees.geojson")), "tree", 5, 15, 0x9900ff00);
//        loadTweets(0xff00c0bb, loadTable("data/tweets/pedestrian1.csv", "header"));
//        loadTweets(0xff00c0bb, loadTable("data/tweets/pedestrian2.csv", "header"));
//        loadTweets(0xff00c0bb, loadTable("data/tweets/runner.csv", "header"));
//        loadTweets(0xff00c0bb, loadTable("data/tweets/runner2.csv", "header"));
//        loadTweets(0xff00c0bb, loadTable("data/tweets/runner3.csv", "header"));
//        loadTweets(0xff00c0bb, loadTable("data/tweets/tourist-pedestrian.csv", "header"));
//        loadTweets(0xff00c0bb, loadTable("data/tweets/tourist1.csv", "header"));
//        loadTweets(0xff00c0bb, loadTable("data/tweets/tourist2.csv", "header"));

//        loadData(loadTable("data/ae-temp.csv", "header"));
        loadData(loadTable("data/ae.csv", "header"));

//        int cameraZ = -500;
//        camera.setOffset(new PVector(width / 2, height / 2, cameraZ));
        camera.lookAt(centerCoord);

//        setStartPoint(new LatLon(55.73898f, 37.605858f));
//        setEndPoint(new LatLon(55.739960443216781f, 37.617145380088019f));

//        Route r = navigator.navigate(crossroadStart, crossroadFinish);
//        if (r != null) currentRoute = r.bake();

//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));
//        AgentFactory.createFlyAgent(new LatLon(55.74433f, 37.615776f));

        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));

//        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), new LatLon(55.741013f, 37.617157f));

//        AgentFactory.createBoids(new LatLon(55.746178f, 37.615578f), "bird", 30);


//        EmitterFactory.createBoids(new LatLon(55.746178f, 37.615578f), "bird", 20, 30 * 1000);
//        EmitterFactory.createBoids(new LatLon(55.742428f, 37.612133f), "bird", 10, 20 * 1000);


//        EmitterFactory.createBike(new LatLon(55.746178f, 37.615578f), 10 * 1000);

//        EmitterFactory.createPedestrian(new LatLon(55.746178f, 37.615578f), 10 * 1000);

//        simulation.addAttractor(new Attractor("a", 100, projector.project(new LatLon(55.73998f, 37.616058f))));

//        AgentFactory.createBike(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createPedestrian(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
//        AgentFactory.createTransport(new LatLon(55.743732f, 37.60762f), new LatLon(55.741013f, 37.617157f));
    }
}
