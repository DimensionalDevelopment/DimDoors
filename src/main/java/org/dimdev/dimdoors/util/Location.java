package org.dimdev.dimdoors.util;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.placer.ColumnPlacer;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.AutoSerializable;
import org.dimdev.annotatednbt.Saved;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;

import java.util.Optional;
import java.util.function.Function;

public class Location implements AutoSerializable {
    public static final Codec<Location> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(World.CODEC.fieldOf("world").forGetter(location -> {
            return location.world;
        }), BlockPos.field_25064.fieldOf("pos").forGetter(location -> {
            return location.pos;
        })).apply(instance, Location::new);
    });

    public final RegistryKey<World> world;
    public final BlockPos pos;

    public Location(RegistryKey<World> world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    public Location(ServerWorld world, int x, int y, int z) {
        this(world, new BlockPos(x, y, z));
    }

    public Location(ServerWorld world, BlockPos pos) {
        this(world.getRegistryKey(), pos);
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
        return getWorld().getBlockState(pos);
    }

    public FluidState getFluidState() {
        return getWorld().getFluidState(pos);
    }

    public BlockEntity getBlockEntity() {
        return getWorld().getBlockEntity(pos);
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
        return world;
    }

    public ServerWorld getWorld() {
        return DimensionalDoorsInitializer.getServer().getWorld(world);
    }
}
