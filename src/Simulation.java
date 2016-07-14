import geojson.LatLon;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public class Simulation {
    public ArrayList<CityGraph> graphs;
    public ArrayList<Agent> agents;
    public ArrayList<Attractor> attractors;

    private ArrayList<Track> tracks;

    Simulation() {
        graphs = new ArrayList<>();
        agents = new ArrayList<>();
        attractors = new ArrayList<>();
        tracks = new ArrayList<>();
    }

    public CityGraph graph(int i) {
        return graphs.get(i);
    }

    public void update() {
//        interactAgents();

        Iterator<Agent> i = this.agents.iterator();
        while (i.hasNext()) {
            Agent agent = i.next();
            interactAttractors(agent);

            agent.run();
            if (!agent.moving) {
                this.saveTrack(agent);
                i.remove();
            }
        }
    }

    private void interactAgents() {
        for (Agent a : this.agents) {
            this.agents
                    .stream()
                    .filter(b -> a != b)
                    .forEach(a::interact);
        }
    }

    private void interactAttractors(Agent agent) {
        this.attractors
                .stream()
                .forEach(agent::interact);
    }

    private void saveTrack(Agent agent) {
        this.tracks.add(agent.track);
    }

    public void addGraphLayer(CityGraph g) {
        graphs.add(g);
    }

    public void addAgent(Agent v) {
        agents.add(v);
    }

    public void addAttractor(Attractor a) {
        attractors.add(a);
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
