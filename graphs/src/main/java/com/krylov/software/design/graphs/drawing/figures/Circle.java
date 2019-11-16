package com.krylov.software.design.graphs.drawing.figures;

import java.util.Arrays;
import java.util.List;

public class Circle {
    public int x, y, r, val;

    public Circle(int x, int y, int r, int val) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.val = val;
    }

    public String toString() {
        return x + " " + y + " " + r + " " + val;
    }

    public Circle(String s) {
        this(
                Integer.parseInt(Arrays.asList(s.split(" ")).get(0)),
                Integer.parseInt(Arrays.asList(s.split(" ")).get(1)),
                Integer.parseInt(Arrays.asList(s.split(" ")).get(2)),
                Integer.parseInt(Arrays.asList(s.split(" ")).get(3))
        );
    }
}
