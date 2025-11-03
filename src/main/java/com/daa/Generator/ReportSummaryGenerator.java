package com.daa.Generator;


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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


public class ReportSummaryGenerator {

    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final String INPUT_FOLDER = "data";
    private static final String OUTPUT_FOLDER = "report";
    private static final String OUTPUT_FILE = OUTPUT_FOLDER + "/report_summary.csv";

    public static void main(String[] args) throws Exception {
        generateSummary(INPUT_FOLDER, OUTPUT_FILE);
        System.out.println("✅ Summary report generated: " + OUTPUT_FILE);
    }

    public static void generateSummary(String dataDir, String outCsvPath) throws Exception {
        Path in = Path.of(dataDir);
        if (!Files.exists(in)) throw new IllegalStateException("Input folder does not exist: " + dataDir);
        Files.createDirectories(Path.of(OUTPUT_FOLDER));

        List<Path> jsonFiles = Files.list(in)
                .filter(p -> p.toString().endsWith(".json"))
                .sorted()
                .collect(Collectors.toList());

        try (FileWriter fw = new FileWriter(outCsvPath)) {
            fw.write("file,n,edges,isDAG,sccCount,topoTimeMs,dagSpTimeMs,memoTimeMs\n");

            for (Path p : jsonFiles) {
                Map<String, Object> parsed = G.fromJson(Files.readString(p), Map.class);

                String fileName = p.getFileName().toString();
                List<String> nodes = (List<String>) parsed.get("nodes");

                List<Map<String, Object>> edgesRaw = (List<Map<String, Object>>) parsed.get("edges");
                List<Edge> edges = new ArrayList<>();
                if (edgesRaw != null) {
                    for (Map<String, Object> eo : edgesRaw) {
                        String from = String.valueOf(eo.get("from"));
                        String to = String.valueOf(eo.get("to"));
                        int w = ((Number) eo.getOrDefault("w", 1)).intValue();
                        edges.add(new Edge(from, to, w));
                    }
                }

                Graph g = new Graph(nodes, edges);
                int n = g.getNodes().size();
                int m = g.getEdges().size();

                // 1) SCC (Tarjan)
                long t0 = System.nanoTime();
                SCCResult sres = TarjanSCC.run(g);
                long t1 = System.nanoTime();
                double tarjanMs = (t1 - t0) / 1_000_000.0;
                int sccCount = sres.getCount();
                boolean isDAG = (sccCount == n);

                double topoMs = -1.0;
                double dagSpMs = -1.0;
                double memoMs = -1.0;

                if (isDAG) {
                    // For DAG: run topo and DAG algorithms on original graph
                    long t2 = System.nanoTime();
                    List<String> topo = TopologicalSort.kahn(g);
                    long t3 = System.nanoTime();
                    topoMs = (t3 - t2) / 1_000_000.0;

                    long t4 = System.nanoTime();
                    DAGShortestPath.Result sp = DAGShortestPath.shortestFromAutoSources(g);
                    long t5 = System.nanoTime();
                    dagSpMs = (t5 - t4) / 1_000_000.0;

                    // memoTime: currently same as dag shortest (placeholder)
                    memoMs = dagSpMs;
                } else {
                    // Not a DAG — try topological sort on condensation graph (optional)
                    long t2 = System.nanoTime();
                    Graph cond = CondensationBuilder.build(g, sres);
                    List<String> topoCond = TopologicalSort.kahn(cond);
                    long t3 = System.nanoTime();
                    topoMs = (t3 - t2) / 1_000_000.0;

                    // For non-DAG, run shortest/longest on condensation DAG
                    long t4 = System.nanoTime();
                    DAGShortestPath.Result sp = DAGShortestPath.shortestFromAutoSources(cond);
                    long t5 = System.nanoTime();
                    dagSpMs = (t5 - t4) / 1_000_000.0;

                    memoMs = dagSpMs;
                }

                // Write CSV row: use '-' for unavailable (but we have numeric times for all above)
                String topoStr = topoMs >= 0 ? String.format(Locale.US, "%.6f", topoMs) : "-";
                String dagSpStr = dagSpMs >= 0 ? String.format(Locale.US, "%.6f", dagSpMs) : "-";
                String memoStr = memoMs >= 0 ? String.format(Locale.US, "%.6f", memoMs) : "-";

                fw.write(String.join(",",
                        fileName,
                        String.valueOf(n),
                        String.valueOf(m),
                        String.valueOf(isDAG),
                        String.valueOf(sccCount),
                        topoStr,
                        dagSpStr,
                        memoStr
                ) + "\n");

                System.out.println("Processed " + fileName + "  (n=" + n + ", edges=" + m + ", isDAG=" + isDAG + ", scc=" + sccCount + ")");
            }
        }
    }
}
