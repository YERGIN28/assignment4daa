package com.daa.Graph.scc;

import com.daa.Graph.model.Edge;
import com.daa.Graph.model.Graph;
import java.util.*;

public class TarjanSCC {

    public static SCCResult run(Graph graph) {
        List<String> nodes = graph.getNodes();
        List<Edge> edges = graph.getEdges();


        Map<String, List<String>> adj = new HashMap<>();
        for (String n : nodes) adj.put(n, new ArrayList<>());
        for (Edge e : edges) {

            adj.get(e.from).add(e.to);
        }

        Map<String, Integer> index = new HashMap<>();
        Map<String, Integer> lowlink = new HashMap<>();
        Deque<String> stack = new ArrayDeque<>();
        Set<String> onStack = new HashSet<>();

        List<List<String>> components = new ArrayList<>();
        Map<String, Integer> compId = new HashMap<>();

        int[] idx = {0};

        for (String v : nodes) {
            if (!index.containsKey(v)) {
                strongConnect(v, adj, index, lowlink, stack, onStack, components, compId, idx);
            }
        }

        return new SCCResult(components, compId);
    }

    private static void strongConnect(
            String v,
            Map<String, List<String>> adj,
            Map<String, Integer> index,
            Map<String, Integer> lowlink,
            Deque<String> stack,
            Set<String> onStack,
            List<List<String>> components,
            Map<String, Integer> compId,
            int[] idx
    ) {
        index.put(v, idx[0]);
        lowlink.put(v, idx[0]);
        idx[0]++;
        stack.push(v);
        onStack.add(v);


        for (String w : adj.getOrDefault(v, Collections.emptyList())) {
            if (!index.containsKey(w)) {

                strongConnect(w, adj, index, lowlink, stack, onStack, components, compId, idx);
                lowlink.put(v, Math.min(lowlink.get(v), lowlink.get(w)));
            } else if (onStack.contains(w)) {

                lowlink.put(v, Math.min(lowlink.get(v), index.get(w)));
            }
        }


        if (lowlink.get(v).equals(index.get(v))) {
            List<String> component = new ArrayList<>();
            while (true) {
                String w = stack.pop();
                onStack.remove(w);
                component.add(w);
                compId.put(w, components.size());
                if (w.equals(v)) break;
            }
            Collections.reverse(component);
            components.add(component);
        }
    }
}
