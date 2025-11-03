package tests;

import com.daa.Graph.model.Edge;
import com.daa.Graph.model.Graph;
import com.daa.Graph.scc.CondensationBuilder;
import com.daa.Graph.scc.SCCResult;
import com.daa.Graph.scc.TarjanSCC;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CondensationBuilderTest {

    @Test
    void testCondensationBasic() {
        // Graph: A->B, B->A, B->C, C->D
        List<String> nodes = Arrays.asList("A","B","C","D");
        List<Edge> edges = Arrays.asList(
                new Edge("A","B",0),
                new Edge("B","A",0),
                new Edge("B","C",0),
                new Edge("C","D",0)
        );
        Graph g = new Graph(nodes, edges);
        SCCResult scc = TarjanSCC.run(g);
        Graph cond = CondensationBuilder.build(g, scc);

        assertTrue(cond.getNodes().size() >= 2);

        assertFalse(cond.getEdges().isEmpty());
    }
}
