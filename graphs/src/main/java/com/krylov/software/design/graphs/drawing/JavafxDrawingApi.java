package com.krylov.software.design.graphs.drawing;

import com.krylov.software.design.graphs.drawing.figures.Circle;
import com.krylov.software.design.graphs.drawing.figures.Line;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavafxDrawingApi extends Application implements DrawingApi {
    private final static int WIDTH = 600;
    private final static int HEIGHT = 400;

    private List<Circle> circles = new ArrayList<>();
    private List<Line> lines = new ArrayList<>();

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

    public void showPicture() {
        launch(
                circles.stream().map(Circle::toString).collect(Collectors.joining(".!.")),
                lines.stream().map(Line::toString).collect(Collectors.joining(".!."))
        );
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parameters params = getParameters();
        List<String> list = params.getRaw();
        List<String> circlesStrings = Arrays.asList(list.get(0).split(".!."));
        List<Circle> circles = circlesStrings.stream().map(Circle::new).collect(Collectors.toList());
        List<String> linesStrings = Arrays.asList(list.get(1).split(".!."));
        List<Line> lines = list.get(1).equals("")
                ? List.of()
                : linesStrings.stream().map(Line::new).collect(Collectors.toList());
        Group root = new Group();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        circles.forEach(circle -> {
            gc.strokeOval(circle.x, circle.y, 2*circle.r, 2*circle.r);
            gc.strokeText(Integer.toString(circle.val), circle.x + circle.r - 5, circle.y + circle.r + 5);
        });
        lines.forEach(line -> {
            gc.strokeLine(line.x1, line.y1, line.x2, line.y2);
        });
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Javafx graph");
        stage.show();
    }
}
