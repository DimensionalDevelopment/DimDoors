package ddutils.math;

import lombok.Value;

public class GridUtils {
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
    public static int posToNum(GridPos pos) { // TODO: comments
        int x = pos.getX();
        int z = pos.getZ();
        if (x >= z) {
            return x * x + z;
        } else {
            return (z + 2) * z - z;
        }
    }

    // TODO: add more modes: hexagonal sphere packing and maybe spiral, triangle
}
