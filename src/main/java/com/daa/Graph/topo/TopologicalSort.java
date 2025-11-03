package com.daa.Graph.topo;

import com.daa.Graph.model.Edge;
import com.daa.Graph.model.Graph;
import java.util.*;

public class TopologicalSort {

    public static List<String> kahn(Graph dag) {
        List<String> nodes = new ArrayList<>(dag.getNodes());
        List<Edge> edges = dag.getEdges();

        Map<String, Integer> indegree = new HashMap<>();
        Map<String, List<String>> adj = new HashMap<>();
        for (String n : nodes) {
            indegree.put(n, 0);
            adj.put(n, new ArrayList<>());
        }

        for (Edge e : edges) {

            adj.get(e.from).add(e.to);
            indegree.put(e.to, indegree.getOrDefault(e.to, 0) + 1);
        }

        Deque<String> q = new ArrayDeque<>();
        for (String n : nodes) {
            if (indegree.getOrDefault(n, 0) == 0) q.add(n);
        }

        List<String> order = new ArrayList<>();
        while (!q.isEmpty()) {
            String u = q.poll();
            order.add(u);
            for (String v : adj.getOrDefault(u, Collections.emptyList())) {
                indegree.put(v, indegree.get(v) - 1);
                if (indegree.get(v) == 0) q.add(v);
            }
        }

        if (order.size() != nodes.size()) {
            return Collections.emptyList();
        }
        return order;
    }
}
