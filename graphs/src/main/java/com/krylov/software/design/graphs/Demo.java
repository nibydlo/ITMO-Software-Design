package com.krylov.software.design.graphs;

import com.krylov.software.design.graphs.drawing.AwtDrawingApi;
import com.krylov.software.design.graphs.drawing.DrawingApi;
import com.krylov.software.design.graphs.drawing.JavafxDrawingApi;
import com.krylov.software.design.graphs.graph.Graph;
import com.krylov.software.design.graphs.graph.adjacency.AdjacencyMatrix;
import com.krylov.software.design.graphs.graph.adjacency.AdjacencyMatrixGraph;
import com.krylov.software.design.graphs.graph.edges.Edge;
import com.krylov.software.design.graphs.graph.edges.EdgeListGraph;

import java.util.List;

public class Demo {
    private static List<Edge> edgeList = List.of(new Edge(0, 1), new Edge(0, 2), new Edge(1, 2));
    private static AdjacencyMatrix matrix = new AdjacencyMatrix(List.of(
            List.of(false, true, true, true, true),
            List.of(true, false, true, true, true),
            List.of(true, true, false, true, true),
            List.of(true, true, true, false, true),
            List.of(true, true, true, true, false)
    ));

    public static void main(String[] args) throws Exception {
        DrawingApi drawingApi = args[0].equals("awt") ? new AwtDrawingApi() : new JavafxDrawingApi();
        Graph graph = args[1].equals("edges")
                ? new EdgeListGraph(drawingApi, edgeList)
                : new AdjacencyMatrixGraph(drawingApi, matrix);
        graph.drawGraph();
    }
}
