package com.krylov.software.design.graphs.graph;

public class Segment {
    private final Dot x1, x2;

    public Segment(Dot x1, Dot x2) {
        this.x1 = x1;
        this.x2 = x2;
    }

    public Dot getX1() {
        return x1;
    }

    public Dot getX2() {
        return x2;
    }
}
