package org.dimdev.dimdoors.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MaskEntity extends PathfinderMob {

    private static final EntityDataAccessor<Byte> DATA_MODE = SynchedEntityData.defineId(MaskEntity.class, EntityDataSerializers.BYTE);

    private static final byte MODE_GUARD = 0;
    private static final byte MODE_PATROL = 1;
    private static final byte MODE_WANDER = 2;
    private static final byte MODE_CHASE = 3;
    private static final byte MODE_SPOTTING = 4;

    // Detection/acquisition range (used by canSeePlayer / targeting)
    private static final double MAX_DETECTION_DISTANCE_SQ = 36.0;

    // Chase leash range (used only to abort chase + teleport home)
    private static final double TELEPORT_BACK_DISTANCE_SQ = 2500.0;

    private static final int SPOTTING_DURATION_TICKS = 33;
    private static final double CATCH_DISTANCE_SQ = 2.25;
    private static final int CHASE_BLOCK_BREAK_INTERVAL = 6;
    private static final double BLOCK_BREAK_PROBE_DISTANCE = 0.8;
    private static final int GUARD_WALL_CHECK_DISTANCE = 3;
    private static final int GUARD_CANDIDATE_ATTEMPTS = 8;
    private static final int WANDER_WALL_CLEARANCE = 2;
    private static final int WANDER_CANDIDATE_ATTEMPTS = 8;
    private static final int PATROL_BLOCK_BREAK_INTERVAL = 6;
    private static final double PATROL_BLOCK_BREAK_PROBE_DISTANCE = 0.6;
    private static final double CHASE_MAX_HORIZONTAL_SPEED = 0.12;
    private static final double CHASE_ACCEL_PER_TICK = 0.01;
    private static final int PASSIVE_SCAN_INTERVAL_TICKS = 10;
    private static final int LOST_SIGHT_GIVE_UP_TICKS = 60;
    private static final double ALERT_RADIUS_SQ = 100.0;

    private int lostSightTicks = 0;
    private int spottingLostSightTicks = 0;
    private static final int SPOTTING_LOST_SIGHT_CANCEL_TICKS = 10;

    public final AnimationState idleState = new AnimationState();
    public final AnimationState spottedState = new AnimationState();

    private int spottingTicks = 0;
    private int chaseTicks = 0;
    private static final int MAX_CHASE_TICKS = 400;
    private int passiveScanTicks = 0;

    private BlockPos homePos;
    private BlockPos patrolTargetA;
    private BlockPos patrolTargetB;
    private int patrolIndex = 0;
    private byte resumeMode = MODE_GUARD;
    private int patrolPauseTicks = 0;
    private static final int PATROL_PAUSE_DURATION_TICKS = 40;

    protected MaskEntity(EntityType<? extends MaskEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new MaskMoveControl(this);
        this.setNoGravity(true);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_CAUTIOUS, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_OTHER, -1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MODE, MODE_GUARD);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.FLYING_SPEED, 0.28);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 50.0F));
        this.goalSelector.addGoal(2, new MaskMovementGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            spottedState.animateWhen(getMode() == MODE_SPOTTING, this.tickCount);
            idleState.animateWhen(getMode() != MODE_SPOTTING, this.tickCount);
            return;
        }

        if (getMode() == MODE_GUARD || getMode() == MODE_PATROL || getMode() == MODE_WANDER) {
            passiveScanTicks++;

            if (passiveScanTicks >= PASSIVE_SCAN_INTERVAL_TICKS) {
                passiveScanTicks = 0;

                if (this.getTarget() == null) {
                    Player nearest = findNearestDetectablePlayer();
                    if (nearest != null) {
                        this.setTarget(nearest);
                        alertNearbyMasks(nearest);
                    }
                }
            }
        }

        if (getMode() == MODE_SPOTTING) {
            LivingEntity target = this.getTarget();

            if (!(target instanceof Player player)) {
                super.setTarget(null);
                spottingTicks = 0;
                spottingLostSightTicks = 0;
                lostSightTicks = 0;
                passiveScanTicks = 0;
                setMode(resumeMode);
                return;
            }

            if (player.isCreative() || player.isSpectator()) {
                super.setTarget(null);
                spottingTicks = 0;
                spottingLostSightTicks = 0;
                lostSightTicks = 0;
                passiveScanTicks = 0;
                setMode(resumeMode);
                return;
            }

            faceTargetInstantly(player);

            boolean visible = canSeePlayer(player);
            if (visible) {
                spottingLostSightTicks = 0;
            } else {
                spottingLostSightTicks++;
            }

            if (spottingLostSightTicks > SPOTTING_LOST_SIGHT_CANCEL_TICKS) {
                super.setTarget(null);
                spottingTicks = 0;
                spottingLostSightTicks = 0;
                lostSightTicks = 0;
                passiveScanTicks = 0;
                setMode(resumeMode);
                return;
            }

            if (spottingTicks >= SPOTTING_DURATION_TICKS) {
                Vec3 v = this.getDeltaMovement();
                this.setDeltaMovement(0.0, v.y, 0.0);
                chaseTicks = 0;
                lostSightTicks = 0;
                passiveScanTicks = 0;
                setMode(MODE_CHASE);
                spottingTicks = 0;
                spottingLostSightTicks = 0;
                return;
            }

            spottingTicks++;
            return;
        }

        if (getMode() == MODE_CHASE) {
            chaseTicks++;

            Player nearest = findNearestChasePlayer();
            if (nearest != null && nearest != this.getTarget()) {
                super.setTarget(nearest);
            }

            Player target = this.getTarget() instanceof Player p ? p : null;
            if (target == null) {
                resetChase();
                return;
            }

            if (target.isCreative() || target.isSpectator()) {
                resetChase();
                return;
            }

            faceTargetInstantly(target);

            boolean insideSolid = isChaseInsideSolid();
            boolean visible = canSeePlayer(target);

            if (visible || insideSolid) {
                lostSightTicks = 0;
            } else {
                lostSightTicks++;
            }

            if (lostSightTicks > LOST_SIGHT_GIVE_UP_TICKS || chaseTicks > MAX_CHASE_TICKS || this.distanceToSqr(target) > TELEPORT_BACK_DISTANCE_SQ) {
                resetChase();
                return;
            }

            if (this.distanceToSqr(target) < CATCH_DISTANCE_SQ) {
                catchPlayer(target);
                return;
            }

            if (insideSolid && this.tickCount % 5 == 0) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.STONE_HIT, SoundSource.HOSTILE, 0.5F, 0.8F);
            }
        }
    }

    private void resetChase() {
        super.setTarget(null);
        spottingTicks = 0;
        spottingLostSightTicks = 0;
        chaseTicks = 0;
        lostSightTicks = 0;
        patrolPauseTicks = 0;
        passiveScanTicks = 0;
        setMode(resumeMode);

        if (homePos != null) {
            this.teleportTo(homePos.getX() + 0.5, homePos.getY() + 1.01, homePos.getZ() + 0.5);
        }

        if (this.getMoveControl() instanceof MaskMoveControl moveControl) {
            moveControl.setToWait();
        }

        Vec3 v = this.getDeltaMovement();
        this.setDeltaMovement(0.0, v.y, 0.0);
    }

    public void configurePassiveMode(byte mode, BlockPos homePos) {
        this.homePos = homePos;
        this.patrolPauseTicks = 0;

        if (mode == MODE_GUARD || mode == MODE_WANDER) {
            this.resumeMode = mode;
            this.setMode(mode);
        } else {
            this.resumeMode = MODE_GUARD;
            this.setMode(MODE_GUARD);
        }
    }

    public void configurePatrol(BlockPos homePos, BlockPos patrolTargetA, BlockPos patrolTargetB) {
        this.homePos = homePos;
        this.patrolTargetA = patrolTargetA;
        this.patrolTargetB = patrolTargetB;
        this.patrolIndex = 0;
        this.patrolPauseTicks = 0;
        this.resumeMode = MODE_PATROL;
        this.setMode(MODE_PATROL);
    }

    private void tryBreakBlockInPath() {
        Vec3 movement = this.getDeltaMovement();
        if (movement.lengthSqr() < 1.0E-6) return;

        Vec3 dir = movement.normalize().scale(BLOCK_BREAK_PROBE_DISTANCE);

        BlockPos pos = BlockPos.containing(position().add(dir));
        BlockState state = this.level().getBlockState(pos);
        if (!state.isAir() && state.getDestroySpeed(this.level(), pos) >= 0) {
            this.level().destroyBlock(pos, true, this);
            this.level().playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.9F, 0.9F + this.random.nextFloat() * 0.2F);
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 12,
                        0.3, 0.3, 0.3, 0.08);
            }
        }
    }

    private boolean canSeePlayer(LivingEntity target) {
        if (!(target instanceof Player player) || player.isCreative() || player.isSpectator()) return false;
        double distSq = this.distanceToSqr(player);
        if (distSq > MAX_DETECTION_DISTANCE_SQ) return false;
        Vec3 eye = this.getEyePosition();
        Vec3 targetEye = player.getEyePosition();
        HitResult hit = this.level().clip(new ClipContext(eye, targetEye, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hit.getType() == HitResult.Type.BLOCK) return false;
        return true;
    }

    private void catchPlayer(Player player) {
        ItemStack mask = new ItemStack(Items.NETHERITE_HELMET);
        ItemStack oldHelmet = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
        player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, mask);
        if (!oldHelmet.isEmpty()) {
            player.drop(oldHelmet, true, false);
        }
        this.level().broadcastEntityEvent(player, (byte) 35);
        this.level().playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 0.6F);
        this.remove(RemovalReason.DISCARDED);
    }

    private void triggerSpotted() {
        spottingTicks = 0;
        spottingLostSightTicks = 0;
        chaseTicks = 0;
        lostSightTicks = 0;
        passiveScanTicks = 0;

        if (this.getMoveControl() instanceof MaskMoveControl moveControl) {
            moveControl.setToWait();
        }

        Vec3 v = this.getDeltaMovement();
        this.setDeltaMovement(0.0, v.y, 0.0);

        setMode(MODE_SPOTTING);
    }

    public byte getMode() {
        return this.entityData.get(DATA_MODE);
    }

    public void setPassiveMode(byte mode) {
        if (mode == MODE_GUARD || mode == MODE_PATROL || mode == MODE_WANDER) {
            this.resumeMode = mode;
            this.setMode(mode);
        } else {
            this.resumeMode = MODE_GUARD;
            this.setMode(MODE_GUARD);
        }
    }

    public void setMode(byte mode) {
        byte prev = getMode();
        this.entityData.set(DATA_MODE, mode);

        if (prev != mode) {
            passiveScanTicks = 0;
        }

        if (mode == MODE_PATROL && prev != MODE_PATROL) {
            patrolPauseTicks = 0;
        }

        this.setGlowingTag(mode == MODE_CHASE);
        this.noPhysics = mode == MODE_CHASE;
    }

    private void alertNearbyMasks(Player player) {
        for (MaskEntity other : this.level().getEntitiesOfClass(MaskEntity.class, this.getBoundingBox().inflate(10.0))) {
            if (other == this) {
                continue;
            }

            if (this.distanceToSqr(other) <= ALERT_RADIUS_SQ) {
                other.setTarget(player);
            }
        }
    }

    private void startImmediateChase(Player player) {
        if (player == null || player.isCreative() || player.isSpectator()) {
            return;
        }

        byte currentMode = getMode();
        if (currentMode == MODE_GUARD || currentMode == MODE_PATROL || currentMode == MODE_WANDER) {
            resumeMode = currentMode;
        }

        super.setTarget(player);

        spottingTicks = 0;
        spottingLostSightTicks = 0;
        chaseTicks = 0;
        lostSightTicks = 0;
        passiveScanTicks = 0;

        if (this.getMoveControl() instanceof MaskMoveControl moveControl) {
            moveControl.setToWait();
        }

        this.getLookControl().setLookAt(player, 10.0F, 10.0F);
        setMode(MODE_CHASE);
    }

    private Player findNearestPlayerIgnoringLineOfSight(double maxDistSq) {
        Player best = null;
        double bestDistSq = maxDistSq;

        for (Player player : this.level().players()) {
            if (player.isCreative() || player.isSpectator()) {
                continue;
            }

            double distSq = this.distanceToSqr(player);
            if (distSq <= bestDistSq) {
                bestDistSq = distSq;
                best = player;
            }
        }

        return best;
    }

    private Player findNearestChasePlayer() {
        return findNearestPlayerIgnoringLineOfSight(TELEPORT_BACK_DISTANCE_SQ);
    }

    private boolean isChaseInsideSolid() {
        BlockPos bodyPos = this.blockPosition();
        BlockPos eyePos = BlockPos.containing(this.getEyePosition());

        BlockState bodyState = this.level().getBlockState(bodyPos);
        if (!bodyState.isAir() && bodyState.isSolid()) {
            return true;
        }

        BlockState eyeState = this.level().getBlockState(eyePos);
        return !eyeState.isAir() && eyeState.isSolid();
    }

    @Override
    public void setTarget(LivingEntity target) {
        super.setTarget(target);

        if (target != null) {
            byte currentMode = getMode();
            if (currentMode != MODE_SPOTTING && currentMode != MODE_CHASE) {
                if (currentMode == MODE_GUARD || currentMode == MODE_PATROL || currentMode == MODE_WANDER) {
                    resumeMode = currentMode;
                } else {
                    resumeMode = MODE_GUARD;
                }
                triggerSpotted();
            }

            getLookControl().setLookAt(target, 10.0F, 10.0F);
        }
    }

    private void faceTargetInstantly(LivingEntity target) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();

        if (dx * dx + dz * dz < 1.0E-6) {
            return;
        }

        float yRot = (float)(Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;

        this.setYRot(yRot);
        this.yRotO = yRot;
        this.yBodyRot = yRot;
        this.yBodyRotO = yRot;
        this.yHeadRot = yRot;
        this.yHeadRotO = yRot;
    }

    @Override
    public SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level,
                                        net.minecraft.world.DifficultyInstance difficulty,
                                        net.minecraft.world.entity.MobSpawnType reason,
                                        SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData);
        if (this.homePos == null) {
            this.homePos = this.blockPosition();
        }
        return data;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("Mode", getMode());
        if (homePos != null) tag.putLong("HomePos", homePos.asLong());
        if (patrolTargetA != null) tag.putLong("PatrolA", patrolTargetA.asLong());
        if (patrolTargetB != null) tag.putLong("PatrolB", patrolTargetB.asLong());
        tag.putInt("PatrolIndex", patrolIndex);
        tag.putByte("ResumeMode", resumeMode);
        tag.putInt("PatrolPauseTicks", patrolPauseTicks);
        tag.putInt("SpottingTicks", spottingTicks);
        tag.putInt("ChaseTicks", chaseTicks);
        tag.putInt("PassiveScanTicks", passiveScanTicks);
        tag.putInt("LostSightTicks", lostSightTicks);
        tag.putInt("SpottingLostSightTicks", spottingLostSightTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setMode(tag.getByte("Mode"));
        if (tag.contains("HomePos")) homePos = BlockPos.of(tag.getLong("HomePos"));
        if (tag.contains("PatrolA")) patrolTargetA = BlockPos.of(tag.getLong("PatrolA"));
        if (tag.contains("PatrolB")) patrolTargetB = BlockPos.of(tag.getLong("PatrolB"));
        patrolIndex = tag.getInt("PatrolIndex");
        if (tag.contains("ResumeMode")) resumeMode = tag.getByte("ResumeMode");
        if (tag.contains("PatrolPauseTicks")) patrolPauseTicks = tag.getInt("PatrolPauseTicks");
        if (tag.contains("SpottingTicks")) spottingTicks = tag.getInt("SpottingTicks");
        if (tag.contains("ChaseTicks")) chaseTicks = tag.getInt("ChaseTicks");
        if (tag.contains("PassiveScanTicks")) passiveScanTicks = tag.getInt("PassiveScanTicks");
        if (tag.contains("LostSightTicks")) lostSightTicks = tag.getInt("LostSightTicks");
        if (tag.contains("SpottingLostSightTicks")) spottingLostSightTicks = tag.getInt("SpottingLostSightTicks");

        if (homePos == null) {
            homePos = this.blockPosition();
        }
    }

    private Player findNearestDetectablePlayer() {
        Player best = null;
        double bestDistSq = MAX_DETECTION_DISTANCE_SQ;

        for (Player player : this.level().players()) {
            if (!canSeePlayer(player)) {
                continue;
            }

            double distSq = this.distanceToSqr(player);
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                best = player;
            }
        }

        return best;
    }

    class MaskMovementGoal extends Goal {
        private final MaskEntity mask;

        MaskMovementGoal(MaskEntity mask) {
            this.mask = mask;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        private void executeChase() {
            LivingEntity target = mask.getTarget();
            if (target == null) {
                return;
            }

            double dx = target.getX() - mask.getX();
            double dz = target.getZ() - mask.getZ();
            double horizontalSq = dx * dx + dz * dz;

            if (horizontalSq < 1.0E-6) {
                return;
            }

            Vec3 toTarget = new Vec3(dx, 0.0, dz);

            boolean insideSolid = mask.isChaseInsideSolid();
            double accelScale = insideSolid ? 0.35 : 1.0;
            double speedCap = insideSolid ? (CHASE_MAX_HORIZONTAL_SPEED * 0.35) : CHASE_MAX_HORIZONTAL_SPEED;

            Vec3 accel = toTarget.normalize().scale(CHASE_ACCEL_PER_TICK * accelScale);
            Vec3 vel = mask.getDeltaMovement().add(accel);

            double vx = vel.x;
            double vz = vel.z;
            double hSq = vx * vx + vz * vz;

            if (hSq > speedCap * speedCap) {
                double inv = speedCap / Math.sqrt(hSq);
                vel = new Vec3(vx * inv, 0.0, vz * inv);
            } else {
                vel = new Vec3(vel.x, 0.0, vel.z);
            }

            mask.setDeltaMovement(vel);
        }

        private void executeGuard() {
            if (homePos != null && mask.tickCount % 60 == 0) {
                for (int attempt = 0; attempt < GUARD_CANDIDATE_ATTEMPTS; attempt++) {
                    double dx = (mask.random.nextDouble() - 0.5) * 3;
                    double dy = (mask.random.nextDouble() - 0.5) * 1;
                    double dz = (mask.random.nextDouble() - 0.5) * 3;

                    double x = homePos.getX() + dx;
                    double y = homePos.getY() + dy;
                    double z = homePos.getZ() + dz;

                    if (isWallTooCloseInFront(x, y, z)) {
                        continue;
                    }

                    mask.getMoveControl().setWantedPosition(x, y, z, 0.15);

                    return;
                }
            }
        }

        private void executeWander() {
            if (mask.random.nextInt(30) == 0) {
                for (int attempt = 0; attempt < WANDER_CANDIDATE_ATTEMPTS; attempt++) {
                    double dx = (mask.random.nextDouble() - 0.5) * 20;
                    double dy = (mask.random.nextDouble() - 0.5) * 5;
                    double dz = (mask.random.nextDouble() - 0.5) * 20;

                    double x = mask.getX() + dx;
                    double y = mask.getY() + dy;
                    double z = mask.getZ() + dz;

                    if (isTooCloseToWall(x, y, z)) {
                        continue;
                    }

                    mask.getMoveControl().setWantedPosition(x, y, z, 0.18);
                    return;
                }
            }
        }

        private void executePatrol() {
            if (patrolTargetA == null || patrolTargetB == null) {
                if (homePos == null) {
                    return;
                }
                patrolTargetA = homePos;
                patrolTargetB = homePos.offset(8, 0, 0);
                patrolIndex = 0;
            }

            BlockPos target = (patrolIndex == 0 ? patrolTargetA : patrolTargetB);

            if (mask.distanceToSqr(Vec3.atCenterOf(target)) < 4) {
                if (patrolPauseTicks <= 0) {
                    patrolPauseTicks = PATROL_PAUSE_DURATION_TICKS;
                }

                patrolPauseTicks--;

                ((MaskMoveControl) mask.getMoveControl()).setToWait();
                mask.setSpeed(0.0f);

                if (patrolPauseTicks <= 0) {
                    patrolIndex = 1 - patrolIndex;
                }

                return;
            }

            patrolPauseTicks = 0;

            double tx = target.getX() + 0.5;
            double ty = target.getY();
            double tz = target.getZ() + 0.5;

            mask.getMoveControl().setWantedPosition(tx, ty, tz, 0.2);

            if (mask.tickCount % PATROL_BLOCK_BREAK_INTERVAL == 0) {
                if (mask.tryBreakBlockToward(tx, ty, tz, PATROL_BLOCK_BREAK_PROBE_DISTANCE)) {
                    Player nearest = mask.findNearestPlayerIgnoringLineOfSight(MAX_DETECTION_DISTANCE_SQ);
                    if (nearest != null) {
                        mask.setTarget(nearest);
                        mask.alertNearbyMasks(nearest);
                    }
                }
            }
        }

        @Override
        public void tick() {
            byte mode = mask.getMode();

            switch (mode) {
                case MODE_CHASE -> executeChase();
                case MODE_GUARD -> executeGuard();
                case MODE_PATROL -> executePatrol();
                case MODE_WANDER -> executeWander();
                default -> {}
            }
        }
    }

    private boolean isTooCloseToWall(double x, double y, double z) {
        BlockPos center = BlockPos.containing(x, y, z);

        for (int dx = -WANDER_WALL_CLEARANCE; dx <= WANDER_WALL_CLEARANCE; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -WANDER_WALL_CLEARANCE; dz <= WANDER_WALL_CLEARANCE; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    BlockPos p = center.offset(dx, dy, dz);
                    BlockState s = this.level().getBlockState(p);
                    if (!s.isAir() && s.isSolid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isWallTooCloseInFront(double targetX, double targetY, double targetZ) {
        Vec3 from = this.position();
        Vec3 to = new Vec3(targetX, targetY, targetZ);
        Vec3 dir = to.subtract(from);

        if (dir.lengthSqr() < 1.0E-6) {
            return false;
        }

        Vec3 forward = dir.normalize();

        for (int i = 1; i <= GUARD_WALL_CHECK_DISTANCE; i++) {
            BlockPos p =  BlockPos.containing(this.position().add(forward.scale(i)));
            BlockState s = this.level().getBlockState(p);
            if (!s.isAir() && s.isSolid()) {
                return true;
            }
        }

        return false;
    }

    private boolean tryBreakBlockToward(double targetX, double targetY, double targetZ, double probeDistance) {
        Vec3 from = this.position();
        Vec3 to = new Vec3(targetX, targetY, targetZ);
        Vec3 dir = to.subtract(from);

        if (dir.lengthSqr() < 1.0E-6) {
            return false;
        }

        Vec3 step = dir.normalize().scale(probeDistance);
        BlockPos pos = BlockPos.containing(from.add(step));

        BlockState state = this.level().getBlockState(pos);
        if (!state.isAir() && state.getDestroySpeed(this.level(), pos) >= 0) {
            this.level().destroyBlock(pos, true, this);
            this.level().playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.9F, 0.9F + this.random.nextFloat() * 0.2F);
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        12, 0.3, 0.3, 0.3, 0.08);
            }
            return true;
        }

        return false;
    }

    public class MaskMoveControl extends MoveControl {

        private static final double HOVER_HEIGHT = 1.01;
        private static final double HOVER_CORRECTION_SPEED = 0.05;
        private static final double HOVER_DAMPING = 0.8;

    public MaskMoveControl(MaskEntity mob) {
            super(mob);
        }

        @Override
        public void tick() {
            boolean chase = MaskEntity.this.getMode() == MODE_CHASE;
            boolean insideSolid = chase && MaskEntity.this.isChaseInsideSolid();

            if (!chase && !insideSolid) {
                double groundY = findGroundYBelow();
                double targetY = groundY + HOVER_HEIGHT;
                double distanceFromTarget = targetY - mob.getY();
                double newVerticalVelocity = (mob.getDeltaMovement().y * HOVER_DAMPING) + (distanceFromTarget * HOVER_CORRECTION_SPEED);

                mob.setDeltaMovement(
                        mob.getDeltaMovement().x,
                        newVerticalVelocity,
                        mob.getDeltaMovement().z
                );
            }

            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 delta = new Vec3(this.wantedX - mob.getX(), 0.0, this.wantedZ - mob.getZ());
                double horizontalDistance = delta.horizontalDistance();

                if (horizontalDistance < mob.getBbWidth()) {
                    this.operation = MoveControl.Operation.WAIT;
                    mob.setSpeed(0.0f);
                    return;
                }

                mob.setYRot((float) Mth.atan2(delta.z, delta.x) * Mth.RAD_TO_DEG - 90.0f);
                mob.setSpeed((float) this.speedModifier);
            }
        }

        private double findGroundYBelow() {
            BlockPos current = mob.blockPosition();

            for (int i = 1; i <= 16; i++) {
                BlockPos below = current.below(i);
                if (below.getY() < mob.level().getMinBuildHeight()) {
                    return mob.getY();
                }

                var state = mob.level().getBlockState(below);
                var shape = state.getCollisionShape(mob.level(), below);
                if (!shape.isEmpty()) {
                    double top = shape.max(Direction.Axis.Y);
                    return below.getY() + top;
                }
            }

            return mob.getY();
        }

        public void setToWait() {
            this.operation = Operation.WAIT;
        }
    }
}