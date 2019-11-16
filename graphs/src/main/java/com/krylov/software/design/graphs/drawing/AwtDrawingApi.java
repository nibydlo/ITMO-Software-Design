package com.krylov.software.design.graphs.drawing;

import com.krylov.software.design.graphs.drawing.figures.Circle;
import com.krylov.software.design.graphs.drawing.figures.Line;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class AwtDrawingApi extends Frame implements DrawingApi {
    private static int VERTICAL_SHIFT = 40;

    private List<Circle> circles = new ArrayList<>();
    private List<Line> lines = new ArrayList<>();

    public AwtDrawingApi() {}

    public AwtDrawingApi(List<Circle> circles, List<Line> lines) {
        this.circles = circles;
        this.lines = lines;
    }

    public long getDrawingAresWidth() {
        return 0;
    }

    public long getDrawingAreaHeight() {
        return 0;
    }

    public void drawCircle(int x, int y, int r, int val) {
        circles.add(new Circle(x, y, r, val));
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        lines.add(new Line(x1, y1, x2, y2));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D ga = (Graphics2D)g;
        ga.setPaint(Color.RED);
        ga.setStroke(new BasicStroke(1.0f));
        circles.forEach(circle -> {
            ga.draw(new Ellipse2D.Double(circle.x, circle.y + VERTICAL_SHIFT, 2*circle.r, 2*circle.r));
            ga.drawString(Integer.toString(circle.val), circle.x + circle.r - 5, circle.y + circle.r + 5 + VERTICAL_SHIFT);
        });
        lines.forEach(line -> {
            ga.drawLine(line.x1, line.y1 + VERTICAL_SHIFT, line.x2, line.y2 + VERTICAL_SHIFT);
        });
    }

    public void showPicture() {
        Frame frame = new AwtDrawingApi(this.circles, this.lines);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}
