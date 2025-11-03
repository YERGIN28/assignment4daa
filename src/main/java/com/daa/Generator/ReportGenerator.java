package com.daa.Generator;

import com.daa.Graph.dagsp.DAGLongestPath;
import com.daa.Graph.dagsp.DAGShortestPath;

import com.daa.Graph.model.Edge;
import com.daa.Graph.model.Graph;
import com.daa.Graph.scc.CondensationBuilder;
import com.daa.Graph.scc.SCCResult;
import com.daa.Graph.scc.TarjanSCC;
import com.daa.Graph.topo.TopologicalSort;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {

    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public static class DatasetSummary {
        public int id;
        public String category;
        public int nodes;
        public int edges;
        public boolean isDAG;
        public int numSCC;
        public double tarjan_ms;
        public double topo_ms;
        public double shortest_ms;
        public double longest_ms;
    }

    public static void generateAll(String dataDir, String outDir) throws Exception {
        Path in = Path.of(dataDir);
        Path out = Path.of(outDir);
        if (!Files.exists(out)) Files.createDirectories(out);

        List<Path> jsonFiles = Files.list(in)
                .filter(p -> p.toString().endsWith(".json"))
                .sorted()
                .collect(Collectors.toList());

        List<DatasetSummary> summaries = new ArrayList<>();

        for (Path p : jsonFiles) {
            Map<String, Object> parsed = G.fromJson(Files.readString(p), Map.class);

            int id = ((Number) parsed.getOrDefault("id", 0)).intValue();
            String category = (String) parsed.getOrDefault("category", "unknown");

            // nodes
            List<String> nodes = (List<String>) parsed.get("nodes");

            // durations
            Map<String, Integer> durations = new HashMap<>();
            Object durObj = parsed.get("durations");
            if (durObj instanceof Map) {
                Map<String, Object> dmap = (Map<String, Object>) durObj;
                for (String key : dmap.keySet()) {
                    durations.put(key, ((Number) dmap.get(key)).intValue());
                }
            }

            // edges
            List<Map<String, Object>> edgesRaw = (List<Map<String, Object>>) parsed.get("edges");
            List<Edge> edges = new ArrayList<>();
            if (edgesRaw != null) {
                for (Map<String, Object> eo : edgesRaw) {
                    String from = (String) eo.get("from");
                    String to = (String) eo.get("to");
                    int w = ((Number) eo.getOrDefault("w", 1)).intValue();
                    edges.add(new Edge(from, to, w));
                }
            }

            Graph g = new Graph(nodes, edges);

            DatasetSummary s = new DatasetSummary();
            s.id = id;
            s.category = category;
            s.nodes = nodes.size();
            s.edges = edges.size();

            // 1) Tarjan SCC
            long t0 = System.nanoTime();
            SCCResult sres = TarjanSCC.run(g);
            long t1 = System.nanoTime();
            s.tarjan_ms = (t1 - t0) / 1_000_000.0;
            s.numSCC = sres.getCount();
            s.isDAG = (s.numSCC == s.nodes);

            // 2) Condensation + Topo
            Graph cond = CondensationBuilder.build(g, sres);
            long t2 = System.nanoTime();
            List<String> topo = TopologicalSort.kahn(cond);
            long t3 = System.nanoTime();
            s.topo_ms = (t3 - t2) / 1_000_000.0;

            // 3) Shortest path (in DAG)
            long t4 = System.nanoTime();
            try {
                Graph target = s.isDAG ? g : cond;
                DAGShortestPath.Result sp = DAGShortestPath.shortestFromAutoSources(target);
                long t5 = System.nanoTime();
                s.shortest_ms = (t5 - t4) / 1_000_000.0;
            } catch (Exception e) {
                long t5 = System.nanoTime();
                s.shortest_ms = (t5 - t4) / 1_000_000.0;
            }

            // 4) Longest path (critical path)
            long t6 = System.nanoTime();
            try {
                Graph target = s.isDAG ? g : cond;
                DAGLongestPath.Result lp = DAGLongestPath.longestFromAutoSources(target, durations);
                long t7 = System.nanoTime();
                s.longest_ms = (t7 - t6) / 1_000_000.0;
            } catch (Exception e) {
                long t7 = System.nanoTime();
                s.longest_ms = (t7 - t6) / 1_000_000.0;
            }

            summaries.add(s);
        }

        // write CSV
        Path csv = out.resolve("report.csv");
        try (FileWriter fw = new FileWriter(csv.toFile())) {
            fw.write("id,category,nodes,edges,isDAG,numSCC,tarjan_ms,topo_ms,shortest_ms,longest_ms\n");
            for (DatasetSummary ds : summaries) {
                fw.write(String.format(Locale.US,
                        "%d,%s,%d,%d,%b,%d,%.6f,%.6f,%.6f,%.6f\n",
                        ds.id, ds.category, ds.nodes, ds.edges, ds.isDAG, ds.numSCC,
                        ds.tarjan_ms, ds.topo_ms, ds.shortest_ms, ds.longest_ms));
            }
        }

        // write JSON output
        Path outJson = out.resolve("output.json");
        try (FileWriter fw = new FileWriter(outJson.toFile())) {
            G.toJson(summaries, fw);
        }

        System.out.println(" Report generated: " + csv);
        System.out.println(" Output JSON: " + outJson);
    }

    public static void main(String[] args) throws Exception {
        String dataDir = "data";
        String outDir = "reports";
        if (args.length > 0) dataDir = args[0];
        if (args.length > 1) outDir = args[1];
        generateAll(dataDir, outDir);
    }
}
