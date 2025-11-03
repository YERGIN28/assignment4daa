package tests;

import com.daa.Graph.dagsp.DAGShortestPath;
import com.daa.Graph.model.Edge;
import com.daa.Graph.model.Graph;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DAGShortestPathTest {

    @Test
    void testSimpleShortestAuto() {
        // DAG: A->B(1), A->C(4), B->C(2), C->D(1)
        List<String> nodes = Arrays.asList("A","B","C","D");
        List<Edge> edges = Arrays.asList(
                new Edge("A","B",1),
                new Edge("A","C",4),
                new Edge("B","C",2),
                new Edge("C","D",1)
        );
        Graph g = new Graph(nodes, edges);
        DAGShortestPath.Result r = DAGShortestPath.shortestFromAutoSources(g);
        Map<String, Double> dist = r.dist;
        assertEquals(0.0, dist.get("A")); // A is source
        assertEquals(1.0, dist.get("B"));
        assertEquals(3.0, dist.get("C")); // A->B->C is 3
        assertEquals(4.0, dist.get("D"));
    }

    @Test
    void testUnreachable() {

        List<String> nodes = Arrays.asList("A","B","E");
        List<Edge> edges = Arrays.asList(
                new Edge("A","B",1)
        );
        Graph g = new Graph(nodes, edges);
        DAGShortestPath.Result r = DAGShortestPath.shortestFromAutoSources(g);
        Map<String, Double> dist = r.dist;


        assertEquals(0.0, dist.get("A"));

        assertEquals(1.0, dist.get("B"));

        assertEquals(0.0, dist.get("E"));
    }

}
