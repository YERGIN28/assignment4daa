package com.daa.Generator;

import com.daa.Graph.model.Edge;
import com.daa.Graph.model.Graph;
import com.daa.Graph.scc.SCCResult;
import com.daa.Graph.scc.TarjanSCC;

import java.util.*;


public class GraphGenerator {

    private static final Random rnd = new Random();


    public static List<String> genNodeLabels(int n) {
        List<String> nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int first = i / 26;
            int second = i % 26;
            String label = (first == 0) ? String.valueOf((char)('A' + second))
                    : String.valueOf((char)('A' + first - 1)) + (char)('A' + second);
            nodes.add(label);
        }
        return nodes;
    }


    public static Graph generate(int n, double density, boolean makeAcyclic, int maxWeight, int maxDuration, Map<String,Integer> outDurations) {
        List<String> nodes = genNodeLabels(n);
        List<Edge> edges = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                if (makeAcyclic && j <= i) continue;

                if (rnd.nextDouble() <= density) {
                    int w = 1 + rnd.nextInt(Math.max(1, maxWeight));
                    edges.add(new Edge(nodes.get(i), nodes.get(j), w));
                }
            }
        }


        if (!makeAcyclic) {

            int extras = (int)(n * density * 0.3);
            for (int k = 0; k < extras; k++) {
                int i = rnd.nextInt(n);
                int j = rnd.nextInt(n);
                if (i == j) continue;
                int w = 1 + rnd.nextInt(Math.max(1, maxWeight));
                edges.add(new Edge(nodes.get(i), nodes.get(j), w));
            }
        }


        for (String node : nodes) {
            int dur = 1 + rnd.nextInt(Math.max(1, maxDuration));
            outDurations.put(node, dur);
        }

        return new Graph(nodes, edges);
    }


    public static GraphStats computeStats(Graph g) {
        SCCResult res = TarjanSCC.run(g);
        boolean isDAG = res.getCount() == g.getNodes().size(); // each vertex its own SCC -> DAG
        int edges = g.getEdges().size();
        return new GraphStats(g.getNodes().size(), edges, isDAG, res.getCount());
    }
}

