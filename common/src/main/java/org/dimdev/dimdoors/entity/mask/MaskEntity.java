package org.dimdev.dimdoors.entity.mask;

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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.effect.ChasedEffect;
import org.dimdev.dimdoors.item.MaskItem;
import org.dimdev.dimdoors.item.MaskWandItem;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.world.ModDimensions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MaskEntity extends Mob {
    private static final EntityDataAccessor<Byte> MODE = SynchedEntityData.defineId(MaskEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> TYPE = SynchedEntityData.defineId(MaskEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> FROZEN = SynchedEntityData.defineId(MaskEntity.class, EntityDataSerializers.BOOLEAN);

    private static final double BLOCK_ALERT_RANGE = 5.0;
    private static final double ROOM_ALERT_RANGE = 48.0;
    private static final double CHASE_RANGE = 50.0;
    private static final double CATCH_DISTANCE_SQ = 1.35 * 1.35;
    private static final double CHASE_SPEED = 0.125;
    private static final double CHASE_SOLID_SPEED = 0.045;
    private static final double PASSIVE_SPEED = 0.055;
    private static final int PASSIVE_SCAN_INTERVAL = 5;
    private static final int PATROL_PAUSE_TICKS = 45;
    private static final int STUN_TICKS = 60;
    private static final int MAX_WAYPOINTS = 10;
    private static final double ECHO_BLOCK_NOTICE_RANGE = 10.0;

    public final AnimationState idleState = new AnimationState();
    public final AnimationState spottedState = new AnimationState();

    @Nullable
    private BlockPos homePos;
    private final List<BlockPos> patrolRoute = new ArrayList<>();
    private int patrolIndex;
    private int patrolDirection = 1;
    private int patrolPauseTicks;
    private int stunTicks;
    private int passiveScanTicks;
    private int idleSoundCooldown;
    private int turnCooldown;
    @Nullable
    private BlockPos echoBlockTarget;
    @Nullable
    private Vec3 wanderTarget;
    private MaskMode resumeMode = MaskMode.GUARD;

    public MaskEntity(EntityType<? extends MaskEntity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public static void alertMasksNearBlock(Level level, BlockPos pos, Player player) {
        if (level.isClientSide || player.isCreative() || player.isSpectator()) {
            return;
        }

        AABB search = new AABB(pos).inflate(BLOCK_ALERT_RANGE);
        for (MaskEntity mask : level.getEntitiesOfClass(MaskEntity.class, search)) {
            mask.alertRoom(player);
        }
    }

    public static void notifyEchoesOfPlacedBlock(Level level, BlockPos pos, Player player) {
        if (level.isClientSide || player.isCreative() || player.isSpectator()) {
            return;
        }

        AABB search = new AABB(pos).inflate(ECHO_BLOCK_NOTICE_RANGE);
        for (MaskEntity mask : level.getEntitiesOfClass(MaskEntity.class, search)) {
            if (mask.canEchoTarget(pos)) {
                mask.setEchoBlockTarget(pos);
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MODE, (byte) MaskMode.GUARD.ordinal());
        builder.define(TYPE, (byte) MaskType.CYCLOP.ordinal());
        builder.define(FROZEN, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            spottedState.animateWhen(getMode() == MaskMode.CHASE, this.tickCount);
            idleState.animateWhen(getMode() != MaskMode.CHASE, this.tickCount);
            return;
        }

        if (getMaskType() == MaskType.RANDOM) {
            setMaskType(randomNormalType());
        }

        if (getMaskType() == MaskType.BLACK && getMode() != MaskMode.CHASE) {
            setMode(MaskMode.CHASE);
        }

        if (isFrozen()) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        tickSounds();
        emitDetectionBubble();
        tickMode();
        move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(getMode() == MaskMode.CHASE ? 0.86 : 0.72));
    }

    private void tickMode() {
        if (isPassiveMode(getMode()) && getMaskType() == MaskType.ECHO && tickEchoBlockTarget()) {
            scanForTargets();
            return;
        }

        switch (getMode()) {
            case GUARD -> tickGuard();
            case PATROL -> tickPatrol();
            case WANDER -> tickWander();
            case CHASE -> tickChase();
            case STUNNED -> tickStunned();
        }

        if (isPassiveMode(getMode())) {
            scanForTargets();
        }
    }

    private void tickSounds() {
        if (getMode() == MaskMode.CHASE || getMode() == MaskMode.STUNNED) {
            return;
        }

        if (idleSoundCooldown > 0) {
            idleSoundCooldown--;
            return;
        }

        if (random.nextInt(220) == 0) {
            level().playSound(null, blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 0.55F, 0.65F + random.nextFloat() * 0.4F);
            idleSoundCooldown = 80;
        }
    }

    private void tickGuard() {
        moveToward(homeCenter(), PASSIVE_SPEED * 0.55);

        if (turnCooldown > 0) {
            turnCooldown--;
            return;
        }

        Direction direction = pickOpenHorizontalDirection();
        if (direction != null) {
            faceDirection(direction);
        }
        turnCooldown = 45 + random.nextInt(85);
    }

    private void tickPatrol() {
        if (patrolRoute.size() < 2) {
            setMode(MaskMode.GUARD);
            return;
        }

        BlockPos targetPos = patrolRoute.get(Mth.clamp(patrolIndex, 0, patrolRoute.size() - 1));
        Vec3 target = Vec3.atCenterOf(targetPos);
        if (distanceToSqr(target) < 1.2) {
            setDeltaMovement(getDeltaMovement().scale(0.45));
            if (patrolPauseTicks++ >= PATROL_PAUSE_TICKS) {
                patrolPauseTicks = 0;
                advancePatrolIndex();
            }
            return;
        }

        patrolPauseTicks = 0;
        moveToward(target, PASSIVE_SPEED);
        breakBlockToward(target, 0.75);
    }

    private void tickWander() {
        if (wanderTarget == null || distanceToSqr(wanderTarget) < 1.5 || random.nextInt(120) == 0) {
            wanderTarget = pickWanderTarget();
        }

        if (wanderTarget != null) {
            moveToward(wanderTarget, PASSIVE_SPEED * 0.85);
        }
    }

    private void tickChase() {
        Player target = findNearestPlayer(CHASE_RANGE);
        if (target == null) {
            if (getMaskType() == MaskType.BLACK) {
                remove(RemovalReason.DISCARDED);
            } else {
                resetToHome();
            }
            return;
        }

        facePosition(target.position());

        if (distanceToSqr(target) <= CATCH_DISTANCE_SQ) {
            catchPlayer(target);
            return;
        }

        boolean inSolid = isInsideSolidBlock();
        moveToward(target.getEyePosition(), inSolid ? CHASE_SOLID_SPEED : CHASE_SPEED);

        if (inSolid && tickCount % 5 == 0) {
            emitDiggingEffects();
        }
    }

    private void tickStunned() {
        setDeltaMovement(Vec3.ZERO);
        if (stunTicks-- <= 0) {
            Player nearest = findNearestPlayer(CHASE_RANGE);
            if (nearest != null) {
                alertRoom(nearest);
            } else {
                setMode(MaskMode.CHASE);
            }
        }
    }

    private void scanForTargets() {
        if (++passiveScanTicks < PASSIVE_SCAN_INTERVAL) {
            return;
        }
        passiveScanTicks = 0;

        if (getMaskType() == MaskType.FORESIGHT && dodgeProjectile()) {
            Player nearest = findNearestPlayer(CHASE_RANGE);
            if (nearest != null) {
                alertRoom(nearest);
            }
            return;
        }

        Player player = findDetectedPlayer();
        if (player != null) {
            alertRoom(player);
        }
    }

    private boolean tickEchoBlockTarget() {
        if (echoBlockTarget == null) {
            return false;
        }

        if (!canEchoTarget(echoBlockTarget)) {
            echoBlockTarget = null;
            return false;
        }

        Vec3 target = Vec3.atCenterOf(echoBlockTarget);
        if (distanceToSqr(target) <= 1.4) {
            destroyEchoTarget(echoBlockTarget);
            echoBlockTarget = null;
            return true;
        }

        moveToward(target, PASSIVE_SPEED * 1.15);
        breakBlockToward(target, 0.9);
        return true;
    }

    private boolean canEchoTarget(BlockPos pos) {
        return getMaskType() == MaskType.ECHO && isPassiveMode(getMode()) && !level().getBlockState(pos).isAir() && isBlockInSight(pos, MaskType.ECHO);
    }

    private void setEchoBlockTarget(BlockPos pos) {
        if (echoBlockTarget == null || distanceToSqr(Vec3.atCenterOf(pos)) < distanceToSqr(Vec3.atCenterOf(echoBlockTarget))) {
            echoBlockTarget = pos.immutable();
        }
    }

    private void destroyEchoTarget(BlockPos pos) {
        BlockState state = level().getBlockState(pos);
        if (!state.isAir() && state.getDestroySpeed(level(), pos) >= 0.0F) {
            level().destroyBlock(pos, true, this);
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 18, 0.35, 0.35, 0.35, 0.08);
            }
        }
    }

    @Nullable
    private Player findDetectedPlayer() {
        Player best = null;
        double bestDistance = Double.MAX_VALUE;

        for (Player player : level().players()) {
            if (!canDetect(player)) {
                continue;
            }

            double distance = distanceToSqr(player);
            if (distance < bestDistance) {
                best = player;
                bestDistance = distance;
            }
        }

        return best;
    }

    private boolean canDetect(Player player) {
        if (player.isCreative() || player.isSpectator()) {
            return false;
        }

        MaskType type = getMaskType();
        if (type == MaskType.BLACK) {
            return true;
        }

        Vec3 toPlayer = player.getEyePosition().subtract(getEyePosition());
        double distance = toPlayer.length();

        if (type == MaskType.SCULKING) {
            return distance <= 8.0 && (player.getDeltaMovement().horizontalDistanceSqr() > 0.0009 || !player.isShiftKeyDown());
        }

        if (distance > detectionRange(type)) {
            return false;
        }

        if (type == MaskType.ENLIGHTENED) {
            return true;
        }

        Vec3 flat = new Vec3(toPlayer.x, 0.0, toPlayer.z);
        if (flat.lengthSqr() < 1.0E-5) {
            return hasLineTo(player);
        }

        double dot = forwardVector().dot(flat.normalize());
        boolean inShape = switch (type) {
            case CYCLOP, RANDOM -> dot >= 0.68;
            case ECHO, FORESIGHT -> dot >= 0.0;
            default -> true;
        };

        return inShape && hasLineTo(player);
    }

    private boolean hasLineTo(Player player) {
        HitResult hit = level().clip(new ClipContext(getEyePosition(), player.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return hit.getType() != HitResult.Type.BLOCK;
    }

    private boolean isBlockInSight(BlockPos pos, MaskType type) {
        Vec3 toBlock = Vec3.atCenterOf(pos).subtract(getEyePosition());
        double distance = toBlock.length();
        if (distance > detectionRange(type)) {
            return false;
        }

        if (type != MaskType.ENLIGHTENED) {
            Vec3 flat = new Vec3(toBlock.x, 0.0, toBlock.z);
            if (flat.lengthSqr() > 1.0E-5) {
                double dot = forwardVector().dot(flat.normalize());
                if (type == MaskType.ECHO && dot < 0.0) {
                    return false;
                }
            }
        }

        HitResult hit = level().clip(new ClipContext(getEyePosition(), Vec3.atCenterOf(pos), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return hit.getType() != HitResult.Type.BLOCK || BlockPos.containing(hit.getLocation()).equals(pos);
    }

    private void emitDetectionBubble() {
        if (!(level() instanceof ServerLevel serverLevel) || !isPassiveMode(getMode()) || tickCount % 12 != 0) {
            return;
        }

        MaskType type = getMaskType();
        double range = detectionRange(type);
        if (range <= 0.0 || type == MaskType.BLACK) {
            return;
        }

        int samples = type == MaskType.ENLIGHTENED || type == MaskType.SCULKING ? 20 : 12;
        double arc = switch (type) {
            case CYCLOP, RANDOM -> Math.toRadians(94.0);
            case ECHO, FORESIGHT -> Math.PI;
            case ENLIGHTENED, SCULKING -> Math.PI * 2.0;
            case BLACK -> 0.0;
        };

        Vec3 forward = forwardVector();
        double center = Math.atan2(forward.z, forward.x);
        double start = center - arc * 0.5;
        for (int i = 0; i < samples; i++) {
            double angle = samples == 1 ? center : start + arc * i / (samples - 1);
            Vec3 particle = getEyePosition().add(Math.cos(angle) * range, -0.15, Math.sin(angle) * range);
            serverLevel.sendParticles(type == MaskType.SCULKING ? ParticleTypes.SCULK_SOUL : ParticleTypes.END_ROD, particle.x, particle.y, particle.z, 1, 0.01, 0.01, 0.01, 0.0);
        }
    }

    private double detectionRange(MaskType type) {
        return switch (type) {
            case CYCLOP, RANDOM -> 6.0;
            case ECHO -> 4.0;
            case ENLIGHTENED -> 3.0;
            case FORESIGHT -> 2.0;
            case SCULKING -> 8.0;
            case BLACK -> CHASE_RANGE;
        };
    }

    private boolean dodgeProjectile() {
        AABB search = getBoundingBox().inflate(3.0);
        List<Projectile> projectiles = level().getEntitiesOfClass(Projectile.class, search, projectile -> projectile.getOwner() instanceof Player);
        if (projectiles.isEmpty()) {
            return false;
        }

        Projectile projectile = projectiles.get(0);
        Vec3 away = position().subtract(projectile.position());
        Vec3 dodge = new Vec3(-away.z, 0.0, away.x);
        if (dodge.lengthSqr() < 1.0E-5) {
            dodge = forwardVector().cross(new Vec3(0.0, 1.0, 0.0));
        }
        setDeltaMovement(getDeltaMovement().add(dodge.normalize().scale(0.22)));
        return true;
    }

    private void alertRoom(@Nullable Player player) {
        if (player == null || player.isCreative() || player.isSpectator()) {
            return;
        }

        AABB room = getBoundingBox().inflate(ROOM_ALERT_RANGE);
        for (MaskEntity mask : level().getEntitiesOfClass(MaskEntity.class, room)) {
            if (mask.getMode() == MaskMode.STUNNED || mask.isFrozen()) {
                continue;
            }
            mask.startChase(player);
        }

        level().playSound(null, blockPosition(), SoundEvents.CHAIN_HIT, SoundSource.HOSTILE, 1.6F, 0.55F);
    }

    private void startChase(Player player) {
        if (isPassiveMode(getMode())) {
            resumeMode = getMode();
        }
        setMode(MaskMode.CHASE);
        passiveScanTicks = 0;
        stunTicks = 0;
        patrolPauseTicks = 0;
    }

    private void resetToHome() {
        setMode(resumeMode);
        passiveScanTicks = 0;
        patrolPauseTicks = 0;
        stunTicks = 0;

        if (homePos != null) {
            teleportTo(homePos.getX() + 0.5, homePos.getY() + 1.05, homePos.getZ() + 0.5);
        }

        setDeltaMovement(Vec3.ZERO);
    }

    private void catchPlayer(Player player) {
        if (getMaskType() == MaskType.BLACK) {
            level().playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.0F, 0.55F);
            if (ModDimensions.LIMBO_DIMENSION != null) {
                TeleportUtil.teleportUntargeted(player, ModDimensions.LIMBO_DIMENSION);
            }
            remove(RemovalReason.DISCARDED);
            return;
        }

        MaskItem.applyCaught(player, getMaskType());
        remove(RemovalReason.DISCARDED);
    }

    private void advancePatrolIndex() {
        patrolIndex += patrolDirection;
        if (patrolIndex >= patrolRoute.size()) {
            patrolIndex = Math.max(0, patrolRoute.size() - 2);
            patrolDirection = -1;
        } else if (patrolIndex < 0) {
            patrolIndex = Math.min(1, patrolRoute.size() - 1);
            patrolDirection = 1;
        }
    }

    private void moveToward(Vec3 target, double speed) {
        Vec3 delta = target.subtract(position());
        if (delta.lengthSqr() < 1.0E-5) {
            return;
        }

        Vec3 desired = delta.normalize().scale(speed);
        setDeltaMovement(getDeltaMovement().scale(0.65).add(desired.scale(0.35)));
        facePosition(target);
    }

    private Vec3 homeCenter() {
        BlockPos home = homePos == null ? blockPosition() : homePos;
        return new Vec3(home.getX() + 0.5, home.getY() + 1.05, home.getZ() + 0.5);
    }

    @Nullable
    private Vec3 pickWanderTarget() {
        Vec3 origin = homeCenter();
        for (int i = 0; i < 12; i++) {
            Vec3 candidate = origin.add((random.nextDouble() - 0.5) * 14.0, (random.nextDouble() - 0.5) * 4.0, (random.nextDouble() - 0.5) * 14.0);
            if (!isTooCloseToWall(BlockPos.containing(candidate))) {
                return candidate;
            }
        }
        return null;
    }

    private boolean isTooCloseToWall(BlockPos center) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            for (int i = 1; i <= 2; i++) {
                BlockPos pos = center.relative(direction, i);
                BlockState state = level().getBlockState(pos);
                if (!state.isAir() && state.isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private Direction pickOpenHorizontalDirection() {
        Direction[] directions = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
        int start = random.nextInt(directions.length);
        for (int i = 0; i < directions.length; i++) {
            Direction direction = directions[(start + i) % directions.length];
            if (!wallWithin(direction, 3)) {
                return direction;
            }
        }
        return null;
    }

    private boolean wallWithin(Direction direction, int blocks) {
        BlockPos origin = blockPosition();
        for (int i = 1; i <= blocks; i++) {
            BlockPos pos = origin.relative(direction, i);
            BlockState state = level().getBlockState(pos);
            if (!state.isAir() && state.isSolid()) {
                return true;
            }
        }
        return false;
    }

    private void breakBlockToward(Vec3 target, double probeDistance) {
        if (tickCount % 4 != 0) {
            return;
        }

        Vec3 delta = target.subtract(position());
        if (delta.lengthSqr() < 1.0E-5) {
            return;
        }

        BlockPos pos = BlockPos.containing(position().add(delta.normalize().scale(probeDistance)));
        BlockState state = level().getBlockState(pos);
        if (!state.isAir() && state.getDestroySpeed(level(), pos) >= 0.0F) {
            level().destroyBlock(pos, true, this);
            level().playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.9F, 0.85F + random.nextFloat() * 0.25F);
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 14, 0.3, 0.3, 0.3, 0.08);
            }
        }
    }

    private boolean isInsideSolidBlock() {
        BlockState body = level().getBlockState(blockPosition());
        BlockState eyes = level().getBlockState(BlockPos.containing(getEyePosition()));
        return (!body.isAir() && body.isSolid()) || (!eyes.isAir() && eyes.isSolid());
    }

    private void emitDiggingEffects() {
        BlockPos pos = blockPosition();
        BlockState state = level().getBlockState(pos);
        if (state.isAir()) {
            return;
        }

        level().playSound(null, pos, SoundEvents.STONE_HIT, SoundSource.HOSTILE, 0.45F, 0.7F);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), getX(), getY() + 0.5, getZ(), 8, 0.25, 0.25, 0.25, 0.03);
        }
    }

    @Nullable
    private Player findNearestPlayer(double range) {
        Player best = null;
        double bestDistance = range * range;

        for (Player player : level().players()) {
            if (player.isCreative() || player.isSpectator()) {
                continue;
            }

            double distance = distanceToSqr(player);
            if (distance <= bestDistance) {
                bestDistance = distance;
                best = player;
            }
        }

        return best;
    }

    private Vec3 forwardVector() {
        float radians = getYRot() * Mth.DEG_TO_RAD;
        return new Vec3(-Mth.sin(radians), 0.0, Mth.cos(radians)).normalize();
    }

    private void faceDirection(Direction direction) {
        setYRot(direction.toYRot());
        yBodyRot = getYRot();
        yHeadRot = getYRot();
    }

    private void facePosition(Vec3 target) {
        Vec3 delta = target.subtract(position());
        if (delta.horizontalDistanceSqr() < 1.0E-5) {
            return;
        }

        float yaw = (float) (Mth.atan2(delta.z, delta.x) * Mth.RAD_TO_DEG) - 90.0F;
        setYRot(yaw);
        yBodyRot = yaw;
        yHeadRot = yaw;
    }

    private static boolean isPassiveMode(MaskMode mode) {
        return mode == MaskMode.GUARD || mode == MaskMode.PATROL || mode == MaskMode.WANDER;
    }

    private MaskType randomNormalType() {
        MaskType[] values = {MaskType.CYCLOP, MaskType.ECHO, MaskType.ENLIGHTENED, MaskType.FORESIGHT, MaskType.SCULKING};
        return values[random.nextInt(values.length)];
    }

    public void configureFromWand(BlockPos home, List<BlockPos> waypoints, MaskType type) {
        homePos = home.immutable();
        setMaskType(type == MaskType.RANDOM ? randomNormalType() : type);
        patrolRoute.clear();
        patrolIndex = 0;
        patrolDirection = 1;
        patrolPauseTicks = 0;
        wanderTarget = null;

        if (waypoints.size() >= 2) {
            patrolRoute.add(homePos);
            int max = Math.min(MAX_WAYPOINTS, waypoints.size());
            for (int i = max - 1; i >= 0; i--) {
                patrolRoute.add(waypoints.get(i).immutable());
            }
            resumeMode = patrolRoute.size() >= 2 ? MaskMode.PATROL : MaskMode.GUARD;
        } else {
            resumeMode = MaskMode.GUARD;
        }

        if (getMaskType() == MaskType.BLACK) {
            resumeMode = MaskMode.CHASE;
            setMode(MaskMode.CHASE);
        } else {
            setMode(resumeMode);
        }

        teleportTo(home.getX() + 0.5, home.getY() + 1.05, home.getZ() + 0.5);
    }

    public MaskMode getMode() {
        byte id = entityData.get(MODE);
        MaskMode[] values = MaskMode.values();
        return values[Mth.clamp(id, 0, values.length - 1)];
    }

    public void setMode(MaskMode mode) {
        entityData.set(MODE, (byte) mode.ordinal());
        setGlowingTag(mode == MaskMode.CHASE);
        noPhysics = mode == MaskMode.CHASE;
        setInvulnerable(mode == MaskMode.CHASE || mode == MaskMode.STUNNED);
    }

    public MaskType getMaskType() {
        byte id = entityData.get(TYPE);
        MaskType[] values = MaskType.values();
        return values[Mth.clamp(id, 0, values.length - 1)];
    }

    public void setMaskType(MaskType type) {
        entityData.set(TYPE, (byte) type.ordinal());
    }

    public boolean isFrozen() {
        return entityData.get(FROZEN);
    }

    public void setFrozen(boolean frozen) {
        entityData.set(FROZEN, frozen);
        if (frozen) {
            setDeltaMovement(Vec3.ZERO);
        }
    }

    public void recallHomeAndToggleFrozen() {
        boolean freeze = !isFrozen();
        if (freeze && homePos != null) {
            teleportTo(homePos.getX() + 0.5, homePos.getY() + 1.05, homePos.getZ() + 0.5);
            setDeltaMovement(Vec3.ZERO);
        }
        setFrozen(freeze);
    }

    public void detachFromDestroyedHome(BlockPos home) {
        if (homePos != null && !homePos.equals(home)) {
            return;
        }

        homePos = null;
        patrolRoute.clear();
        echoBlockTarget = null;
        resumeMode = MaskMode.WANDER;
        setFrozen(false);
        setMode(MaskMode.WANDER);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALLING_ANVIL)) {
            remove(RemovalReason.KILLED);
            return true;
        }

        Entity attacker = source.getEntity();
        if (attacker instanceof Player player && MaskWandItem.isHoldingMaskWand(player)) {
            if (player.isShiftKeyDown()) {
                recallHomeAndToggleFrozen();
                player.displayClientMessage(net.minecraft.network.chat.Component.literal(isFrozen() ? "Mask frozen" : "Mask released"), true);
            } else {
                MaskType nextType = getMaskType().nextEditable();
                setMaskType(nextType);
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("Mask type: " + nextType.getSerializedName()), true);
            }
            return false;
        }

        if (getMode() == MaskMode.CHASE && attacker instanceof Player player) {
            catchPlayer(player);
            return false;
        }

        if (getMode() == MaskMode.PATROL) {
            setMode(MaskMode.STUNNED);
            stunTicks = STUN_TICKS;
        }

        return false;
    }

    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);
        if (level().isClientSide) {
            return;
        }

        if (getMode() == MaskMode.STUNNED && isStompedBy(player)) {
            shatter(player);
            return;
        }

        if (getMode() == MaskMode.CHASE) {
            catchPlayer(player);
        }
    }

    private boolean isStompedBy(Player player) {
        return player.getY() > getY() + 0.35 && player.getDeltaMovement().y < -0.08;
    }

    private void shatter(Player player) {
        spawnAtLocation(new ItemStack(ModItems.MASK_SHARD));
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            ChasedEffect.giveTo(serverPlayer);
        }
        level().playSound(null, blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.0F, 0.65F);
        remove(RemovalReason.KILLED);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData);
        if (homePos == null) {
            homePos = blockPosition();
        }
        if (getMaskType() == MaskType.RANDOM) {
            setMaskType(randomNormalType());
        }
        if (getMaskType() == MaskType.BLACK) {
            setMode(MaskMode.CHASE);
        }
        return data;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Mode", getMode().getSerializedName());
        tag.putString("ResumeMode", resumeMode.getSerializedName());
        tag.putString("MaskType", getMaskType().getSerializedName());
        tag.putBoolean("Frozen", isFrozen());
        if (homePos != null) {
            tag.putLong("HomePos", homePos.asLong());
        }
        tag.putInt("PatrolIndex", patrolIndex);
        tag.putInt("PatrolDirection", patrolDirection);
        tag.putInt("PatrolRouteSize", patrolRoute.size());
        for (int i = 0; i < patrolRoute.size(); i++) {
            tag.putLong("PatrolRoute" + i, patrolRoute.get(i).asLong());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setMode(readMode(tag, "Mode", MaskMode.GUARD));
        resumeMode = readMode(tag, "ResumeMode", getMode());
        setMaskType(readType(tag, "MaskType", MaskType.CYCLOP));
        setFrozen(tag.getBoolean("Frozen"));
        if (tag.contains("HomePos")) {
            homePos = BlockPos.of(tag.getLong("HomePos"));
        }
        patrolIndex = tag.getInt("PatrolIndex");
        patrolDirection = tag.contains("PatrolDirection") ? tag.getInt("PatrolDirection") : 1;
        patrolRoute.clear();
        int routeSize = tag.getInt("PatrolRouteSize");
        for (int i = 0; i < routeSize; i++) {
            String key = "PatrolRoute" + i;
            if (tag.contains(key)) {
                patrolRoute.add(BlockPos.of(tag.getLong(key)));
            }
        }
        if (homePos == null) {
            homePos = blockPosition();
        }
    }

    private static MaskMode readMode(CompoundTag tag, String key, MaskMode fallback) {
        if (!tag.contains(key)) {
            return fallback;
        }
        String value = tag.getString(key);
        for (MaskMode mode : MaskMode.values()) {
            if (mode.getSerializedName().equals(value)) {
                return mode;
            }
        }
        return fallback;
    }

    private static MaskType readType(CompoundTag tag, String key, MaskType fallback) {
        if (!tag.contains(key)) {
            return fallback;
        }
        String value = tag.getString(key);
        for (MaskType type : MaskType.values()) {
            if (type.getSerializedName().equals(value)) {
                return type;
            }
        }
        return fallback;
    }
}
