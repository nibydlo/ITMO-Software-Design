package com.krylov.software.design.graphs.drawing;

public interface DrawingApi {
    public long getDrawingAresWidth();

    public long getDrawingAreaHeight();

    public void drawCircle(int x, int y, int r, int val);

    public void drawLine(int x1, int y1, int x2, int y2);

    public void showPicture();
}
