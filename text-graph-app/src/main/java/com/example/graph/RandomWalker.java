package com.example.graph;


import java.util.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class RandomWalker {
  private final Graph<String, DefaultWeightedEdge> graph;
  private final Set<DefaultWeightedEdge> seen = new HashSet<>();
  private final Random rnd = new Random();
  private String curr;
  private final StringBuilder sb = new StringBuilder();

  public RandomWalker(Graph<String, DefaultWeightedEdge> graph) {
    this.graph = graph;
    List<String> vs = new ArrayList<>(graph.vertexSet());
    curr = vs.get(rnd.nextInt(vs.size()));
    sb.append(curr);
  }

  public boolean canStep() {
    return !graph.outgoingEdgesOf(curr).isEmpty();
  }

  public String step() {
    var edges = graph.outgoingEdgesOf(curr);
    List<DefaultWeightedEdge> edgeList = new ArrayList<>(edges);
    if (edgeList.isEmpty()) return null;
    DefaultWeightedEdge e = edgeList.get(rnd.nextInt(edgeList.size()));
    if (seen.contains(e)) return null;
    seen.add(e);
    curr = graph.getEdgeTarget(e);
    sb.append(" → ").append(curr);
    return curr;
  }

  public String getPath() {
    return sb.toString();
  }
}
