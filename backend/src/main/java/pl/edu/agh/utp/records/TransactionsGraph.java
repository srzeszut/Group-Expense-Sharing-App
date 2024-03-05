package pl.edu.agh.utp.records;

import java.util.List;
import java.util.UUID;

public record TransactionsGraph(List<Vertex> vertices, List<Edge> edges) {

    public record Vertex(UUID userId, String username) {}

    public record Edge(UUID from, UUID to, double value) {}

    public record Pair(UUID from, UUID to) {}

}
