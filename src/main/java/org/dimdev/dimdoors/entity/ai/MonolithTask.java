package org.dimdev.dimdoors.entity.ai;

import io.netty.buffer.Unpooled;

import java.util.EnumSet;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.sound.ModSoundEvents;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import static net.minecraft.predicate.entity.EntityPredicates.EXCEPT_SPECTATOR;
import static org.dimdev.dimdoors.entity.MonolithEntity.MAX_AGGRO;

public class MonolithTask extends Goal {
    protected final MonolithEntity mob;
    protected PlayerEntity target;
    protected final float range;
    protected final TargetPredicate targetPredicate;

    public MonolithTask(MonolithEntity mobEntity, float f) {
        this.mob = mobEntity;
        this.range = f;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
        this.targetPredicate = (new TargetPredicate()).setBaseMaxDistance(range).includeTeammates().includeInvulnerable().ignoreEntityTargetRules().setPredicate(EXCEPT_SPECTATOR::test);
    }

    private PlayerEntity getTarget() {
        PlayerEntity playerEntity = this.mob.world.getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        return playerEntity != null && mob.canSee(playerEntity) && playerEntity.distanceTo(this.mob) < 50? playerEntity : null;
    }

    public boolean canStart() {
        return (this.target = getTarget()) != null && target.distanceTo(this.mob) <= 50;
    }

    public boolean shouldContinue() {
        return (this.target = getTarget()) != null && target.distanceTo(this.mob) <= 50;
    }

    public void start() {
    }

    public void stop() {
        this.target = null;
        this.mob.setAggro(0);
    }

    public void tick() {
        if(target != null && this.target.distanceTo(this.mob) > 70) {
            this.stop();
            return;
        }

        boolean visibility = target != null;
        mob.updateAggroLevel(target, visibility);

        // Change orientation and face a player if one is in range
        if (target != null) {
            mob.facePlayer(target);
            if (mob.isDangerous()) {
                // Play sounds on the server side, if the player isn't in Limbo.
                // Limbo is excluded to avoid drowning out its background music.
                // Also, since it's a large open area with many Monoliths, some
                // of the sounds that would usually play for a moment would
                // keep playing constantly and would get very annoying.
                mob.playSounds(target.getPos());

                PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
                data.writeInt(this.mob.getAggro());
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(target, DimensionalDoorsInitializer.MONOLITH_PARTICLE_PACKET, data);
            }

            // Teleport the target player if various conditions are met
            if (mob.getAggro() >= MAX_AGGRO && ModConfig.MONOLITHS.monolithTeleportation && !target.isCreative() && mob.isDangerous()) {
                mob.setAggro(0);
                //Location destination = LimboDimension.getLimboSkySpawn(player);
                //TeleportUtil.teleport(player, destination, 0, 0);
                target.world.playSound(null, new BlockPos(target.getPos()), ModSoundEvents.CRACK, SoundCategory.HOSTILE, 13, 1);
            }
        }
    }
}
