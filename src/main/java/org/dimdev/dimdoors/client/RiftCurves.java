package org.dimdev.dimdoors.client;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

// https://en.wikipedia.org/wiki/L-system
//
// System: {rules, angle, start_string}
// Rules: rule1:rule_2:...:rule_n
// Rule: from_substring>to_substring
//
// Each iteration, rules are applied to the string. After, string
// is interpreted as commands for a pencil on a sheet of paper:
//   F - move forward by one step
//   + - turn clockwise by angle
//   - - turn counter-clockwise by angle
//   [ - save state (push to stack)
//   ] - restore state (pop from stack)
@Environment(EnvType.CLIENT)
public final class RiftCurves {
    public static final List<PolygonInfo> CURVES = new ArrayList<>();

    public static final String[] TERDRAGON = {"F>+F----F++++F-", "60", "F"};
    public static final String[] DRAGON = {"X>X+YF:Y>FX-Y", "90", "FX"};
    public static final String[] TWINDRAGON = {"X>X+YF:Y>FX-Y", "90", "FX--FX"};
    public static final String[] VORTEX = {"X>X+YF:Y>FX-Y", "90", "FX---FX"};

    static {
        CURVES.add(generate(TERDRAGON, 4));
        CURVES.add(generate(TERDRAGON, 5));
        CURVES.add(generate(TERDRAGON, 7));
        CURVES.add(generate(VORTEX, 9));
        CURVES.add(generate(VORTEX, 10));
        CURVES.add(generate(VORTEX, 11));
        CURVES.add(generate(TWINDRAGON, 7));
        CURVES.add(generate(TWINDRAGON, 8));
        CURVES.add(generate(TWINDRAGON, 9));
        CURVES.add(generate(TWINDRAGON, 10));
        CURVES.add(generate(DRAGON, 8));
        CURVES.add(generate(DRAGON, 9));
        CURVES.add(generate(DRAGON, 10));
        CURVES.add(generate(DRAGON, 11));
    }

    private static PolygonInfo generate(String[] args, int steps) {
        //Parse the rules from the first index
        String[] rules = args[0].split(":");
        HashMap<String, String> lSystemsRule = new HashMap<>();

        for (String rule : rules) {
            String[] parts = rule.split(">");
            lSystemsRule.put(parts[0], parts[1]);
        }

        //get the angle for each turn
        int angle = Integer.parseInt(args[1]);

        //String to hold the output
        //Initialize with starting string
        String output;

        //generate the l-system
        output = generate(args[2], steps, lSystemsRule);

        //get the boundary of the polygon
        List<Point> polygon = getBoundary(convertToPoints(angle, output, steps));

        //replace the boundary of the polygon with a series of points representing triangles for rendering
        return new PolygonInfo(tesselate(polygon));
    }

    /**
     * Takes an unordered list of points comprising a fractal curve and builds a
     * closed polygon around it
     */
    public static List<Point> getBoundary(ArrayList<double[]> input) {
        // store max x and y values to create bounding box
        int maxY = Integer.MIN_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int minX = Integer.MAX_VALUE;

        // store possible singles here
        HashSet<Point> singles = new HashSet<>();

        // list to store confirmed singles and output in the correct order
        ArrayList<Point> output = new ArrayList<>();

        // sort into Hashmaps and hashsets to make contains operations possible,
        // while testing for duplicates
        for (double[] point : input) {
            int xCoord = (int) Math.round(point[0]);
            int yCoord = (int) Math.round(point[1]);

            if (xCoord > maxX) maxX = xCoord;
            if (xCoord < minX) minX = xCoord;
            if (yCoord > maxY) maxY = yCoord;
            if (yCoord < minY) minY = yCoord;

            singles.add(new Point(xCoord, yCoord));
        }

        // find a suitable starting point
        Point startPoint = new Point(minX, minY);
        Point prevPoint = startPoint;

        while (startPoint.y < maxY) {
            if (singles.contains(startPoint)) {
                break;
            }

            startPoint = new Point(startPoint.x, startPoint.y + 1);
        }

        // record the first point so we know where to stop
        final Point firstPoint = startPoint;

        // determine the direction to start searching from
        Point direction = getVector(prevPoint, startPoint);

        //output.add(startPoint);

        // loop around in a clockwise circle, jumping to the next point when we
        // find it and resetting the direction to start seaching from
        // to the last found point. This ensures we always find the next
        // *outside* point
        do {
            // get the next point
            direction = rotateCounterClockwise(direction);
            Point target = new Point(startPoint.x + direction.x, startPoint.y + direction.y);

            // see if that point is part of our fractal curve
            if (singles.contains(target)) {
                if (target.equals(firstPoint)) {
                    output.remove(output.get(output.size() - 1));
                    break;
                }
                // get the vector to start from for the next cycle
                direction = getVector(startPoint, target);

                // prune zero width spikes
                if (output.size() > 1 && output.get(output.size() - 2).equals(target)) {
                    output.remove(output.size() - 1);
                } else {

                    if (output.contains(target) && !target.equals(output.get(0))) {
                        int index = output.indexOf(target);
                        while (output.size() > index) {
                            output.remove(output.size() - 1);
                        }
                    }
                    output.add(target);
                }
                startPoint = target;
            }
        }
        while (!(output.get(output.size() - 1).equals(firstPoint) && output.size() > 1) && output.size() < singles.size());

        return output;
    }

    /**
     * using a point as a 2d vector, normalize it (sorta)
     */
    public static Point getVector(Point origin, Point destination) {
        int[] normals = {origin.x - destination.x, origin.y - destination.y};

        for (int i = 0; i < normals.length; i++) {
            if (normals[i] > 0) {
                normals[i] = 1;
            } else if (normals[i] == 0) {
                normals[i] = 0;
            } else if (normals[i] < 0) {
                normals[i] = -1;
            }
        }
        return new Point(normals[0], normals[1]);
    }

    /**
     * rotate a normal around the origin
     */
    public static Point rotateCounterClockwise(Point previous) {
        return new Point(
                (int) (previous.x * Math.cos(Math.toRadians(90)) - previous.y * Math.sin(Math.toRadians(90))),
                (int) (previous.x * Math.sin(Math.toRadians(90)) + previous.y * Math.cos(Math.toRadians(90)))
        );
    }

    /**
     * Take an l-system string and convert it into a series of points on a
     * cartesian grid. Designed to keep terdragons oriented the same direction
     * regardless of iterations
     */
    public static ArrayList<double[]> convertToPoints(double angle, String system, int generations) {

        // determine the starting point and rotation to begin drawing from
        int rotation = generations % 2 == 0 ? 2 : 4;
        double[] currentState = {(generations + rotation) % 4 * 90, 0, 0};

        // the output for a totally unordered list of points defining the curve
        ArrayList<double[]> output = new ArrayList<>();

        // the stack used to deal with branching l-systems that use [ and ]
        ArrayDeque<double[]> state = new ArrayDeque<>();

        // perform the rules corresponding to each symbol in the l-system
        for (Character ch : system.toCharArray()) {
            double motion = 1;

            // move forward
            if (ch == 'F') {
                currentState[1] -= Math.cos(Math.toRadians(currentState[0])) * motion;
                currentState[2] -= Math.sin(Math.toRadians(currentState[0])) * motion;
                output.add(new double[]{currentState[1], currentState[2]});
            }
            // start branch
            if (ch == '[') {

                state.push(currentState.clone());
            }
            // turn left
            if (ch == '-') {
                currentState = new double[]{(currentState[0] - angle) % 360, currentState[1], currentState[2]};
            }
            // turn right
            if (ch == '+') {
                currentState[0] = (currentState[0] + angle) % 360;
            }
            // end branch and return to previous fork
            if (ch == ']') {
                currentState = state.pop();
            }
        }
        return output;
    }

    /**
     * grow and l-system string based on the rules provided in the args
     */
    public static String generate(String start, int steps, HashMap<String, String> lSystemsRule) {

        while (steps > 0) {
            StringBuilder output = new StringBuilder();

            for (Character ch : start.toCharArray()) {
                // get the rule applicable for the variable
                String data = lSystemsRule.get(ch.toString());

                // handle constants for rule-less symbols
                if (data == null) {
                    data = ch.toString();
                }
                output.append(data);
            }
            steps--;
            start = output.toString();
        }
        return start;
    }

    // a data container class to transmit the important information about the polygon
    public static class PolygonInfo {
        public final ArrayList<Point> points;

        public int maxX;
        public final int maxY;
        public final int minX;
        public final int minY;

        public PolygonInfo(ArrayList<Point> points) {
            int minX, minY, maxX, maxY;
            minX = minY = maxX = maxY = 0;

            // Find bounding box of the polygon
            this.points = points;
            if (points.size() > 0) {
                Point firstPoint = points.get(0);
                minX = firstPoint.x;
                minY = firstPoint.y;
                maxX = firstPoint.x;
                maxY = firstPoint.y;

                for (Point point : points) {
                    if (point.x < minX) minX = point.x;
                    if (point.y < minY) minY = point.y;
                    if (point.x > maxX) maxX = point.x;
                    if (point.y > maxY) maxY = point.y;
                }
            }

            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }

    // TODO: Use the GLU tesselator (GPU) instead of poly2tri (CPU): http://wiki.lwjgl.org/wiki/Using_GLU_Tesselator.html
    public static ArrayList<Point> tesselate(List<Point> polygon) {
        ArrayList<Point> points = new ArrayList<>();

        ArrayList<PolygonPoint> polyPoints = new ArrayList<>();

        for (Point p : polygon) {
            polyPoints.add(new PolygonPoint(p.x, p.y));
        }

        Polygon poly = new Polygon(polyPoints);
        Poly2Tri.triangulate(poly);

        ArrayList<DelaunayTriangle> tris = (ArrayList<DelaunayTriangle>) poly.getTriangles();

        for (DelaunayTriangle tri : tris) {
            for (TriangulationPoint tpoint : tri.points) {
                points.add(new Point((int) tpoint.getX(), (int) tpoint.getY()));
            }
        }
        return points;
    }

    public static class Point {
        public final int x;
        public final int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Point && ((Point) o).x == this.x && ((Point) o).y == this.y;
        }

        @Override
        public int hashCode() {
            return this.x * 31 + this.y;
        }
    }
}
