package com.krylov.software.design.graphs.graph;

import com.krylov.software.design.graphs.drawing.DrawingApi;

import java.util.ArrayList;
import java.util.List;

public abstract class Graph {
    protected final static int GRAPH_CENTER_X = 250;
    protected final static int GRAPH_CENTER_Y = 150;
    protected final static int GRAPH_RADIUS = 100;
    protected final static int VERTEX_RADIUS = 20;

    public DrawingApi drawingApi;

    public Graph(DrawingApi drawingApi) {
        this.drawingApi = drawingApi;
    }

    public abstract void drawGraph();

    public List<Dot> regularPolygonCoordinates(int x, int y, int r, int n) {
        List<Dot> coordinates = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            coordinates.add(new Dot(
                    (int) Math.round(x + r * Math.cos(2 * Math.PI * i / n)),
                    (int) Math.round(y + r * Math.sin(2 * Math.PI * i / n))
            ));
        }
        return coordinates;
    }

    public Segment getSegment(Dot a, Dot b) {
        int horizontalDist = Math.abs(a.x - b.x);
        int verticalDist = Math.abs(a.y - b.y);
        double dist = Math.sqrt(Math.pow(horizontalDist, 2) + Math.pow(verticalDist, 2));
        int ax = a.x + (int) Math.round(Math.signum(b.x - a.x) * horizontalDist * VERTEX_RADIUS / dist) + VERTEX_RADIUS;
        int ay = a.y + (int) Math.round(Math.signum(b.y - a.y) * verticalDist * VERTEX_RADIUS / dist) + VERTEX_RADIUS;
        int bx = b.x + (int) Math.round(Math.signum(a.x - b.x) * horizontalDist * VERTEX_RADIUS / dist) + VERTEX_RADIUS;
        int by = b.y + (int) Math.round(Math.signum(a.y - b.y) * verticalDist * VERTEX_RADIUS / dist) + VERTEX_RADIUS;

        return new Segment(
                new Dot(ax, ay),
                new Dot(bx, by)
        );
    }
}
