package org.dimdev.dimdoors.world.pocket;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import dev.onyxstudios.cca.api.v3.component.Component;
import org.dimdev.dimdoors.DimensionalDoorsComponents;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.util.math.GridUtil;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class PocketRegistry implements Component {

	int gridSize; // Determines how much pockets in their dimension are spaced
	int privatePocketSize;
	int publicPocketSize;
	Map<Integer, Pocket> pockets;
	int nextID;

	private ServerWorld world;

	public PocketRegistry() {
		super();
		this.gridSize = ModConfig.INSTANCE.getPocketsConfig().pocketGridSize;

		this.nextID = 0;
		this.pockets = new HashMap<>();
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		this.gridSize = tag.getInt("gridSize");
		this.privatePocketSize = tag.getInt("privatePocketSize");
		this.publicPocketSize = tag.getInt("publicPocketSize");

		CompoundTag pocketsTag = tag.getCompound("pockets");
		this.pockets = pocketsTag.getKeys().stream().collect(Collectors.toMap(Integer::parseInt, a -> Pocket.fromTag(pocketsTag.getCompound(a))));
		this.nextID = tag.getInt("nextID");
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		tag.putInt("gridSize", this.gridSize);
		tag.putInt("privatePocketSize", this.privatePocketSize);
		tag.putInt("publicPocketSize", this.publicPocketSize);

		CompoundTag pocketsTag = new CompoundTag();
		this.pockets.forEach((key, value) -> pocketsTag.put(key.toString(), value.toTag()));
		tag.put("pockets", pocketsTag);
		tag.putInt("nextID", this.nextID);
	}

	public static PocketRegistry getInstance(RegistryKey<World> key) {
		ServerWorld world = DimensionalDoorsInitializer.getWorld(key);

		if (!(ModDimensions.isPocketDimension(world))) {
			throw new UnsupportedOperationException("PocketRegistry is only available for pocket dimensions!");
		}

		PocketRegistry instance = DimensionalDoorsComponents.POCKET_REGISTRY_COMPONENT_KEY.get(world);

		instance.world = world;
		for (Pocket pocket : instance.pockets.values()) {
			pocket.world = key;
		}

		return instance;
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
		Pocket pocket = new Pocket(id, this.world.getRegistryKey(), pos.x, pos.z);
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
