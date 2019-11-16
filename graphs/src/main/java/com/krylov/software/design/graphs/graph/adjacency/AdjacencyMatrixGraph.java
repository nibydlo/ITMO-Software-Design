package com.krylov.software.design.graphs.graph.adjacency;

import com.krylov.software.design.graphs.drawing.DrawingApi;
import com.krylov.software.design.graphs.graph.Dot;
import com.krylov.software.design.graphs.graph.Graph;
import com.krylov.software.design.graphs.graph.Segment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdjacencyMatrixGraph extends Graph {
    private AdjacencyMatrix matrix;

    public AdjacencyMatrixGraph(DrawingApi drawingApi, AdjacencyMatrix matrix) {
        super(drawingApi);
        this.matrix = matrix;
    }

    public void drawGraph() {
        List<Dot> coordinates = regularPolygonCoordinates(GRAPH_CENTER_X, GRAPH_CENTER_Y, GRAPH_RADIUS, matrix.getVertexCount());
        Map<Integer, Dot> xyByVertex = new HashMap<>();
        for (int i = 0; i < matrix.getVertexCount(); i++) {
            drawingApi.drawCircle(coordinates.get(i).x, coordinates.get(i).y, VERTEX_RADIUS, i);
            xyByVertex.put(i, coordinates.get(i));

        }
        for (int i = 0; i < matrix.getVertexCount(); i++) {
            for (int j = i + 1; j < matrix.getVertexCount(); j++) {
                if (matrix.getMatrix().get(i).get(j)) {
                    Segment segment = getSegment(xyByVertex.get(i), xyByVertex.get(j));
                    drawingApi.drawLine(
                            segment.getX1().x,
                            segment.getX1().y,
                            segment.getX2().x,
                            segment.getX2().y
                    );
                }
            }
        }
        drawingApi.showPicture();
    }
}
