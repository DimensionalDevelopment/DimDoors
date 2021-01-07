package org.dimdev.dimdoors.util.math;

public final class GridUtil {
	public static final class GridPos {
		public final int x;
		public final int z;

		public GridPos(int x, int z) {
			this.x = x;
			this.z = z;
		}

		public boolean equals(Object o) {
			if (o == this) return true;
			if (!(o instanceof GridPos)) return false;
			GridPos other = (GridPos) o;
			if (this.x != other.x) return false;
			return this.z == other.z;
		}

		public int hashCode() {
			int PRIME = 59;
			int result = 1;
			result = result * PRIME + this.x;
			result = result * PRIME + this.z;
			return result;
		}

		public String toString() {
			return "GridUtils.GridPos(x=" + this.x + ", z=" + this.z + ")";
		}
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
		int x = layerNumber <= layer ? layer : layer - (layerNumber - layer);
		int z = Math.min(layerNumber, layer);

		return new GridPos(x, z);
	}

	/**
	 * Calculates the element number
	 *
	 * @param pos The location on the grid
	 * @return The location on the grid
	 */
	public static int posToNum(GridPos pos) {
		int x = pos.x;
		int z = pos.z;
		if (x >= z) { // First side
			return x * x + z; // (number of points in the square x * x) + (z points on the top layer)
		} else { // Second side
			return (z + 1) * z + z - x; // (number of points in the rectangle (z + 1) * z) + (z - x points on the top layer)
		}
	}
}
