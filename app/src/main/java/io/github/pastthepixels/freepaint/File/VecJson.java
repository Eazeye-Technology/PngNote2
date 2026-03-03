package io.github.pastthepixels.freepaint.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

import com.txkj.drawingapp.activity.BookActivity4Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import io.github.pastthepixels.freepaint.Graphics.DrawCanvas;
import io.github.pastthepixels.freepaint.Graphics.DrawPath;
import io.github.pastthepixels.freepaint.Graphics.Point;

public class VecJson {
    public static String colorToHex8(int color) {
        return String.format("#%08X", color);
    }

    public static int hex8ToColor(String hex) {
        if (hex != null && hex.startsWith("#")) {
            return (int)(Long.parseLong(hex.substring(1), 16));
        } else if (hex != null) {
            return (int)(Long.parseLong(hex));
        } else {
            return 0;
        }
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] imageByte = outputStream.toByteArray();
            return Base64.encodeToString(imageByte, Base64.DEFAULT);
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return null;
    }

    public static Bitmap base64ToBitmap(String input) {
        if (input == null) {
            return null;
        }
        try {
            byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return null;
    }

    private final DrawCanvas canvas;
    private String data = "";

    public VecJson(DrawCanvas canvas) {
        this.canvas = canvas;
    }

    public void createJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("width", Float.toString(canvas.documentSize.x));
            obj.put("height", Float.toString(canvas.documentSize.y));
            obj.put("viewport-fill", colorToHex8(canvas.documentColor));
            JSONArray arrPaths = new JSONArray();
            for (DrawPath path : canvas.paths) {
                JSONObject objPath = new JSONObject();

                {
                    JSONArray points = new JSONArray();
                    for (Point p : path.points) {
                        JSONObject objPoint = new JSONObject();
                        objPoint.put("x", p.x);
                        objPoint.put("y", p.y);
                        objPoint.put("color", colorToHex8(p.color));
                        objPoint.put("pressure", p.pressure);
                        objPoint.put("command", p.command.ordinal());
                        points.put(objPoint);
                    }
                    objPath.put("path", points);
                }
                {
                    objPath.put("pointsTextType", path.pointsTextType);
                    objPath.put("pointsText", path.pointsText);
                    objPath.put("pointsBitmap", bitmapToBase64(path.pointsBitmap));
                    objPath.put("pointsTextX", path.pointsTextX);
                    objPath.put("pointsTextY", path.pointsTextY);
                    objPath.put("pointsType", path.pointsType);
                    objPath.put("isBold", path.isBold);
                    objPath.put("isItalics", path.isItalics);
                    objPath.put("isUnderline", path.isUnderline);
                    objPath.put("styleType", path.styleType);
                    objPath.put("pointsTextColor", colorToHex8(path.pointsTextColor));
                    objPath.put("pointsTextSize", path.pointsTextSize);
//                    float[] matrix = new float[9];
//                    path.getMatrix().getValues(matrix);
//                    for (int i = 0; i < 9; ++i) {
//                        objPath.put("matrix" + i, matrix[i]);
//                    }
                    objPath.put("pointsScaleX", path.pointsScaleX);
                    objPath.put("pointsScaleY", path.pointsScaleY);

                    if (path.appearance.fill != -1) {
                        objPath.put("fill", colorToHex8(path.appearance.fill));
                    }
                    if (path.appearance.stroke != -1) {
                        objPath.put("stroke", colorToHex8(path.appearance.stroke));
                    }
                    objPath.put("strokeSize", path.appearance.strokeSize);
                    objPath.put("penType", path.appearance.penType);
                }

                // Done
                arrPaths.put(objPath);
            }
            //Done
            obj.put("paths", arrPaths);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // Closes the tag. We are done.
        this.data = obj.toString();
//        System.out.println(this.data);
        System.out.println("**VecJson.createJson**");
    }

    public void writeFile(OutputStream stream) throws IOException {
        stream.write(data.getBytes(StandardCharsets.UTF_8));
        stream.close();
    }

    public String writeString() {
        return data;
    }

    /**
     * Loads an SVG file from an InputStream and then parses it by calling <code>SVG.parseFile()</code>
     *
     * @param stream The InputStream to parse. The method is designed such that it would be passed from a DrawCanvas with <code>DrawCanvas.loadFile()</code>
     */
    public void loadFile(InputStream stream) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            parseFile(
                    (new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
                            .lines().collect(Collectors.joining("\n"))
            );
        }
    }

    public void parseFile(String data) {
        System.out.println("**VecJson.parseFile**");
//        System.out.println(data);

        // Loads the document.
        JSONObject document;
        try {
            document = new JSONObject(data);

            // Set size/color
            canvas.documentSize.set(
                    Float.parseFloat(document.optString("width")),
                    Float.parseFloat(document.optString("height"))
            );
            if (document.has("viewport-fill")) {
                canvas.documentColor = hex8ToColor(document.optString("viewport-fill"));
            }

            // Add paths
            canvas.paths.clear();

            // Clear path list/history
            canvas.versions.clear();
            canvas.version_index = -1;
            canvas.onVersionChanged();

            JSONArray nodes = document.optJSONArray("paths");
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject element = nodes.optJSONObject(i);

                DrawPath path = new DrawPath(null, DrawPath.POINTS_TYPE_STROKE); //FIXME:???
                path.appearance.stroke = path.appearance.fill = -1; //FIXME:???
                path.isClosed = false; //FIXME:??? element.getString("d").toUpperCase().contains("Z");
                // Points
                path.points = new CopyOnWriteArrayList<Point>();
                JSONArray arrPath = element.optJSONArray("path");
                for (int j = 0; j < arrPath.length(); ++j) {
                    JSONObject objPoint = arrPath.optJSONObject(j);
                    float x = (float) objPoint.optDouble("x");
                    float y = (float) objPoint.optDouble("y");
                    int color = hex8ToColor(objPoint.optString("color"));
                    float pressure = (float) objPoint.optDouble("pressure");
                    int command = objPoint.optInt("command");
                    Point p = new Point(x, y, Point.COMMANDS.values()[command], color);
                    p.pressure = pressure;
                    path.points.add(p);
                }

                path.pointsTextType = element.optInt("pointsTextType", 0);
                path.pointsText = element.optString("pointsText");
                path.pointsBitmap = base64ToBitmap(element.optString("pointsBitmap"));
                path.pointsTextX = (float) element.optDouble("pointsTextX");
                path.pointsTextY = (float) element.optDouble("pointsTextY");
                path.pointsType = element.optInt("pointsType");
                path.isBold = element.optBoolean("isBold");
                path.isItalics = element.optBoolean("isItalics");
                path.isUnderline = element.optBoolean("isUnderline");
                path.styleType = element.optInt("styleType");
                path.pointsTextColor = hex8ToColor(element.optString("pointsTextColor"));
                path.pointsTextSize = (float)element.optDouble("pointsTextSize", 28);

//                float[] matrix = new float[9];
//                boolean full_zero = true;
//                for (int i_ = 0; i_ < 9; ++i_) {
//                    matrix[i_] = (float)element.optDouble("matrix" + i_, 0);
//                    if (matrix[i_] != 0) {
//                        full_zero = false;
//                    }
//                }
//                if (!full_zero) {
//                    path.getMatrix().setValues(matrix);
//                } else {
//                    //path.getMatrix().set(new Matrix());
//                }
                path.pointsScaleX = (float)element.optDouble("pointsScaleX", 1.0);
                path.pointsScaleY = (float)element.optDouble("pointsScaleY", 1.0);

                // Fill/stroke
                if (element.has("fill")) {
                    path.appearance.fill = hex8ToColor(element.optString("fill"));
                }
                if (element.has("stroke")) {
                    path.appearance.stroke = hex8ToColor(element.optString("stroke"));
                }
                // Stroke width
                if (element.has("strokeSize")) {
                    path.appearance.strokeSize = Integer.parseInt(element.optString("strokeSize"));
                }
                if (element.has("penType")) {
                    path.appearance.penType = Integer.parseInt(element.optString("penType"));
                }
                // Done!!
                path.cachePath();
                canvas.paths.add(path);

                // Save everything in the version history
                canvas.versions.add(canvas.cloneDrawPathList(canvas.paths));
                if (!BookActivity4Config.LOAD_OLD_PAGE_NO_UNDO) {
                    canvas.version_index += 1;
                }
                canvas.onVersionChanged();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Invalidate!
        canvas.invalidate();
    }
}
