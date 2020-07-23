package org.dimdev.util;

import org.dimdev.annotatednbt.AutoSerializable;
import org.dimdev.annotatednbt.Saved;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class Location implements AutoSerializable {
    @Saved
    public final ServerWorld world;
    @Saved
    public final BlockPos pos;

    public Location(ServerWorld world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    public Location(ServerWorld world, int x, int y, int z) {
        this(world, new BlockPos(x, y, z));
    }

    public int getX() {
        return pos.getX();
    }

    public int getY() {
        return pos.getY();
    }

    public int getZ() {
        return pos.getZ();
    }

    public BlockState getBlockState() {
        return world.getBlockState(pos);
    }

    public FluidState getFluidState() {
        return world.getFluidState(pos);
    }

    public BlockEntity getBlockEntity() {
        return world.getBlockEntity(pos);
    }

    public BlockPos getBlockPos() {
        return pos;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Location &&
                ((Location) obj).world.equals(world) &&
                ((Location) obj).pos.equals(pos);
    }

    @Override
    public int hashCode() {
        return world.hashCode() * 31 + pos.hashCode();
    }

    public RegistryKey<World> getWorldId() {
        return world.getRegistryKey();
    }
}
