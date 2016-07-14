import geojson.LatLon;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class City {
    public ArrayList<CityGraph> graphs;
    public ArrayList<Agent> agents;

    int trafficLimit = 1000;

    City() {
        graphs = new ArrayList<CityGraph>();
        agents = new ArrayList<Agent>();
    }

    public void update() {
        Iterator<Agent> i = this.agents.iterator();
        while (i.hasNext()) {
            Agent v = i.next();
            // v.update();
            v.run();
//            if (!v.moving) i.remove();
        }
    }

    public CityGraph graph(int i) {
        return graphs.get(i);
    }

    public void addCityGraph(CityGraph g) {
        graphs.add(g);
    }

    public void addAgent(Agent v) {
        agents.add(v);
    }

    public LatLon getCenter() {
        LatLon[] list = new LatLon[graphs.size()];
        for (int i = 0; i < graphs.size(); i++) {
            list[i] = graphs.get(i).getCenter();
        }

        LatLon c = new LatLon();
        for (LatLon ll : list) {
            c.lat += ll.lat;
            c.lon += ll.lon;
        }

        c.lat /= graphs.size();
        c.lon /= graphs.size();
        return c;
    }
}
