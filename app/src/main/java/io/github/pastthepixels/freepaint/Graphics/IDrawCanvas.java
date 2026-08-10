package io.github.pastthepixels.freepaint.Graphics;

import android.app.Activity;
import android.content.Context;

import java.util.concurrent.CopyOnWriteArrayList;

import io.github.pastthepixels.freepaint.Tools.SelectionTool;

public interface IDrawCanvas {
    Point mapPoint(float x, float y, float pressure);
    CopyOnWriteArrayList<DrawPath> getPaths();
    Context getContext();
    int getPenType();
    void invalidate();
    void versionBackup();
    Activity getActivity();
    boolean postDelayed(Runnable action, long delayMillis);
    int getWidth();
    int getHeight();
    SelectionTool getSelectionTool();
    boolean getScaleMode();
    boolean getEraserMode();
}
