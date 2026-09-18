package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimcore.api.transfer.FluidUnit;
import org.dimdev.dimcore.api.transfer.ItemUnit;
import org.dimdev.dimdoors.api.rift.target.*;
import org.dimdev.dimcore.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.util.LevelSpaceHelper;
import org.dimdev.dimdoors.util.RotationUtil;

import java.util.List;
import java.util.function.BiFunction;

// A list of the default targets provided by dimcore. Add your own in ModTargets
public final class Targets {
    public static final Class<EntityTarget> ENTITY = EntityTarget.class;
    public static final Class<ItemTarget> ITEM = ItemTarget.class;
    public static final Class<FluidTarget> FLUID = FluidTarget.class;
    public static final Class<RedstoneTarget> REDSTONE = RedstoneTarget.class;

    public static void registerDefaultTargets() {

        DefaultTargets.registerDefaultTarget(ENTITY, (entity, relativePos, relativeRotation, relativeVelocity, location) -> {
            if (location != null) {
                var targetLevel = location.getWorld();

                if(targetLevel == null) return false;

                EntityTarget target = TargetResolver.entity(targetLevel, location.pos);

                if (target != null) {
                    return target.receiveEntity(entity, relativePos, relativeRotation, relativeVelocity, location);
                }

                var localTargetPos = Vec3.upFromBottomCenterOf(location.pos, 0.0);
                var frame = LevelSpaceHelper.INSTANCE.projectTeleportFrame(targetLevel, location, localTargetPos, relativeRotation, relativeVelocity);

                TeleportUtil.teleport(entity, targetLevel, frame.pos(), frame.angle(), frame.velocity());
                return true;
            }

            EntityUtils.chat(entity, Component.translatable("rifts.unlinked2"));
            return false;
        });

        DefaultTargets.registerDefaultTarget(ITEM, new ItemTarget() {
            @Override
            public long insert(ItemUnit unit, boolean simulate) {
                return 0;
            }

            @Override
            public long extract(ItemUnit unit, boolean simulate) {
                return 0;
            }

            @Override
            public List<ItemUnit> contents() {
                return List.of();
            }
        });

        DefaultTargets.registerDefaultTarget(FLUID, new FluidTarget() {
            @Override
            public long insert(FluidUnit unit, boolean simulate) {
                return 0;
            }

            @Override
            public long extract(FluidUnit unit, boolean simulate) {
                return 0;
            }

            @Override
            public List<FluidUnit> contents() {
                return List.of();
            }
        });

        DefaultTargets.registerDefaultTarget(REDSTONE, new RedstoneTarget() {

            @Override
            public boolean recieveSignal(int strength, Location location) {
                if (location != null) {
                    var targetLevel = location.getWorld();

                    if (targetLevel == null) return false;

                    RedstoneTarget target = TargetResolver.target(targetLevel, location.pos, REDSTONE);

                    if (target != null) {
                        return target.recieveSignal(strength, location);
                    }
                }

                return false;
            }
        });
    }

}
