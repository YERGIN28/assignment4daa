package com.daa.Graph.scc;


import java.util.List;
import java.util.Map;


public class SCCResult {
    private final List<List<String>> components;
    private final Map<String, Integer> compId;

    public SCCResult(List<List<String>> components, Map<String, Integer> compId) {
        this.components = components;
        this.compId = compId;
    }

    public List<List<String>> getComponents() {
        return components;
    }

    public Map<String, Integer> getCompId() {
        return compId;
    }

    public int getCount() {
        return components.size();
    }
}
