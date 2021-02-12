package org.dimdev.dimdoors.entity.ai;

import java.util.EnumSet;
import java.util.Random;

import io.netty.buffer.Unpooled;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.sound.ModSoundEvents;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import static net.minecraft.predicate.entity.EntityPredicates.EXCEPT_SPECTATOR;
import static org.dimdev.dimdoors.entity.MonolithEntity.MAX_AGGRO;

public class MonolithAggroGoal extends Goal {
    protected final MonolithEntity mob;
    protected PlayerEntity target;
    protected final float range;
    protected final TargetPredicate targetPredicate;

    public MonolithAggroGoal(MonolithEntity mobEntity, float f) {
        this.mob = mobEntity;
        this.range = f;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
        this.targetPredicate = (new TargetPredicate()).setBaseMaxDistance(this.range).includeTeammates().includeInvulnerable().ignoreEntityTargetRules().setPredicate(EXCEPT_SPECTATOR::test);
    }

    private PlayerEntity getTarget() {
        PlayerEntity playerEntity = this.mob.world.getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        return playerEntity != null && this.mob.canSee(playerEntity) && playerEntity.distanceTo(this.mob) < 50 ? playerEntity : null;
    }

    public boolean canStart() {
        return (this.target = this.getTarget()) != null && this.target.distanceTo(this.mob) <= 50;
    }

    public boolean shouldContinue() {
        return (this.target = this.getTarget()) != null && this.target.distanceTo(this.mob) <= 50;
    }

    public void start() {
    }

    public void stop() {
        this.target = null;
        this.mob.setAggro(0);
    }

    public void tick() {
        if (this.target != null && this.target.distanceTo(this.mob) > 70) {
            this.stop();
            return;
        }

        if (this.target != null && (this.target.getInventory().armor.get(0).getItem() == ModItems.WORLD_THREAD_HELMET && this.target.getInventory().armor.get(1).getItem() == ModItems.WORLD_THREAD_CHESTPLATE && this.target.getInventory().armor.get(2).getItem() == ModItems.WORLD_THREAD_LEGGINGS && this.target.getInventory().armor.get(3).getItem() == ModItems.WORLD_THREAD_BOOTS)) {
            Random random = new Random();
            int i = random.nextInt(64);
            if (this.target instanceof ServerPlayerEntity) {
                if (i < 6) {
                    this.target.getInventory().armor.get(0).damage(i, random, (ServerPlayerEntity) this.target);
                    this.target.getInventory().armor.get(1).damage(i, random, (ServerPlayerEntity) this.target);
                    this.target.getInventory().armor.get(2).damage(i, random, (ServerPlayerEntity) this.target);
                    this.target.getInventory().armor.get(3).damage(i, random, (ServerPlayerEntity) this.target);
                }
            }
            return;
        }

        boolean visibility = this.target != null;
        this.mob.updateAggroLevel(this.target, visibility);

        // Change orientation and face a player if one is in range
        if (this.target != null) {
            this.mob.facePlayer(this.target);
            if (this.mob.isDangerous()) {
                // Play sounds on the server side, if the player isn't in Limbo.
                // Limbo is excluded to avoid drowning out its background music.
                // Also, since it's a large open area with many Monoliths, some
                // of the sounds that would usually play for a moment would
                // keep playing constantly and would get very annoying.
                this.mob.playSounds(this.target.getPos());

                PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
                data.writeInt(this.mob.getAggro());
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(this.target, DimensionalDoorsInitializer.MONOLITH_PARTICLE_PACKET, data);
            }

            // Teleport the target player if various conditions are met
            if (this.mob.getAggro() >= MAX_AGGRO && DimensionalDoorsInitializer.getConfig().getMonolithsConfig().monolithTeleportation && !this.target.isCreative() && this.mob.isDangerous()) {
                this.mob.setAggro(0);
                //Location destination = LimboDimension.getLimboSkySpawn(player);
                //TeleportUtil.teleport(player, destination, 0, 0);
                this.target.world.playSound(null, new BlockPos(this.target.getPos()), ModSoundEvents.CRACK, SoundCategory.HOSTILE, 13, 1);
            }
        }
    }
}
