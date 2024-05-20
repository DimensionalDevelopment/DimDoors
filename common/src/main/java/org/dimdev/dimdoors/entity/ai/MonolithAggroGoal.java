package org.dimdev.dimdoors.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.packet.s2c.MonolithAggroParticlesPacket;
import org.dimdev.dimdoors.network.packet.s2c.MonolithTeleportParticlesPacket;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.tag.ModItemTags;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.EnumSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if (this.target != null && this.target.distanceTo(this.mob) > 70 && this.mob.getAggro() == 0) {
            this.stop();
            return;
        }

        if (this.target != null) {
            var slots = Stream.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET).filter(a -> target.getItemBySlot(a).is(ModItemTags.LIMBO_GAZE_DEFYING)).toList();

            if (!slots.isEmpty()) {
                RandomSource random = RandomSource.create();

                int i = random.nextInt((64 + 16  * (slots.size() / 4)));
                if (this.target instanceof ServerPlayer) {
                    if (i < 4) {
                        var slot = random.nextIntBetweenInclusive(0, slots.size() - 1);

                        var equip = slots.get(slot);

                        var item = this.target.getItemBySlot(equip);

                        item.hurtAndBreak(i, target, livingEntity -> livingEntity.broadcastBreakEvent(equip));

                    }

                    this.mob.updateAggroLevel(this.target, false);
                }

                return;
            }

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
                TeleportUtil.teleport(this.target, DimensionalDoors.getWorld(ModDimensions.LIMBO), this.target.position().add(0, 256, 0f), this.target.getVisualRotationYInDegrees());
                this.target.level.playSound(null, new BlockPos(new Vec3i((int) this.target.position().x, (int) this.target.position().y, (int) this.target.position().z)), ModSoundEvents.CRACK.get(), SoundSource.HOSTILE, 13, 1);
                this.target.awardStat(ModStats.TIMES_TELEPORTED_BY_MONOLITH);
                ServerPacketHandler.get((ServerPlayer) this.target).sendPacket(new MonolithTeleportParticlesPacket());
			}
        }
    }
}
