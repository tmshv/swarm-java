import geojson.IProjector;
import geojson.LatLon;
import pathfinder.*;
import processing.core.PVector;

/**
 * Created at 14/07/16
 *
 * @author tmshv
 */
public  class Driver {
    public City city;
    public int algorithm = 2;
    public int graphIndex = 0;

    private float f = 1.0f;

    private Vehicle vehicle;

    IProjector proj;

    public Driver(City city, int algorhitm, IProjector proj) {
        this.city = city;
        this.proj = proj;
        this.algorithm = algorithm;
    }

    public void drive(Vehicle v) {
        this.vehicle = v;
    }

    public Route navigate(LatLon from, LatLon to) {
        PVector fromV = proj.project(from);
        PVector toV = proj.project(to);
        Crossroad crFrom = this.city.graph(graphIndex).findNearestCrossroadTo(fromV);
        Crossroad crTo = this.city.graph(graphIndex).findNearestCrossroadTo(toV);

        return navigate(crFrom, crTo);
    }

    public Route navigate(Crossroad from, Crossroad to) {
        int start = this.city.graph(graphIndex).getCrossroadIndex(from);
        int finish = this.city.graph(graphIndex).getCrossroadIndex(to);

        return navigate(start, finish); //<>//
    }

    public Route navigate(int from, int to) {
        GraphNode[] graphRoute = findRoute(from, to);
        if (graphRoute.length == 0) return null;

        Route route = new Route(city.graph(graphIndex), graphRoute);
        return route;
    }

    public void driveWith(Route route) {
        if (this.vehicle != null) {
            this.vehicle.move(route);
        }
    }

    public GraphNode[] findRoute(int from, int to) {
        IGraphSearch finder = makePathFinder(this.city.graph(graphIndex).graph, this.algorithm);
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
}