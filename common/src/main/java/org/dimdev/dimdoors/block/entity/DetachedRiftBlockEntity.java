package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import org.dimdev.dimdoors.compat.sable.SableHelper;
import org.dimdev.dimdoors.util.Utils;
import org.dimdev.dimdoors.world.decay.Decay;
import org.dimdev.dimdoors.world.decay.DecaySource;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class DetachedRiftBlockEntity extends RiftBlockEntity {
    public static final float DECAY_RADIUS_DIVISOR = 40f;

    public int spawnedEndermanId = 0;
    public float riftYaw;
    public int curveID;
    private int weight;
    private int updateTimer;

    public double renderAngle;

    public DetachedRiftBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DETACHED_RIFT, pos, state);
        this.curveID = (int) (Math.random() * RiftCurves.CURVES.size());
        this.riftYaw = (float) (Math.random() * 360);
        this.assignWeight(5);
    }

    /**
     * Checks the blocks around the location of the floating rift and applies the decay
     */
    public void applySpreadDecay(ServerLevel world, BlockPos pos) {
        int radius = getDecayRadius();
        if(radius <= 0) return;

        BlockPos selected = Utils.randomInSphere(world.getRandom(), 1, pos, radius).iterator().next();
        if (selected.equals(pos)) return;

        Decay.decayBlock(world, pos, world.getBlockState(pos), selected, world.getBlockState(selected), DecaySource.RIFT);
    }

    public void setClosing() {
        this.setWeight(-100);
    }

    public void setStabilized() {
        this.setWeight(0);
    }

    public int getWeight() {
        return weight;
    }

    public int getDecayRadius() {
        var size = getData().getSize();
        return size > 0 ? Mth.ceil(size / DECAY_RADIUS_DIVISOR) : 0;
    }

    public void setWeight(int weight) {
        this.assignWeight(weight);
        this.setChanged();
    }

    private void assignWeight(int weight) {
        this.weight = Mth.clamp(weight, -100, 100);
    }

    public int getCurveID() {
        return this.curveID;
    }

    @Override
    public void gatherDebug(Consumer<Component> textConsumer) {
        super.gatherDebug(textConsumer);
        textConsumer.accept(Component.literal("Decay radius: " + this.getDecayRadius()));
    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        super.serialize(nbt);
        nbt.putInt("spawnedEnderManId", this.spawnedEndermanId);
        nbt.putInt("curveID", this.curveID);
        nbt.putFloat("rotation", this.riftYaw);
        nbt.putInt("weight", this.weight);

        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        this.spawnedEndermanId = nbt.getInt("spawnedEnderManId");
        this.curveID = nbt.getInt("curveID");
        this.riftYaw = nbt.getFloat("rotation");

        if (nbt.contains("weight", Tag.TAG_ANY_NUMERIC)) {
            this.assignWeight(nbt.getInt("weight"));
        } else if (nbt.contains("closing", Tag.TAG_ANY_NUMERIC) || nbt.contains("stablized", Tag.TAG_ANY_NUMERIC)) {
            if (nbt.getBoolean("stablized")) this.assignWeight(0);
            if (nbt.getBoolean("closing")) this.assignWeight(-100);
        }
    }

    @Override
    public boolean isDetached() {
        return true;
    }

    @Override
    public void unregister() {
        super.unregister();
        level.removeBlock(getBlockPos(), false);
    }

    @Override
    public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 velocity, Location location) {
        if (this.level instanceof ServerLevel serverLevel) {
            Vec3 localTargetPos = Vec3.atBottomCenterOf(this.worldPosition);

            var frame = SableHelper.INSTANCE.projectTeleportFrame(serverLevel, location, localTargetPos, relativeAngle, velocity);

            TeleportUtil.teleport(entity, this.level, frame.pos(), frame.angle(), frame.velocity());
        }
        return true;
    }

    public void update(Level level, BlockPos pos, BlockState blockState) {
        if (weight != 0) {
            var absoluteChance = Math.abs(weight);

            if (updateTimer % 20 == 0) {
                if (level.random.nextInt(0, 100) <= absoluteChance) {
                    var sizeChange = weight > 0 ? 1 : -1;

                    getData().setSize(getData().getSize() + sizeChange);
                }

                if (weight < 0 && getData().getSize() == 0) {
                    unregister();
                    return;
                }

                updateTimer = 0;
                sync();
            }



            if (DimensionalDoors.getConfig().getGeneralConfig().enableRiftDecay /*&& level.random.nextInt(0, 100) <= weight*/ && getData().getSize() > 0) {
                applySpreadDecay((ServerLevel) level, pos);
            }

            tryEndermanSpawn(level, pos);



            updateTimer++;
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putInt("weight", this.weight);
        tag.putInt("curveID", this.curveID);
        tag.putFloat("rotation", this.riftYaw);
        return tag;

    }

    private void tryEndermanSpawn(Level level, BlockPos pos) {
        if (level.getEntity(spawnedEndermanId) instanceof EnderMan) {
            return;
        }

        if (level.random.nextFloat() < DimensionalDoors.getConfig().getGeneralConfig().endermanSpawnChance) {

            List<EnderMan> list = level.getEntitiesOfClass(EnderMan.class, new AABB(pos.getX() - 9, pos.getY() - 3, pos.getZ() - 9, pos.getX() + 9, pos.getY() + 3, pos.getZ() + 9));

            if (list.isEmpty()) {
                EnderMan enderman = EntityType.ENDERMAN.spawn(
                        (ServerLevel) level,
                        pos,
                        MobSpawnType.STRUCTURE);
                Objects.requireNonNull(enderman).absMoveTo(pos.getX() + 0.5, pos.getY() - 1, pos.getZ() + 0.5, 5, 6);

                if (level.random.nextDouble() < DimensionalDoors.getConfig().getGeneralConfig().endermanAggressiveChance) {
                    Player player = level.getNearestPlayer(enderman, 50);
                    if (player != null) {
                        enderman.setTarget(player);
                    }
                }
            }
        }
    }
}
