package com.daa.Generator;

import com.daa.Graph.model.Edge;
import com.daa.Graph.model.Graph;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DatasetGenerator {

    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final Random rnd = new Random();

    public static void generateAll(String outDir) throws Exception {
        Path dir = Path.of(outDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        int id = 1;

        for (int i = 0; i < 3; i++) {
            int n = 6 + rnd.nextInt(5); // 6..10
            double density = rnd.nextBoolean() ? 0.2 : 0.5; // sparse vs medium
            boolean makeDAG = rnd.nextDouble() < 0.7; // mostly DAGs
            generateAndSave(dir, id++, "small", n, density, makeDAG);
        }


        for (int i = 0; i < 3; i++) {
            int n = 10 + rnd.nextInt(11); // 10..20
            double density = 0.2 + rnd.nextDouble() * 0.5; // mixed
            boolean makeDAG = rnd.nextDouble() < 0.5;
            generateAndSave(dir, id++, "medium", n, density, makeDAG);
        }

        // large: 3 graphs, nodes 20-50
        for (int i = 0; i < 3; i++) {
            int n = 20 + rnd.nextInt(31); // 20..50
            double density = 0.1 + rnd.nextDouble() * 0.6;
            boolean makeDAG = rnd.nextDouble() < 0.4;
            generateAndSave(dir, id++, "large", n, density, makeDAG);
        }
    }

    private static void generateAndSave(Path dir, int id, String category, int n, double density, boolean makeDAG) throws Exception {
        Map<String,Integer> durations = new HashMap<>();
        Graph g = GraphGenerator.generate(n, density, makeDAG, 20, 15, durations);
        GraphStats stats = GraphGenerator.computeStats(g);

        Map<String,Object> out = new LinkedHashMap<>();
        out.put("id", id);
        out.put("category", category);
        out.put("nodes", g.getNodes());
        out.put("durations", durations);


        List<Map<String,Object>> edgesJson = new ArrayList<>();
        for (Edge e : g.getEdges()) {
            Map<String,Object> eo = new LinkedHashMap<>();
            eo.put("from", e.from);
            eo.put("to", e.to);
            eo.put("w", e.weight);
            edgesJson.add(eo);
        }
        out.put("edges", edgesJson);
        out.put("isDAG", stats.isDAG);
        out.put("numSCC", stats.sccCount);
        out.put("meta", stats.toString());

        String fname = String.format("%s/%s_%d.json", dir.toString(), category, id);
        try (FileWriter fw = new FileWriter(new File(fname))) {
            G.toJson(out, fw);
        }
        System.out.printf("Saved %s (n=%d, edges=%d, isDAG=%b, scc=%d)\n", fname, stats.nodes, stats.edges, stats.isDAG, stats.sccCount);
    }


    public static void main(String[] args) throws Exception {
        String out = "data";
        if (args.length > 0) out = args[0];
        generateAll(out);
    }
}

