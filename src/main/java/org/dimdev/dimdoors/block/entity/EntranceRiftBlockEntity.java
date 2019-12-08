package org.dimdev.dimdoors.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.util.TeleportUtil;

import java.util.Random;

public class EntranceRiftBlockEntity extends RiftBlockEntity {
    public EntranceRiftBlockEntity() {
        super(ModBlockEntityTypes.ENTRANCE_RIFT);
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
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
    public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) {
        Vec3d targetPos = new Vec3d(pos).add(0.5, 0, 0.5).add(new Vec3d(getOrientation().getVector()).multiply(ModConfig.GENERAL.teleportOffset + 0.5));

        TeleportUtil.teleport(entity, world, targetPos.x, targetPos.y, targetPos.z, entity.yaw + relativeYaw, entity.pitch + relativePitch);
        return true;
    }

    public Direction getOrientation() {
        return Direction.NORTH; // TODO
    }

    @Environment(EnvType.CLIENT)
    public float[][] getColors(int count) {
        Random rand = new Random(31100L);
        float[][] colors = new float[count][];

        for (int i = 0; i < count; i++) {
            colors[i] = getEntranceRenderColor(rand);
        }

        return colors;
    }

    @Environment(EnvType.CLIENT)
    protected float[] getEntranceRenderColor(Random rand) {
        float red, green, blue;

        if (world.dimension.isNether()) {
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
