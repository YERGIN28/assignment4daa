package com.daa.Generator;


public class GraphStats {
    public final int nodes;
    public final int edges;
    public final boolean isDAG;
    public final int sccCount;

    public GraphStats(int nodes, int edges, boolean isDAG, int sccCount) {
        this.nodes = nodes;
        this.edges = edges;
        this.isDAG = isDAG;
        this.sccCount = sccCount;
    }

    @Override
    public String toString() {
        return String.format("nodes=%d edges=%d isDAG=%b sccs=%d", nodes, edges, isDAG, sccCount);
    }
}
