package org.dimdev.dimdoors.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.rift.targets.EntityTarget;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;

public class EternalFluidBlock extends FluidBlock {
    private static final EntityTarget TARGET = new EscapeTarget(true);

    public EternalFluidBlock(Block.Settings settings) {
        super(ModFluids.ETERNAL_FLUID, settings);
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        TARGET.receiveEntity(entity, entity.yaw, 0);
    }
}
