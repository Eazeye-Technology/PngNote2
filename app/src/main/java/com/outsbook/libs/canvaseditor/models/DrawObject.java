package com.outsbook.libs.canvaseditor.models;

public class DrawObject {
    private PathAndPaint pathAndPaint;
    private Sticker sticker;
    public enum DrawType {
        PATH,
        STICKER;
    }
    private DrawType drawType;

    public DrawObject(PathAndPaint pathAndPaint, Sticker sticker, DrawType drawType) {
        this.pathAndPaint = pathAndPaint;
        this.sticker = sticker;
        this.drawType = drawType;
    }

    public PathAndPaint getPathAndPaint() {
        return this.pathAndPaint;
    }

    public Sticker getSticker() {
        return this.sticker;
    }

    public DrawType getDrawType() {
        return this.drawType;
    }

    @Override
    public String toString() {
        return "DrawObject(pathAndPaint=" + this.pathAndPaint + ", sticker=" + this.sticker + ", drawType=" + this.drawType + ')';
    }
}
