package com.krylov.software.design.graphs.graph.adjacency;

import java.util.List;

public class AdjacencyMatrix {
    private List<List<Boolean>> matrix;
    private int vertexCount;

    public AdjacencyMatrix(List<List<Boolean>> matrix) {
        this.matrix = matrix;
        vertexCount = matrix.size();
    }

    public List<List<Boolean>> getMatrix() {
        return matrix;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
