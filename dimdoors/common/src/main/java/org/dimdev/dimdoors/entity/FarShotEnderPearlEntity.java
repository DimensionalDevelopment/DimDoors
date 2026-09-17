package org.dimdev.dimdoors.entity;

import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.item.RaycastHelper;
import org.dimdev.dimdoors.util.RotationUtil;
import org.jetbrains.annotations.NotNull;

public class FarShotEnderPearlEntity extends ThrowableItemProjectile {
    private final boolean setTargetOnFire;
    private final int punchLevel;

    public FarShotEnderPearlEntity(EntityType<? extends FarShotEnderPearlEntity> entityType, Level level) {
        super(entityType, level);
        this.setTargetOnFire = false;
        this.punchLevel = 0;
    }

    public FarShotEnderPearlEntity(Level level, LivingEntity shooter) {
        this(level, shooter, false, 0);
    }

    public FarShotEnderPearlEntity(@NotNull Level level, @NotNull LivingEntity shooter, boolean hasFlaming, int punchLevel) {
        super(ModEntityTypes.FARSHOT_ENDER_PEARL, shooter, level);
        this.setTargetOnFire = hasFlaming;
        this.punchLevel = punchLevel;
    }

    protected @NotNull Item getDefaultItem() {
        return Items.ENDER_PEARL;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        Entity target = result.getEntity();
        target.hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F); // vanilla parity: aggro the target

        Entity owner = this.getOwner();
        if (owner == null || owner == target || !(this.level() instanceof ServerLevel level) || owner.level() != level) {
            return;
        }

        Vec3 ownerPos = owner.position();
        Vec3 targetPos = target.position();
        float ownerYRot = owner.getYRot(), ownerXRot = owner.getXRot();
        float targetYRot = target.getYRot(), targetXRot = target.getXRot();

        owner.unRide();
        target.unRide();

        for (Vec3 pos : new Vec3[]{ownerPos, targetPos}) {
            level.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y + 1.0, pos.z, 48, 0.35, 0.75, 0.35, 0.35);
            level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        owner.resetFallDistance();
        if (owner instanceof ServerPlayer player) {
            player.connection.teleport(targetPos.x, targetPos.y, targetPos.z, ownerYRot, ownerXRot);
        } else {
            owner.teleportTo(targetPos.x, targetPos.y, targetPos.z);
        }

        if(setTargetOnFire) target.igniteForSeconds(5f);
        target.resetFallDistance();
        if (target instanceof ServerPlayer player) {
            player.connection.teleport(ownerPos.x, ownerPos.y, ownerPos.z, targetYRot, targetXRot);
        } else {
            target.teleportTo(ownerPos.x, ownerPos.y, ownerPos.z);
        }

        if(punchLevel > 0) {
            target.addDeltaMovement(this.getDeltaMovement().normalize().scale(0.8 * punchLevel));
        }

        this.discard();
    }

    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        for(int i = 0; i < 32; ++i) {
            this.level().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * (double) 2.0F, this.getZ(), this.random.nextGaussian(), (double)0.0F, this.random.nextGaussian());
        }

        if (this.level() instanceof ServerLevel serverlevel) {
            if (!this.isRemoved()) {
                Entity entity = this.getOwner();
                if (entity != null && isAllowedToTeleportOwner(entity, serverlevel)) {
                    if (entity.isPassenger()) entity.unRide();

                    if (entity instanceof ServerPlayer serverplayer) {
                        if (serverplayer.connection.isAcceptingMessages()) {
                            if (this.random.nextFloat() < 0.05F && serverlevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                                Endermite endermite = (Endermite)EntityType.ENDERMITE.create(serverlevel);
                                if (endermite != null) {
                                    endermite.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                                    serverlevel.addFreshEntity(endermite);
                                }
                            }

                            entity.changeDimension(new DimensionTransition(serverlevel, this.position(), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), DimensionTransition.DO_NOTHING));
                            entity.resetFallDistance();
                            serverplayer.resetCurrentImpulseContext();
                            entity.hurt(this.damageSources().fall(), 5.0F);
                            this.playSound(serverlevel, this.position());
                        }
                    } else {
                        entity.changeDimension(new DimensionTransition(serverlevel, this.position(), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), DimensionTransition.DO_NOTHING));
                        entity.resetFallDistance();
                        this.playSound(serverlevel, this.position());
                    }

                    this.discard();
                    return;
                }

                this.discard();
            }
        }

    }

    private static boolean isAllowedToTeleportOwner(Entity entity, Level level) {
        if (entity.level().dimension() != level.dimension()) {
            return entity.canUsePortal(true);
        } else {
            return entity instanceof LivingEntity livingentity ? livingentity.isAlive() && !livingentity.isSleeping() : entity.isAlive();
        }
    }

    public void tick() {
        if (this.getOwner() instanceof ServerPlayer entity && !entity.isAlive() && this.level().getGameRules().getBoolean(GameRules.RULE_ENDER_PEARLS_VANISH_ON_DEATH)) {
            this.discard();
        } else {
            HitResult hit  = RaycastHelper.projectileCast(this, this::canHitEntity);

            if (RaycastHelper.hitsDetachedRift(hit, this.level())) {
                this.onHit(hit);
            }

            super.tick();
        }

    }

    private void playSound(Level level, Vec3 pos) {
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
    }

    public boolean canChangeDimensions(Level oldLevel, @NotNull Level newLevel) {
        if (oldLevel.dimension() == Level.END) {
            if (this.getOwner() instanceof ServerPlayer serverplayer) {
                return super.canChangeDimensions(oldLevel, newLevel) && serverplayer.seenCredits;
            }
        }

        return super.canChangeDimensions(oldLevel, newLevel);
    }

    protected void onInsideBlock(@NotNull BlockState state) {
        super.onInsideBlock(state);
        if (state.is(Blocks.END_GATEWAY)) {
            if (this.getOwner() instanceof ServerPlayer serverplayer) {
                serverplayer.onInsideBlock(state);
            }
        }
    }
}
