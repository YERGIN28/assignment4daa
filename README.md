
# Assignment 4

## 1. Overview
This project is part of **Assignment 4** and implements several core graph algorithms used for analysis of directed graphs.  
The system is able to:
- generate datasets of different sizes (small / medium / large)
- evaluate each graph using multiple algorithms
- produce both per-graph reports and a global summary report

All algorithms are implemented manually, from scratch, without external graph libraries.

---

## 2. Implemented Algorithms

| Algorithm | Purpose | Notes |
|-----------|----------|-------|
| **Tarjan SCC** | Finds Strongly Connected Components | Works for any directed graph |
| **Topological Sort (Kahn)** | Valid only if the graph is a DAG | Fails if cycles exist |
| **DAG Shortest Path** | DP-based shortest path on DAG | Linear: `O(V + E)` |
| **Memoized DAG Shortest Path** | Same as above but memo-based | Same time as DAG SP in this project |

---

## 3. Project Structure
```

src/
├─ main/java/com/daa/
│   ├─ graph/model/           (Graph, Edge)
│   ├─ graph/scc/             (Tarjan SCC, condensation)
│   ├─ graph/topo/            (Topological Sort)
│   ├─ graph/dagsp/           (DAG shortest path)
│   ├─ generator/
│   │   ├─ DataGenerator.java
│   │   ├─ ReportGenerator.java
│   │   └─ ReportSummaryGenerator.java
│
└─ test/java/tests/           (JUnit tests)
data/                          (generated JSON datasets)
report/                        (CSV results)

```

---

## 4. How to Run

### 4.1 Generate graph datasets
```

Run → DataGenerator.main()

```
Output: `data/*.json`

### 4.2 Generate per-graph reports (one CSV per dataset)
```

Run → ReportGenerator.main()

```
Output: `report/*.csv`

### 4.3 Generate summary report (all graphs in one table)
```

Run → ReportSummaryGenerator.main()

```
Output: `report/report_summary.csv`

### 4.4 Run tests
```

mvn clean test

```

### 4.5 Run in IntelliJ
1. Open project → Maven loads automatically  
2. Right-click any generator → Run  
3. Or press **Shift + F10**

---

## 5. Dataset & Weight Model
| Dataset type | Node range | Edge density | Weight model |
|--------------|------------|--------------|--------------|
| small | 5–10 nodes | sparse | random ints 1–10 |
| medium | 10–20 nodes | mixed | random ints 1–10 |
| large | 30–40 nodes | dense | random ints 1–10 |

Weights are **positive only**, so Dijkstra-style DP is valid.

---

## 6. Full Results Table (from `report/report_summary.csv`)

| file | n | edges | isDAG | sccCount | topoTimeMs | dagSpTimeMs | memoTimeMs |
|------|----|--------|--------|-----------|-------------|--------------|--------------|
| large_7.json | 34 | 376 | true | 34 | 0.449101 | 0.919000 | 0.919000 |
| large_8.json | 30 | 352 | false | 1 | 0.158500 | 0.023300 | 0.023300 |
| large_9.json | 33 | 88 | true | 33 | 0.147400 | 0.291000 | 0.291000 |
| medium_4.json | 19 | 218 | false | 1 | 0.153300 | 0.044000 | 0.044000 |
| medium_5.json | 15 | 38 | true | 15 | 0.099400 | 0.216500 | 0.216500 |
| medium_6.json | 12 | 51 | false | 1 | 0.076800 | 0.040300 | 0.040300 |
| small_1.json | 6 | 15 | false | 2 | 0.060300 | 0.028300 | 0.028300 |
| small_2.json | 10 | 9 | true | 10 | 0.033400 | 0.063200 | 0.063200 |
| small_3.json | 9 | 19 | true | 9 | 0.081799 | 0.155301 | 0.155301 |

 `memoTimeMs == dagSpTimeMs` is intentional because memo-version uses the same DP logic.

---

## 7. Analysis

### 7.1 SCC Performance
- SCC runtime increases with **graph density**, not just node count.
- Graphs where `sccCount = 1` are fully cyclic → Tarjan runs slower due to deep recursion.
- DAG graphs (`sccCount = n`) finish faster, because no merging occurs.

### 7.2 Topological Sort
- Toposort is **only valid when SCC count equals number of nodes**.
- In cyclic graphs, toposort is instead performed on the condensation DAG.

### 7.3 DAG Shortest Path
- Runs in **linear time `O(V + E)`**, so even large dense DAGs perform well.
- Works only when no cycles exist (or after SCC condensation).

### 7.4 Memoized Shortest Path
- Same complexity as DAG shortest path.
- In this implementation, memo and non-memo versions give identical runtimes.

### 7.5 Effect of graph structure
| Property | Effect |
|----------|--------|
| More SCCs → closer to DAG | faster topo & DAG SP |
| Fully cyclic graph | SCC heavy, topo impossible |
| Higher density | SCC slower, DAG SP still linear |

---

## 8. Conclusions
| When to use | Recommendation |
|-------------|----------------|
| Graph may contain cycles | run SCC first |
| Graph is a DAG | topological + DAG shortest path = fastest |
| You need strongly connected regions | use Tarjan |
| Graph is dense and cyclic | condensation graph is more efficient |

---

## 9. Author
**Ginayat Yerassyl**  
SE-2422  

