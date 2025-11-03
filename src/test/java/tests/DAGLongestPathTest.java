package tests;


import com.daa.Graph.dagsp.DAGLongestPath;
import com.daa.Graph.model.Edge;
import com.daa.Graph.model.Graph;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DAGLongestPathTest {

    @Test
    void testLongestEdgeWeights() {

        // A->B(2), B->C(3), A->C(4)
        List<String> nodes = Arrays.asList("A","B","C");
        List<Edge> edges = Arrays.asList(
                new Edge("A","B",2),
                new Edge("B","C",3),
                new Edge("A","C",4)
        );
        Graph g = new Graph(nodes, edges);
        DAGLongestPath.Result r = DAGLongestPath.longestFromAutoSources(g, null); // edge mode
        Map<String, Double> dist = r.dist;
        // longest to C is A->B->C = 5
        assertEquals(0.0, dist.get("A"));
        assertEquals(2.0, dist.get("B"));
        assertEquals(5.0, dist.get("C"));
    }

    @Test
    void testLongestNodeDurations() {
        // Node durations: A=1, B=2, C=3
        // Edges: A->B, B->C, A->C
        List<String> nodes = Arrays.asList("A","B","C");
        List<Edge> edges = Arrays.asList(
                new Edge("A","B",0),
                new Edge("B","C",0),
                new Edge("A","C",0)
        );
        Graph g = new Graph(nodes, edges);
        Map<String, Integer> durations = new HashMap<>();
        durations.put("A", 1);
        durations.put("B", 2);
        durations.put("C", 3);
        DAGLongestPath.Result r = DAGLongestPath.longestFromAutoSources(g, durations);
        Map<String, Double> dist = r.dist;
        // longest to C: A(1)+B(2)+C(3) = 6 via A->B->C
        assertEquals(1.0, dist.get("A"));
        assertEquals(3.0, dist.get("B"));
        assertEquals(6.0, dist.get("C"));
    }
}
