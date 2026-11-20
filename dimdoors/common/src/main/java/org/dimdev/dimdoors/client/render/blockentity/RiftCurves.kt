package org.dimdev.dimdoors.client.render.blockentity

import org.poly2tri.Poly2Tri
import org.poly2tri.geometry.polygon.Polygon
import org.poly2tri.geometry.polygon.PolygonPoint
import java.util.*
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

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
object RiftCurves {
    val CURVES = mutableListOf<PolygonInfo>()

    val TERDRAGON = arrayOf("F>+F----F++++F-", "60", "F")
    val DRAGON = arrayOf("X>X+YF:Y>FX-Y", "90", "FX")
    val TWINDRAGON = arrayOf("X>X+YF:Y>FX-Y", "90", "FX--FX")
    val VORTEX = arrayOf("X>X+YF:Y>FX-Y", "90", "FX---FX")

    init {
        CURVES.add(generate(TERDRAGON, 4))
        CURVES.add(generate(TERDRAGON, 5))
        CURVES.add(generate(TERDRAGON, 7))
        CURVES.add(generate(VORTEX, 9))
        CURVES.add(generate(VORTEX, 10))
        CURVES.add(generate(VORTEX, 11))
        CURVES.add(generate(TWINDRAGON, 7))
        CURVES.add(generate(TWINDRAGON, 8))
        CURVES.add(generate(TWINDRAGON, 9))
        CURVES.add(generate(TWINDRAGON, 10))
        CURVES.add(generate(DRAGON, 8))
        CURVES.add(generate(DRAGON, 9))
        CURVES.add(generate(DRAGON, 10))
        CURVES.add(generate(DRAGON, 11))
    }

    private fun generate(args: Array<String>, steps: Int): PolygonInfo {
        //Parse the rules from the first index
        val rules = args[0].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val lSystemsRule = HashMap<String?, String?>()

        for (rule in rules) {
            val parts = rule.split(">".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            lSystemsRule[parts[0]] = parts[1]
        }

        //get the angle for each turn
        val angle = args[1].toInt()

        //generate the l-system

        //String to hold the output
        //Initialize with starting string
        val output: String = generate(args[2], steps, lSystemsRule)

        //get the boundary of the polygon
        val polygon = getBoundary(convertToPoints(angle.toDouble(), output, steps))

        //replace the boundary of the polygon with a series of points representing triangles for rendering
        return PolygonInfo(tesselate(polygon))
    }

    /**
     * Takes an unordered list of points comprising a fractal curve and builds a
     * closed polygon around it
     */
    fun getBoundary(input: ArrayList<DoubleArray>): MutableList<Point> {
        // store max x and y values to create bounding box
        var maxY = Int.MIN_VALUE
        var maxX = Int.MIN_VALUE
        var minY = Int.MAX_VALUE
        var minX = Int.MAX_VALUE

        // store possible singles here
        val singles = mutableListOf<Point>()

        // list to store confirmed singles and output in the correct order
        val output = ArrayList<Point>()

        // sort into Hashmaps and hashsets to make contains operations possible,
        // while testing for duplicates
        for (point in input) {
            val xCoord = point[0].roundToInt()
            val yCoord = point[1].roundToInt()

            if (xCoord > maxX) maxX = xCoord
            if (xCoord < minX) minX = xCoord
            if (yCoord > maxY) maxY = yCoord
            if (yCoord < minY) minY = yCoord

            singles.add(Point(xCoord, yCoord))
        }

        // find a suitable starting point
        var startPoint = Point(minX, minY)
        val prevPoint = startPoint

        while (startPoint.y < maxY) {
            if (singles.contains(startPoint)) {
                break
            }

            startPoint = Point(startPoint.x, startPoint.y + 1)
        }

        // record the first point so we know where to stop
        val firstPoint = startPoint

        // determine the direction to start searching from
        var direction = getVector(prevPoint, startPoint)

        //output.add(startPoint);

        // loop around in a clockwise circle, jumping to the next point when we
        // find it and resetting the direction to start seaching from
        // to the last found point. This ensures we always find the next
        // *outside* point
        do {
            // get the next point
            direction = rotateCounterClockwise(direction)
            val target = Point(startPoint.x + direction.x, startPoint.y + direction.y)

            // see if that point is part of our fractal curve
            if (singles.contains(target)) {
                if (target == firstPoint) {
                    output.remove(output[output.size - 1])
                    break
                }
                // get the vector to start from for the next cycle
                direction = getVector(startPoint, target)

                // prune zero width spikes
                if (output.size > 1 && output[output.size - 2] == target) {
                    output.removeAt(output.size - 1)
                } else {
                    if (output.contains(target) && target != output[0]) {
                        val index = output.indexOf(target)
                        while (output.size > index) {
                            output.removeAt(output.size - 1)
                        }
                    }
                    output.add(target)
                }
                startPoint = target
            }
        } while (!(output[output.size - 1] == firstPoint && output.size > 1) && output.size < singles.size)

        return output
    }

    /**
     * using a point as a 2d vector, normalize it (sorta)
     */
    fun getVector(origin: Point, destination: Point): Point {
        val normals = intArrayOf(origin.x - destination.x, origin.y - destination.y)

        for (i in normals.indices) {
            if (normals[i] > 0) {
                normals[i] = 1
            } else if (normals[i] == 0) {
                normals[i] = 0
            } else if (normals[i] < 0) {
                normals[i] = -1
            }
        }
        return Point(normals[0], normals[1])
    }

    /**
     * rotate a normal around the origin
     */
    fun rotateCounterClockwise(previous: Point): Point {
        return Point(
            (previous.x * cos(Math.toRadians(90.0)) - previous.y * sin(Math.toRadians(90.0))).toInt(),
            (previous.x * sin(Math.toRadians(90.0)) + previous.y * cos(Math.toRadians(90.0))).toInt()
        )
    }

    /**
     * Take an l-system string and convert it into a series of points on a
     * cartesian grid. Designed to keep terdragons oriented the same direction
     * regardless of iterations
     */
    fun convertToPoints(angle: Double, system: String, generations: Int): ArrayList<DoubleArray> {
        // determine the starting point and rotation to begin drawing from

        val rotation = if (generations % 2 == 0) 2 else 4
        var currentState = doubleArrayOf(((generations + rotation) % 4 * 90).toDouble(), 0.0, 0.0)

        // the output for a totally unordered list of points defining the curve
        val output = ArrayList<DoubleArray>()

        // the stack used to deal with branching l-systems that use [ and ]
        val state = ArrayDeque<DoubleArray>()

        // perform the rules corresponding to each symbol in the l-system
        for (ch in system.toCharArray()) {
            val motion = 1.0

            // move forward
            if (ch == 'F') {
                currentState[1] -= cos(Math.toRadians(currentState[0])) * motion
                currentState[2] -= sin(Math.toRadians(currentState[0])) * motion
                output.add(doubleArrayOf(currentState[1], currentState[2]))
            }
            // start branch
            if (ch == '[') {
                state.push(currentState.clone())
            }
            // turn left
            if (ch == '-') {
                currentState = doubleArrayOf((currentState[0] - angle) % 360, currentState[1], currentState[2])
            }
            // turn right
            if (ch == '+') {
                currentState[0] = (currentState[0] + angle) % 360
            }
            // end branch and return to previous fork
            if (ch == ']') {
                currentState = state.pop()
            }
        }
        return output
    }

    /**
     * grow and l-system string based on the rules provided in the args
     */
    fun generate(start: String, steps: Int, lSystemsRule: HashMap<String?, String?>): String {
        var start = start
        var steps = steps
        while (steps > 0) {
            val output = StringBuilder()

            for (ch in start.toCharArray()) {
                // get the rule applicable for the variable
                var data = lSystemsRule[ch.toString()]

                // handle constants for rule-less symbols
                if (data == null) {
                    data = ch.toString()
                }
                output.append(data)
            }
            steps--
            start = output.toString()
        }
        return start
    }

    // TODO: Use the GLU tesselator (GPU) instead of poly2tri (CPU): http://wiki.lwjgl.org/wiki/Using_GLU_Tesselator.html
    fun tesselate(polygon: MutableList<Point>): MutableList<Point> {
        val points = mutableListOf<Point>()

        val polyPoints = mutableListOf<PolygonPoint>()

        for (p in polygon) {
            polyPoints.add(PolygonPoint(p.x.toDouble(), p.y.toDouble()))
        }

        val poly = Polygon(polyPoints)
        Poly2Tri.triangulate(poly)

        val tris = poly.triangles

        for (tri in tris) {
            for (tpoint in tri.points) {
                points.add(Point(tpoint.x.toInt(), tpoint.y.toInt()))
            }
        }
        return points
    }

    // a data container class to transmit the important information about the polygon
    class PolygonInfo(val points: MutableList<Point>) {

        var maxX: Int
        val maxY: Int
        val minX: Int
        val minY: Int

        init {
            var minX: Int
            var minY: Int
            var maxX: Int
            var maxY: Int
            maxY = 0
            maxX = 0
            minY = 0
            minX = 0

            // Find bounding box of the polygon
            if (points.isNotEmpty()) {
                val firstPoint = points[0]
                minX = firstPoint.x
                minY = firstPoint.y
                maxX = firstPoint.x
                maxY = firstPoint.y

                for (point in points) {
                    if (point.x < minX) minX = point.x
                    if (point.y < minY) minY = point.y
                    if (point.x > maxX) maxX = point.x
                    if (point.y > maxY) maxY = point.y
                }
            }

            this.minX = minX
            this.minY = minY
            this.maxX = maxX
            this.maxY = maxY
        }
    }


    data class Point(val x: Int, val y: Int)
}
