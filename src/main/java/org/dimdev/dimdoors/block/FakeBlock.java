package org.dimdev.dimdoors.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.jetbrains.annotations.Nullable;

public class FakeBlock extends Block implements BlockEntityProvider {
	public FakeBlock(Settings settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntityTypes.FAKE_BLOCK.instantiate(pos, state);
	}
}
