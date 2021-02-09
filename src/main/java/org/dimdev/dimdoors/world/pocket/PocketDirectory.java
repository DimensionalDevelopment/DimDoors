package org.dimdev.dimdoors.world.pocket;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.util.math.GridUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class PocketDirectory {
	int gridSize; // Determines how much pockets in their dimension are spaced
	int privatePocketSize;
	int publicPocketSize;
	Map<Integer, Pocket> pockets;
	int nextID;
	RegistryKey<World> worldKey;

	public PocketDirectory(RegistryKey<World> worldKey) {
		this.gridSize = DimensionalDoorsInitializer.CONFIG.getPocketsConfig().pocketGridSize;
		this.worldKey = worldKey;
		this.nextID = 0;
		this.pockets = new HashMap<>();
	}

	public static PocketDirectory readFromNbt(String id, CompoundTag tag) {
		PocketDirectory directory = new PocketDirectory(RegistryKey.of(Registry.DIMENSION, new Identifier(id)));

		directory.gridSize = tag.getInt("gridSize");
		directory.privatePocketSize = tag.getInt("privatePocketSize");
		directory.publicPocketSize = tag.getInt("publicPocketSize");

		CompoundTag pocketsTag = tag.getCompound("pockets");
		directory.pockets = pocketsTag.getKeys().stream().collect(Collectors.toMap(Integer::parseInt, a -> Pocket.fromTag(pocketsTag.getCompound(a))));
		directory.nextID = tag.getInt("nextID");

		return directory;
	}

	public CompoundTag writeToNbt() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("gridSize", this.gridSize);
		tag.putInt("privatePocketSize", this.privatePocketSize);
		tag.putInt("publicPocketSize", this.publicPocketSize);

		CompoundTag pocketsTag = new CompoundTag();
		this.pockets.forEach((key, value) -> pocketsTag.put(key.toString(), value.toTag()));
		tag.put("pockets", pocketsTag);
		tag.putInt("nextID", this.nextID);

		return tag;
	}

	/**
	 * Create a new blank pocket.
	 *
	 * @return The newly created pockets
	 */
	public Pocket newPocket() {
		Pocket pocket = null;
		while (pocket == null) pocket = this.newPocket(this.nextID++);
		return pocket;
	}

	/**
	 * Create a new pockets with a specific ID.
	 *
	 * @return The newly created Pocket, or null if that ID is already taken.
	 */
	public Pocket newPocket(int id) {
		if (this.pockets.get(id) != null) return null;
		GridUtil.GridPos pos = this.idToGridPos(id);
		Pocket pocket = new Pocket(id, worldKey, pos.x, pos.z);
		this.pockets.put(id, pocket);
		if (id >= this.nextID) this.nextID = id + 1;
		return pocket;
	}

	public void removePocket(int id) {
		this.pockets.remove(id);
	}

	/**
	 * Gets the pocket with a certain ID, or null if there is no pocket with that ID.
	 *
	 * @return The pocket with that ID, or null if there was no pocket with that ID.
	 */
	public Pocket getPocket(int id) {
		return this.pockets.get(id);
	}

	public GridUtil.GridPos idToGridPos(int id) {
		return GridUtil.numToPos(id);
	}

	public int gridPosToID(GridUtil.GridPos pos) {
		return GridUtil.posToNum(pos);
	}

	/**
	 * Calculates the default BlockPos where a pocket should be based on the ID. Use this only for placing
	 * pockets, and use Pocket.getGridPos() for getting the position
	 *
	 * @param id The ID of the pocket
	 * @return The BlockPos of the pocket
	 */
	public BlockPos idToPos(int id) {
		GridUtil.GridPos pos = this.idToGridPos(id);
		return new BlockPos(pos.x * this.gridSize * 16, 0, pos.z * this.gridSize * 16);
	}

	/**
	 * Calculates the ID of a pocket at a certain BlockPos.
	 *
	 * @param pos The position
	 * @return The ID of the pocket, or -1 if there is no pocket at that location
	 */
	public int posToID(BlockPos pos) {
		return this.gridPosToID(new GridUtil.GridPos(pos.getX() / (this.gridSize * 16), pos.getZ() / (this.gridSize * 16)));
	}

	public Pocket getPocketAt(BlockPos pos) { // TODO: use BlockPos
		return this.getPocket(this.posToID(pos));
	}

	public boolean isWithinPocketBounds(BlockPos pos) {
		Pocket pocket = this.getPocketAt(pos);
		return pocket != null && pocket.isInBounds(pos);
	}

	public int getGridSize() {
		return this.gridSize;
	}

	public int getPrivatePocketSize() {
		return this.privatePocketSize;
	}

	public int getPublicPocketSize() {
		return this.publicPocketSize;
	}

	public Map<Integer, Pocket> getPockets() {
		return this.pockets;
	}

	public int getNextID() {
		return this.nextID;
	}
}


