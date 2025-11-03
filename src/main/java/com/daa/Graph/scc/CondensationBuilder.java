package com.daa.Graph.scc;

import com.daa.Graph.model.Edge;
import com.daa.Graph.model.Graph;

import java.util.*;


public class CondensationBuilder {
    public static Graph build(Graph original, SCCResult sccResult) {
        Map<String, Integer> compId = sccResult.getCompId();
        int compCount = sccResult.getCount();

        List<String> condNodes = new ArrayList<>();
        for (int i = 0; i < compCount; i++) condNodes.add("C" + i);


        Set<String> seen = new HashSet<>();
        List<Edge> condEdges = new ArrayList<>();

        for (Edge e : original.getEdges()) {
            String u = e.from;
            String v = e.to;
            int cu = compId.get(u);
            int cv = compId.get(v);
            if (cu != cv) {
                String key = cu + "->" + cv;
                if (!seen.contains(key)) {
                    seen.add(key);

                    condEdges.add(new Edge("C" + cu, "C" + cv, 1));
                }
            }
        }

        return new Graph(condNodes, condEdges);
    }
}

