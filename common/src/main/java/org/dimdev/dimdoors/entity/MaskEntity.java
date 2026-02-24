package org.dimdev.dimdoors.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MaskEntity extends PathfinderMob {
    private static final byte SPOTTED_EVENT = 45;
    public final AnimationState idleState = new AnimationState();
    public final AnimationState spottedState = new AnimationState();
    private boolean isSpotting = false;
    private int spottedCounter = -1;

    protected MaskEntity(EntityType<? extends MaskEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();

        tickSpotted();

        if (this.level().isClientSide()) {
            this.spottedState.animateWhen(isSpotting, this.tickCount);
            this.idleState.animateWhen(!isSpotting, this.tickCount);
        }
    }

    public void tickSpotted() {
        if(isSpotting) {
            if (spottedCounter > 33) {
                spottedCounter = 0;
                isSpotting = false;
            } else {
                spottedCounter++;
            }
        }
    }

    @Override
    public void handleEntityEvent(byte b) {
        if(b == SPOTTED_EVENT) {
            this.isSpotting = true;
            spottedCounter = 0;
        }
        super.handleEntityEvent(b);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if(!level().isClientSide()) {
            triggerSpotted();
        }

        return false;
    }

    private void triggerSpotted() {
        this.isSpotting = true;
        spottedCounter = 0;
        this.level().broadcastEntityEvent(this, SPOTTED_EVENT);
    }
}
