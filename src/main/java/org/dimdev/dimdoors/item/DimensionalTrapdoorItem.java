package org.dimdev.dimdoors.item;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public class DimensionalTrapdoorItem extends BlockItem {
	private final Consumer<? super EntranceRiftBlockEntity> setupFunction;

	public DimensionalTrapdoorItem(Block block, Properties settings, Consumer<? super EntranceRiftBlockEntity> setupFunction) {
		super(block, settings);
		this.setupFunction = setupFunction;
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if (world.isClientSide) {
			return super.place(context);
		}

		boolean replaceable = world.getBlockState(pos).canBeReplaced(context); // Check this before calling super, since that changes the block
		InteractionResult result = super.place(context);

		if (result == InteractionResult.SUCCESS) {
			if (!replaceable) {
				pos = pos.relative(context.getNearestLookingDirection());
			}

			BlockState state = world.getBlockState(pos);
			// Get the rift entity (not hard coded, works with any door size)
			EntranceRiftBlockEntity entranceRift = ((RiftProvider<EntranceRiftBlockEntity>) state.getBlock()).getRift(world, pos, state);

			// Configure the rift to its default functionality
			this.setupRift(entranceRift);

			// Register the rift in the registry
			entranceRift.setChanged();
			entranceRift.register();
		}

		return result;
	}

	protected void setupRift(EntranceRiftBlockEntity entranceRift) {
		this.setupFunction.accept(entranceRift);
	}
}
