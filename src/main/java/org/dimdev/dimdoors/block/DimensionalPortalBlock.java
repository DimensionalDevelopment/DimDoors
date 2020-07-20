package org.dimdev.dimdoors.block;

import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DimensionalPortalBlock extends Block implements RiftProvider<EntranceRiftBlockEntity> {
    public DimensionalPortalBlock(Settings settings) {
        super(settings);
    }

    @Override
    public EntranceRiftBlockEntity getRift(World world, BlockPos pos, BlockState state) {
        return (EntranceRiftBlockEntity) this.createBlockEntity(world);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new EntranceRiftBlockEntity();
    }
}
