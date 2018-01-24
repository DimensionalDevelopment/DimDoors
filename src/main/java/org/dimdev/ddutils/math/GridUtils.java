package org.dimdev.ddutils.math;

import lombok.Value;

public final class GridUtils {
    @Value
    public static class GridPos {
        private int x;
        private int z;
    }

    /**
     * Calculates the grid position for a certain element number in the grid.
     *
     * @param num The element's number in the grid
     * @return The location on the grid
     */
    public static GridPos numToPos(int num) { // TODO: alternate the sign on every number to have negative coords too
        // Grows by adding two sides to a square, keeping both x and z positive
        int layer = (int) Math.sqrt(num); // The layer of the square, the innermost being layer 0
        int layerNumber = num - layer * layer; // The number of the spot on that layer
        //                           | First Side   |  Second Side                     |
        int x = layerNumber <= layer ? layer        :  layer - (layerNumber - layer);
        int z = layerNumber <= layer ? layerNumber  :  layer;

        return new GridPos(x, z);
    }

    /**
     * Calculates the element number
     *
     * @param pos The location on the grid
     * @return The location on the grid
     */
    public static int posToNum(GridPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        if (x >= z) { // First side
            return x * x + z; // (number of points in the square x * x) + (z points on the top layer)
        } else { // Second side
            return (z + 1) * z + z - x; // (number of points in the rectangle (z + 1) * z) + (z - x points on the top layer)
        }
    }
}
