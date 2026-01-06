package com.outsbook.libs.canvaseditor.models;

import android.graphics.Paint;
import android.graphics.Path;

public class PathAndPaint {
    private Path path;
    private Paint paint;

    public PathAndPaint(Path path, Paint paint) {
        this.path = path;
        this.paint = paint;
    }

    public Path getPath() {
        return this.path;
    }

    public Paint getPaint() {
        return this.paint;
    }

    @Override
    public String toString() {
        return "PathAndPaint(path=" + this.path + ", paint=" + this.paint + ')';
    }
}
