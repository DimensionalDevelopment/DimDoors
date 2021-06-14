package org.dimdev.dimdoors.api.util.math;

import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Vector;

public final class GridUtil {
	public static final class GridPos {
		public int x;
		public int z;

		public GridPos(int x, int z) {
			this.x = x;
			this.z = z;
		}

		public GridPos(BlockPos pos, int gridSize) {
			this.x = Math.floorDiv(Math.floorDiv(pos.getX(), gridSize), 16);
			this.z = Math.floorDiv(Math.floorDiv(pos.getZ(), gridSize), 16);
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) return true;
			if (!(o instanceof GridPos)) return false;
			GridPos other = (GridPos) o;
			if (this.x != other.x) return false;
			return this.z == other.z;
		}

		@Override
		public int hashCode() {
			int PRIME = 59;
			int result = 1;
			result = result * PRIME + this.x;
			result = result * PRIME + this.z;
			return result;
		}

		@Override
		public String toString() {
			return "GridUtils.GridPos(x=" + this.x + ", z=" + this.z + ")";
		}
	}


	/**
	 * Calculates the grid position for a certain element id in the grid.
	 *
	 * @param id The element's id in the grid
	 * @return The location on the grid
	 */
	public static GridPos idToGridPos(int id) {
		if (id < 0) throw new UnsupportedOperationException("Cannot get GridPos of negative id.");
		GridPos out = id > 8 ? idToGridPos(id / 9) : new GridPos(0, 0);
		int x = out.x * 3;
		int z = out.z * 3;

		long minor = id % 9;
		x += (minor + 1) % 3 - 1;
		z += (minor / 3 + 1) % 3 - 1;

		out.x = x;
		out.z = z;
		return out;
	}


	/**
	 * Calculates the element id
	 *
	 * @param pos The location on the grid
	 * @return The id of the location on the grid
	 */
	public static int gridPosToID(GridPos pos) {
		return convToID(new Vector<>(Arrays.asList(pos.x, pos.z)));
	}

	private static int convToID(Vector<Integer> vector) {
		int x = vector.get(0);
		int z = vector.get(1);

		int id = Math.floorMod(x, 3) + (Math.floorMod(z, 3) * 3);

		x = Math.floorDiv(x + 1, 3);
		z = Math.floorDiv(z + 1, 3);
		if (x != 0 || z != 0) {
			vector.set(0, x);
			vector.set(1, z);
			id += 9 * convToID(vector);
		}
		return id;
	}
}
