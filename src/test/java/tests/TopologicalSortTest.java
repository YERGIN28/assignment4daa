package tests;

import com.daa.Graph.model.Edge;
import com.daa.Graph.model.Graph;
import com.daa.Graph.topo.TopologicalSort;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TopologicalSortTest {

    @Test
    void testKahnSimple() {

        List<String> nodes = Arrays.asList("A","B","C","D");
        List<Edge> edges = Arrays.asList(
                new Edge("A","B",0),
                new Edge("A","C",0),
                new Edge("B","D",0),
                new Edge("C","D",0)
        );
        Graph g = new Graph(nodes, edges);
        List<String> order = TopologicalSort.kahn(g);
        assertEquals(4, order.size());

        assertTrue(order.indexOf("A") < order.indexOf("B"));
        assertTrue(order.indexOf("A") < order.indexOf("C"));
        assertTrue(order.indexOf("B") < order.indexOf("D") || order.indexOf("C") < order.indexOf("D"));
    }

    @Test
    void testKahnCycle() {

        List<String> nodes = Arrays.asList("A","B","C");
        List<Edge> edges = Arrays.asList(
                new Edge("A","B",0),
                new Edge("B","C",0),
                new Edge("C","A",0)
        );
        Graph g = new Graph(nodes, edges);
        List<String> order = TopologicalSort.kahn(g);
        assertTrue(order.isEmpty());
    }
}
