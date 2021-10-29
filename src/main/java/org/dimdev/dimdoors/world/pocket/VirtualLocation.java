package org.dimdev.dimdoors.world.pocket;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.Chunk;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.world.ModDimensions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import static net.minecraft.world.World.OVERWORLD;

public class VirtualLocation {
	public static Codec<VirtualLocation> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					World.CODEC.fieldOf("world").forGetter(virtualLocation -> virtualLocation.world),
					Codec.INT.fieldOf("x").forGetter(virtualLocation -> virtualLocation.x),
					Codec.INT.fieldOf("z").forGetter(virtualLocation -> virtualLocation.z),
					Codec.INT.fieldOf("depth").forGetter(virtualLocation -> virtualLocation.depth)
			).apply(instance, VirtualLocation::new)
	);

	private final RegistryKey<World> world;
	private final int x;
	private final int z;
	private final int depth;

	public VirtualLocation(RegistryKey<World> world, int x, int z, int depth) {
		this.world = world;
		this.x = x;
		this.z = z;
		this.depth = depth;
	}

	public static NbtCompound toNbt(VirtualLocation virtualLocation) {
		NbtCompound nbt = new NbtCompound();
		nbt.putString("world", virtualLocation.world.getValue().toString());
		nbt.putInt("x", virtualLocation.x);
		nbt.putInt("z", virtualLocation.z);
		nbt.putInt("depth", virtualLocation.depth);
		return nbt;
	}

	public static VirtualLocation fromNbt(NbtCompound nbt) {
		return new VirtualLocation(
				RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("world"))),
				nbt.getInt("x"),
				nbt.getInt("z"),
				nbt.getInt("depth")
		);
	}

	public static VirtualLocation fromLocation(Location location) {
		VirtualLocation virtualLocation = null;

		if (ModDimensions.isPocketDimension(location.world)) {
			Pocket pocket = DimensionalRegistry.getPocketDirectory(location.world).getPocketAt(location.pos);
			if (pocket != null) {
				virtualLocation = pocket.virtualLocation; // TODO: pockets-relative coordinates
			} else {
				virtualLocation = null; // TODO: door was placed in a pockets dim but outside of a pockets...
			}
		} else if (ModDimensions.isLimboDimension(location.getWorld())) { // TODO: convert to interface on worldprovider
			virtualLocation = new VirtualLocation(location.world, location.getX(), location.getZ(), DimensionalDoorsInitializer.getConfig().getDungeonsConfig().maxDungeonDepth);
		} // TODO: nether coordinate transform

		if (virtualLocation == null) {
			return new VirtualLocation(OVERWORLD, location.getX(), location.getZ(), 5);
		}
		return new VirtualLocation(location.getWorldId(), location.getX(), location.getZ(), virtualLocation.getDepth());
	}

	public Location projectToWorld(boolean acceptLimbo) {
		ServerWorld world = DimensionalDoorsInitializer.getServer().getWorld(this.world);

		if (!acceptLimbo && ModDimensions.isLimboDimension(world)) {
			world = world.getServer().getWorld(OVERWORLD);
		}

		float spread = DimensionalDoorsInitializer.getConfig().getGeneralConfig().depthSpreadFactor * this.depth;
		int newX = (int) (this.x + spread * 2 * (Math.random() - 0.5));
		int newZ = (int) (this.z + spread * 2 * (Math.random() - 0.5));
		//BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(newX, 1, newZ));
		BlockPos pos = getTopPos(world, newX, newZ).up();
		return new Location(world, pos);
	}

	public static BlockPos getTopPos(World world, int x, int z) {
		int topHeight =	world.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z)) // guarantees WorldChunk
				.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x, z);
		return new BlockPos(x, topHeight, z);
	}

	public RegistryKey<World> getWorld() {
		return this.world;
	}

	public int getX() {
		return this.x;
	}

	public int getZ() {
		return this.z;
	}

	public int getDepth() {
		return this.depth;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("world", this.world)
				.add("x", this.x)
				.add("z", this.z)
				.add("depth", this.depth)
				.toString();
	}
}
