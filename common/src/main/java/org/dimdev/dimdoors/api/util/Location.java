package org.dimdev.dimdoors.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.DimensionalDoors;

public class Location {
	public static final Codec<Location> CODEC = RecordCodecBuilder.create(instance -> instance.group(Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(location -> location.world), BlockPos.CODEC.fieldOf("pos").forGetter(location -> location.pos)).apply(instance, Location::new));

	public final ResourceKey<Level> world;
	public final BlockPos pos;

	public Location(ResourceKey<Level> world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
	}

	public Location(ServerLevel world, int x, int y, int z) {
		this(world, new BlockPos(x, y, z));
	}

	public Location(ServerLevel world, BlockPos pos) {
		this(world.dimension(), pos);
	}

	public int getX() {
		return this.pos.getX();
	}

	public int getY() {
		return this.pos.getY();
	}

	public int getZ() {
		return this.pos.getZ();
	}

	public BlockState getBlockState() {
		return this.getWorld().getBlockState(this.pos);
	}

	public FluidState getFluidState() {
		return this.getWorld().getFluidState(this.pos);
	}

	public BlockEntity getBlockEntity() {
		return this.getWorld().getBlockEntity(this.pos);
	}

	public Holder<Biome> getBiome() {
		return this.getWorld().getBiome(pos);
	}

	public BlockPos getBlockPos() {
		return this.pos;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Location &&
				((Location) obj).world.equals(this.world) &&
				((Location) obj).pos.equals(this.pos);
	}

	@Override
	public int hashCode() {
		return this.world.hashCode() * 31 + this.pos.hashCode();
	}

	public ResourceKey<Level> getWorldId() {
		return this.world;
	}

	public ServerLevel getWorld() {
		return DimensionalDoors.getServer().getLevel(this.world);
	}

	public static CompoundTag toNbt(Location location) {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("world", location.world.location().toString());
		nbt.putIntArray("pos", new int[]{location.getX(), location.getY(), location.getZ()});
		return nbt;
	}

	public static Location fromNbt(CompoundTag nbt) {
		int[] pos = nbt.getIntArray("pos");
		return new Location(
				ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(nbt.getString("world"))),
				new BlockPos(pos[0], pos[1], pos[2])
		);
	}
}
