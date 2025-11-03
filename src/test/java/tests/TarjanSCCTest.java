package tests;

import com.daa.Graph.model.Edge;
import com.daa.Graph.model.Graph;
import com.daa.Graph.scc.SCCResult;
import com.daa.Graph.scc.TarjanSCC;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TarjanSCCTest {

    @Test
    void testSimpleCycle() {

        List<String> nodes = Arrays.asList("A","B","C");
        List<Edge> edges = Arrays.asList(
                new Edge("A","B",0),
                new Edge("B","C",0),
                new Edge("C","A",0)
        );
        Graph g = new Graph(nodes, edges);
        SCCResult res = TarjanSCC.run(g);
        assertEquals(1, res.getCount());
        assertTrue(res.getComponents().get(0).containsAll(Arrays.asList("A","B","C")));
    }

    @Test
    void testTwoComponents() {

        List<String> nodes = Arrays.asList("A","B","C","D");
        List<Edge> edges = Arrays.asList(
                new Edge("A","B",0),
                new Edge("B","A",0),
                new Edge("C","D",0)
        );
        Graph g = new Graph(nodes, edges);
        SCCResult res = TarjanSCC.run(g);
        assertEquals(3, res.getCount()); // {A,B}, {C}, {D}

        int compA = res.getCompId().get("A");
        int compB = res.getCompId().get("B");
        assertEquals(compA, compB);
    }

    @Test
    void testSingleNodes() {
        // no edges
        List<String> nodes = Arrays.asList("X","Y");
        List<Edge> edges = Arrays.asList();
        Graph g = new Graph(nodes, edges);
        SCCResult res = TarjanSCC.run(g);
        assertEquals(2, res.getCount());
    }
}
