package org.dimdev.dimdoors.block.entity;

import java.util.Random;

import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.util.RGBA;
import org.dimdev.dimdoors.util.TeleportUtil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;

public class EntranceRiftBlockEntity extends RiftBlockEntity {
    public EntranceRiftBlockEntity() {
        super(ModBlockEntityTypes.ENTRANCE_RIFT);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag nbt) {
        super.fromTag(state, nbt);
        AnnotatedNbt.load(this, nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        AnnotatedNbt.save(this, nbt);
        return nbt;
    }

    @Override
    public boolean teleport(Entity entity) {
        boolean status = super.teleport(entity);

        if (riftStateChanged && !alwaysDelete) {
            markDirty();
        }

        return status;
    }

    @Override
    public boolean receiveEntity(Entity entity, float yawOffset) {
        Vec3d targetPos = Vec3d.ofCenter(pos).add(Vec3d.of(getOrientation().getVector()).multiply(ModConfig.GENERAL.teleportOffset + 0.5));
        TeleportUtil.teleport(entity, world, targetPos, yawOffset);
        return true;
    }

    public Direction getOrientation() {
        return Direction.NORTH; // TODO
    }

    @Environment(EnvType.CLIENT)
    public RGBA[] getColors(int count) {
        Random rand = new Random(31100L);
        float[][] colors = new float[count][];

        for (int i = 0; i < count; i++) {
            colors[i] = getEntranceRenderColor(rand);
        }

        return RGBA.fromFloatArray(colors);
    }

    @Environment(EnvType.CLIENT)
    protected float[] getEntranceRenderColor(Random rand) {
        float red, green, blue;

        if (world.getRegistryKey() == World.NETHER) {
            red = rand.nextFloat() * 0.5F + 0.4F;
            green = rand.nextFloat() * 0.05F;
            blue = rand.nextFloat() * 0.05F;
        } else {
            red = rand.nextFloat() * 0.5F + 0.1F;
            green = rand.nextFloat() * 0.4F + 0.4F;
            blue = rand.nextFloat() * 0.6F + 0.5F;
        }

        return new float[]{red, green, blue, 1};
    }

    @Override
    public boolean isDetached() {
        return false;
    }
}
