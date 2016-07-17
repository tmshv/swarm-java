import geojson.IProjector;
import geojson.LatLon;
import pathfinder.*;
import processing.core.PVector;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public  class Navigator {
    public Simulation simulation;
    public int algorithm = 2;
    public int graphIndex = 0;

    private float f = 1.0f;

    private Follower vehicle;

    IProjector proj;

    public Navigator(Simulation simulation, int algorhitm, IProjector proj) {
        this.simulation = simulation;
        this.proj = proj;
        this.algorithm = algorithm;
    }

    public void drive(Follower v) {
        this.vehicle = v;
    }

    public Route navigate(LatLon from, LatLon to) {
        try{
            PVector fromV = proj.project(from);
            PVector toV = proj.project(to);
            Crossroad crFrom = this.simulation.graph(graphIndex).findNearestCrossroadTo(fromV);
            Crossroad crTo = this.simulation.graph(graphIndex).findNearestCrossroadTo(toV);

            return navigate(crFrom, crTo);
        }catch (Exception e){
            return null;
        }
    }

    public Route navigate(Crossroad from, Crossroad to) {
        int start = this.simulation.graph(graphIndex).getCrossroadIndex(from);
        int finish = this.simulation.graph(graphIndex).getCrossroadIndex(to);

        return navigate(start, finish);
    }

    public Route navigate(int from, int to) {
        GraphNode[] graphRoute = findRoute(from, to);
        if (graphRoute.length == 0) return null;

        Route route = new Route(simulation.graph(graphIndex), graphRoute);
        return route;
    }

    public void driveWith(Route route) {
        if (this.vehicle != null) {
            this.vehicle.move(route);
        }
    }

    public GraphNode[] findRoute(int from, int to) {
        IGraphSearch finder = makePathFinder(this.simulation.graph(graphIndex).graph, this.algorithm);
        finder.search(from, to);
        return finder.getRoute();
    }

    public IGraphSearch makePathFinder(Graph graph, int alg) {
        IGraphSearch pf = null;

        switch (alg) {
            case 0:
                pf = new GraphSearch_DFS(graph);
                break;
            case 1:
                pf = new GraphSearch_BFS(graph);
                break;
            case 2:
                pf = new GraphSearch_Dijkstra(graph);
                break;
            case 3:
                pf = new GraphSearch_Astar(graph, new AshCrowFlight(this.f));
                break;
            case 4:
                pf = new GraphSearch_Astar(graph, new AshManhattan(this.f));
                break;
        }
        return pf;
    }

    public void setLayer(String layerName) {
        this.graphIndex = simulation.getLayerIndex(layerName);
    }
}
