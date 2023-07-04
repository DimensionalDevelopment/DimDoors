package org.dimdev.dimdoors.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.packet.s2c.MonolithAggroParticlesPacket;
import org.dimdev.dimdoors.network.packet.s2c.MonolithTeleportParticlesPacket;
import org.dimdev.dimdoors.sound.ModSoundEvents;

import java.util.EnumSet;

import static org.dimdev.dimdoors.entity.MonolithEntity.MAX_AGGRO;

public class MonolithAggroGoal extends Goal {
	protected final MonolithEntity mob;
    protected Player target;
    protected final float range;
    protected final TargetingConditions targetPredicate;

    public MonolithAggroGoal(MonolithEntity mobEntity, float f) {
        this.mob = mobEntity;
        this.range = f;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        this.targetPredicate = (TargetingConditions.forCombat()).range(this.range).selector(EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
    }

    private Player getTarget() {
        Player playerEntity = this.mob.level.getNearestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        return playerEntity != null && this.mob.hasLineOfSight(playerEntity) && playerEntity.distanceTo(this.mob) < 50 ? playerEntity : null;
    }

    public boolean canUse() {
        return (this.target = this.getTarget()) != null && this.target.distanceTo(this.mob) <= 50;
    }

    public boolean canContinueToUse() {
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
            RandomSource random = RandomSource.create();
            int i = random.nextInt(64);
            if (this.target instanceof ServerPlayer) {
                if (i < 4) {
                    this.target.getInventory().armor.get(0).hurt(i, random, (ServerPlayer) this.target);
                    this.target.getInventory().armor.get(1).hurt(i, random, (ServerPlayer) this.target);
                    this.target.getInventory().armor.get(2).hurt(i, random, (ServerPlayer) this.target);
                    this.target.getInventory().armor.get(3).hurt(i, random, (ServerPlayer) this.target);
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
                this.mob.playSounds(this.target.position());
				ServerPacketHandler.get((ServerPlayer) this.target).sendPacket(new MonolithAggroParticlesPacket(this.mob.getAggro()));
            }

            // Teleport the target player if various conditions are met
            if (this.mob.getAggro() >= MAX_AGGRO && DimensionalDoors.getConfig().getMonolithsConfig().monolithTeleportation && !this.target.isCreative() && this.mob.isDangerous()) {
                this.mob.setAggro(0);
				this.target.teleportTo(this.target.getX(), this.target.getY() + 256, this.target.getZ());
                this.target.level.playSound(null, new BlockPos(new Vec3i((int) this.target.position().x, (int) this.target.position().y, (int) this.target.position().z)), ModSoundEvents.CRACK.get(), SoundSource.HOSTILE, 13, 1);
                this.target.awardStat(ModStats.TIMES_TELEPORTED_BY_MONOLITH);
                ServerPacketHandler.get((ServerPlayer) this.target).sendPacket(new MonolithTeleportParticlesPacket());
			}
        }
    }
}
