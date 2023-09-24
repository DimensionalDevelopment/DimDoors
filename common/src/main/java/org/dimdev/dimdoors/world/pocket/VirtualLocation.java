package org.dimdev.dimdoors.world.pocket;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import static net.minecraft.world.level.Level.OVERWORLD;

public class VirtualLocation {
	public static Codec<VirtualLocation> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(virtualLocation -> virtualLocation.world),
					Codec.INT.fieldOf("x").forGetter(virtualLocation -> virtualLocation.x),
					Codec.INT.fieldOf("z").forGetter(virtualLocation -> virtualLocation.z),
					Codec.INT.fieldOf("depth").forGetter(virtualLocation -> virtualLocation.depth)
			).apply(instance, VirtualLocation::new)
	);

	private final ResourceKey<Level> world;
	private final int x;
	private final int z;
	private final int depth;

	public VirtualLocation(ResourceKey<Level> world, int x, int z, int depth) {
		this.world = world;
		this.x = x;
		this.z = z;
		this.depth = depth;
	}

	public static CompoundTag toNbt(VirtualLocation virtualLocation) {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("world", virtualLocation.world.location().toString());
		nbt.putInt("x", virtualLocation.x);
		nbt.putInt("z", virtualLocation.z);
		nbt.putInt("depth", virtualLocation.depth);
		return nbt;
	}

	public static VirtualLocation fromNbt(CompoundTag nbt) {
		return new VirtualLocation(
				ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbt.getString("world"))),
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
			virtualLocation = new VirtualLocation(location.world, location.getX(), location.getZ(), DimensionalDoors.getConfig().getDungeonsConfig().maxDungeonDepth);
		} else if(location.getWorld() != null) {// TODO: nether coordinate transform
			virtualLocation = new VirtualLocation(location.world, location.getX(), location.getY(), 5);
		}

		if (virtualLocation == null) {
			return new VirtualLocation(OVERWORLD, location.getX(), location.getZ(), 5);
		}
		return new VirtualLocation(location.getWorldId(), location.getX(), location.getZ(), virtualLocation.getDepth());
	}

	public Location projectToWorld(boolean acceptLimbo) {
		ServerLevel world = DimensionalDoors.getServer().getLevel(this.world);

		if (!acceptLimbo && ModDimensions.isLimboDimension(world)) {
			world = world.getServer().overworld();
		}

		float spread = DimensionalDoors.getConfig().getGeneralConfig().depthSpreadFactor * this.depth;
		int newX = (int) (this.x + spread * 2 * (Math.random() - 0.5));
		int newZ = (int) (this.z + spread * 2 * (Math.random() - 0.5));
		//BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(newX, 1, newZ));
		BlockPos pos = getTopPos(world, newX, newZ).above();
		return new Location(world, pos);
	}

	public static BlockPos getTopPos(Level world, int x, int z) {
		int topHeight =	world.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z)) // guarantees WorldChunk
				.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
		return new BlockPos(x, topHeight, z);
	}

	public ResourceKey<Level> getWorld() {
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
