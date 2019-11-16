package com.krylov.software.design.graphs.graph.edges;

import com.krylov.software.design.graphs.drawing.DrawingApi;
import com.krylov.software.design.graphs.graph.Dot;
import com.krylov.software.design.graphs.graph.Graph;
import com.krylov.software.design.graphs.graph.Segment;

import java.util.*;

public class EdgeListGraph extends Graph {
    private List<Edge> edgeList;

    public EdgeListGraph(DrawingApi drawingApi, List<Edge> edgeList) {
        super(drawingApi);
        this.edgeList = edgeList;
    }

    public void drawGraph() {
        Set<Integer> vertexes = new HashSet<>();
        edgeList.forEach(edge -> {
            vertexes.add(edge.getV1());
            vertexes.add(edge.getV2());
        });
        List<Integer> vertexList = new ArrayList<>(vertexes);
        List<Dot> coordinates = regularPolygonCoordinates(GRAPH_CENTER_X, GRAPH_CENTER_Y, GRAPH_RADIUS, vertexList.size());
        Map<Integer, Dot> xyByVertex = new HashMap<>();
        for (int i = 0; i < vertexList.size(); i++) {
            drawingApi.drawCircle(coordinates.get(i).x, coordinates.get(i).y, VERTEX_RADIUS, vertexList.get(i));
            xyByVertex.put(vertexList.get(i), coordinates.get(i));
        }
        edgeList.forEach(edge -> {
            Segment segment = getSegment(xyByVertex.get(edge.getV1()), xyByVertex.get(edge.getV2()));
            drawingApi.drawLine(
                    segment.getX1().x,
                    segment.getX1().y,
                    segment.getX2().x,
                    segment.getX2().y
            );
        });
        drawingApi.showPicture();
    }
}
