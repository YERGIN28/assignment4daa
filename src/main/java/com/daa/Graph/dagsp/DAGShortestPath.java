package com.daa.Graph.dagsp;

import com.daa.Graph.model.Edge;
import com.daa.Graph.model.Graph;
import com.daa.Graph.topo.TopologicalSort;

import java.util.*;

public class DAGShortestPath {

    public static class Result {
        public final Map<String, Double> dist;
        public final Map<String, String> parent;

        public Result(Map<String, Double> dist, Map<String, String> parent) {
            this.dist = dist;
            this.parent = parent;
        }
    }

    public static Result shortestFromAutoSources(Graph dag) {
        List<String> topo = TopologicalSort.kahn(dag);
        Map<String, List<Edge>> adj = new HashMap<>();
        for (String n : dag.getNodes()) adj.put(n, new ArrayList<>());
        for (Edge e : dag.getEdges()) adj.get(e.from).add(e);


        Map<String, Integer> indeg = new HashMap<>();
        for (String n : dag.getNodes()) indeg.put(n, 0);
        for (Edge e : dag.getEdges()) indeg.put(e.to, indeg.getOrDefault(e.to, 0) + 1);

        Set<String> sources = new HashSet<>();
        for (String n : dag.getNodes()) if (indeg.getOrDefault(n, 0) == 0) sources.add(n);


        Map<String, Double> dist = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        for (String n : dag.getNodes()) dist.put(n, Double.POSITIVE_INFINITY);

        for (String s : sources) {
            dist.put(s, 0.0);
            parent.put(s, null);
        }


        for (String u : topo) {
            double du = dist.getOrDefault(u, Double.POSITIVE_INFINITY);
            if (du == Double.POSITIVE_INFINITY) continue; // unreachable so far
            for (Edge e : adj.getOrDefault(u, Collections.emptyList())) {
                String v = e.to;
                double nd = du + e.weight;
                if (nd < dist.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    dist.put(v, nd);
                    parent.put(v, u);
                }
            }
        }

        return new Result(dist, parent);
    }

    public static List<String> reconstructPath(Map<String, String> parent, String target) {
        if (!parent.containsKey(target) && parent.get(target) == null) {

        }
        LinkedList<String> path = new LinkedList<>();
        String cur = target;
        while (cur != null) {
            path.addFirst(cur);
            cur = parent.get(cur);
        }
        return path;
    }
}

