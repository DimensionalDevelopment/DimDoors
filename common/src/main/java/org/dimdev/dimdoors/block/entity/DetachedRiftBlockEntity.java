package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.client.RiftCurves;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class DetachedRiftBlockEntity extends RiftBlockEntity {
    private static final RandomSource random = RandomSource.create();

    public int spawnedEndermanId = 0;
    public float riftYaw;
    public int curveID;

    private boolean unregisterDisabled = false;

    public double renderAngle;

    public DetachedRiftBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DETACHED_RIFT, pos, state);
        this.curveID = (int) (Math.random() * RiftCurves.CURVES.size());
        this.riftYaw = random.nextInt(360);
    }

    /**
     * Checks the blocks around the location of the floating rift and applies the decay
     */
    public void applySpreadDecay(ServerLevel world, BlockPos pos) {
//        TODO: Reimplment
//    float chance = size/100f;
//    if ((random.nextFloat()) <= chance) {
//        BlockPos selected = BlockPos.randomInCube(world.getRandom(), 1, pos, (int) (chance)).iterator().next();
//        Decay.decayBlock(world, selected, world.getBlockState(selected), DecaySource.RIFT);
//    }
    }

    public void setClosing(boolean closing) {
        this.closing = closing;
        this.setChanged();
    }

    public void setStabilized(boolean stabilized) {
        this.stabilized = stabilized;
        this.setChanged();
    }

    public int getCurveID() {
        return this.curveID;
    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        super.serialize(nbt);
        nbt.putInt("spawnedEnderManId", this.spawnedEndermanId);
        nbt.putInt("curveID", this.curveID);
        nbt.putFloat("rotation", this.riftYaw);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        this.spawnedEndermanId = nbt.getInt("spawnedEnderManId");
        this.curveID = nbt.getInt("curveID");
        this.riftYaw = nbt.getFloat("rotation");
    }

    @Override
    public boolean isDetached() {
        return true;
    }

    @Override
    public void unregister() {
        if (!this.unregisterDisabled) {
            super.unregister();
        }
    }

    @Override
    public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 velocity, Location location) {
        if (this.level instanceof ServerLevel)
            TeleportUtil.teleport(entity, this.level, this.worldPosition, relativeAngle, velocity);
        return true;
    }

    public void setUnregisterDisabled(boolean unregisterDisabled) {
        this.unregisterDisabled = unregisterDisabled;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public void setLocked(boolean locked) {
        // NO-OP
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putFloat("size", this.size);
        tag.putInt("curveID", this.curveID);
        tag.putFloat("rotation", this.riftYaw);
        return tag;

    }

    @Override
    protected void onClose(Level level, BlockPos pos) {
        level.removeBlock(pos, false);
    }

    @Override
    protected void onUpdate(Level level, BlockPos pos) {
        if (level.isClientSide) return;

        if (level.getEntity(spawnedEndermanId) instanceof EnderMan) {
            return;
        }

        if (random.nextFloat() < DimensionalDoors.getConfig().getGeneralConfig().endermanSpawnChance) {
            if (updateNearestRift()) {
                List<EnderMan> list = level.getEntitiesOfClass(EnderMan.class, new AABB(pos.getX() - 9, pos.getY() - 3, pos.getZ() - 9, pos.getX() + 9, pos.getY() + 3, pos.getZ() + 9));

                if (list.isEmpty()) {
                    EnderMan enderman = EntityType.ENDERMAN.spawn(
                            (ServerLevel) level,
                            pos,
                            MobSpawnType.STRUCTURE);
                    Objects.requireNonNull(enderman).absMoveTo(pos.getX() + 0.5, pos.getY() - 1, pos.getZ() + 0.5, 5, 6);

                    if (random.nextDouble() < DimensionalDoors.getConfig().getGeneralConfig().endermanAggressiveChance) {
                        Player player = level.getNearestPlayer(enderman, 50);
                        if (player != null) {
                            enderman.setTarget(player);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onGrowth(Level level, BlockPos pos) {
        // Logarithmic growth
        for (int n = 0; n < 10; n++) {
            // TODO: growthSize config options
            size += (float) (DimensionalDoors.getConfig().getGeneralConfig().riftGrowthSpeed / (size + 1));
        }


        if (!level.isClientSide() && DimensionalDoors.getConfig().getGeneralConfig().enableRiftDecay) {
            applySpreadDecay((ServerLevel) level, pos);
        }

        setChanged();
    }
}