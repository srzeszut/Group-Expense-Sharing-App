package pl.edu.agh.utp.model.graph

import java.util.UUID

data class TransactionsGraph(val vertices: List<Vertex>, val edges: List<Edge>) {

    data class Vertex(val userId: UUID, val username: String)

    data class Edge(val from: UUID, val to: UUID, val value: Double)

    fun toNodesString(): String {
        val nodesList = vertices.map { vertex ->
            "{ id: '${vertex.userId}', label: '${vertex.username}' }"
        }
        return nodesList.joinToString(",\n", "[\n", "\n]")
    }

    fun toEdgesString(): String {
        val edgesList = edges.map { edge ->
            "{ from: '${edge.from}', to: '${edge.to}', label: '${edge.value}' }"
        }
        return edgesList.joinToString(",\n", "[\n", "\n]")
    }
}