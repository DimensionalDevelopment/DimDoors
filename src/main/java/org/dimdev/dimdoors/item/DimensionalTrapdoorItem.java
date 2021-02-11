package org.dimdev.dimdoors.item;

import java.util.function.Consumer;

import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DimensionalTrapdoorItem extends BlockItem {
	private final Consumer<? super EntranceRiftBlockEntity> setupFunction;

	public DimensionalTrapdoorItem(Block block, Settings settings, Consumer<? super EntranceRiftBlockEntity> setupFunction) {
		super(block, settings);
		this.setupFunction = setupFunction;
	}

	@Override
	public ActionResult place(ItemPlacementContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();

		if (world.isClient) {
			return super.place(context);
		}

		boolean replaceable = world.getBlockState(pos).canReplace(context); // Check this before calling super, since that changes the block
		ActionResult result = super.place(context);

		if (result == ActionResult.SUCCESS) {
			if (!replaceable) {
				pos = pos.offset(context.getPlayerLookDirection());
			}

			BlockState state = world.getBlockState(pos);
			// Get the rift entity (not hard coded, works with any door size)
			EntranceRiftBlockEntity entranceRift = ((RiftProvider<EntranceRiftBlockEntity>) state.getBlock()).getRift(world, pos, state);

			// Configure the rift to its default functionality
			this.setupRift(entranceRift);

			// Register the rift in the registry
			entranceRift.markDirty();
			entranceRift.register();
		}

		return result;
	}

	protected void setupRift(EntranceRiftBlockEntity entranceRift) {
		this.setupFunction.accept(entranceRift);
	}
}
