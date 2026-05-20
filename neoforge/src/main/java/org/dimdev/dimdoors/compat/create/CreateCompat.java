package org.dimdev.dimdoors.compat.create;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;

public final class CreateCompat {
    private CreateCompat() {
    }

    public static void init() {
        DimensionalDoors.getSided().registerRunnable(Registries.BLOCK_ENTITY_TYPE, CreateCompatBlockEntityTypes::init);

        BlockMovementChecks.registerMovementAllowedCheck((state, world, pos) ->
                state.getBlock() instanceof RiftProvider<?> ? CheckResult.SUCCESS : CheckResult.PASS);
        BlockMovementChecks.registerMovementNecessaryCheck((state, world, pos) ->
                state.getBlock() instanceof RiftProvider<?> ? CheckResult.SUCCESS : CheckResult.PASS);

        DimensionalDoors.getDimensionalDoorBlockRegistrar().registerCustomDoorProduction(new DimensionalDoorBlockRegistrar.DoorProductionSelector() {
            @Override
            public boolean test(ResourceLocation id, Block block) {
                return block instanceof SlidingDoorBlock;
            }
        }, new DimensionalDoorBlockRegistrar.DoorProduction() {
            @Override
            public Block createDoor(DimensionalDoorBlockRegistrar.GeneratedDoorContext context, DoorBlock originalBlock) {
                return new SlidingDimensionalDoorBlock(context.properties(), originalBlock);
            }

            @Override
            public Block createTrapdoor(DimensionalDoorBlockRegistrar.GeneratedDoorContext context, TrapDoorBlock originalBlock) {
                return null;
            }

            @Override
            public void onBlockRegistered(DimensionalDoorBlockRegistrar.GeneratedDoorContext context, Block generatedBlock) {
                CreateCompatBlockEntityTypes.SLIDING_ENTRANCE_RIFT.addBlock(generatedBlock);
            }
        });
    }
}
