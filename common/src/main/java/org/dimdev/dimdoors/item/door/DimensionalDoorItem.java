package org.dimdev.dimdoors.item.door;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.ToolTipHelper;
import org.dimdev.dimdoors.item.RaycastHelper;
import org.dimdev.dimdoors.listener.UseDoorItemOnBlockCallbackListener;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static org.dimdev.dimdoors.item.RaycastHelper.DETACH;

public class DimensionalDoorItem extends BlockItem {
	private final Consumer<? super EntranceRiftBlockEntity> setupFunction;
	private boolean hasToolTip = false;

	public DimensionalDoorItem(Block block, Properties settings, Consumer<? super EntranceRiftBlockEntity> setupFunction) {
		this(block, settings, setupFunction, false);
	}

	public DimensionalDoorItem(Block block, Properties settings, Consumer<? super EntranceRiftBlockEntity> setupFunction, boolean hasToolTip) {
		super(block, settings);
		this.setupFunction = setupFunction;
		this.hasToolTip = hasToolTip;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendHoverText(ItemStack itemStack,  @Nullable Level world, List<Component> list, TooltipFlag tooltipContext) {
		if(hasToolTip) {
			ToolTipHelper.processTranslation(list, this.getDescriptionId() + ".info");
		}
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		context = new UseDoorItemOnBlockCallbackListener.DimDoorBlockPlaceContext(context, RaycastHelper.findDetachRift(context.getPlayer(), DETACH));

		BlockPos pos = context.getClickedPos();

		boolean placedOnRift = context.getLevel().getBlockState(pos).getBlock() == ModBlocks.DETACHED_RIFT.get();

		if (!placedOnRift && !context.getPlayer().isShiftKeyDown() && isRiftNear(context.getLevel(), pos)) {
			// Allowing on second right click would require cancelling client-side, which
			// is impossible (see https://github.com/MinecraftForge/MinecraftForge/issues/3272)
			// without sending custom packets.

			if (context.getLevel().isClientSide) {
				context.getPlayer().displayClientMessage(Component.translatable("rifts.entrances.rift_too_close"), true);
				RiftBlockEntity.showRiftCoreUntil = System.currentTimeMillis() + DimensionalDoors.getConfig().getGraphicsConfig().highlightRiftCoreFor;
			}

			return InteractionResult.FAIL;
		}

		if (context.getLevel().isClientSide) {
			return super.place(context);
		}

		// Store the rift entity if there's a rift block there that may be broken
		DetachedRiftBlockEntity rift = null;
		if (placedOnRift) {
			rift = (DetachedRiftBlockEntity) context.getLevel().getBlockEntity(pos);
			rift.setUnregisterDisabled(true);
			context.getLevel().removeBlock(pos, false);
		}

		InteractionResult result = super.place(context);
		if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
			BlockState state = context.getLevel().getBlockState(pos);
			if (rift == null) {
				// Get the rift entity (not hard coded, works with any door size)
				@SuppressWarnings("unchecked") // Guaranteed to be IRiftProvider<TileEntityEntranceRift> because of constructor
				EntranceRiftBlockEntity entranceRift = ((RiftProvider<EntranceRiftBlockEntity>) state.getBlock()).getRift(context.getLevel(), pos, state);

				// Configure the rift to its default functionality
				this.setupRift(entranceRift);

				// Register the rift in the registry
				entranceRift.setChanged();
				entranceRift.register();
			} else {
				// Copy from the old rift
				EntranceRiftBlockEntity newRift = (EntranceRiftBlockEntity) context.getLevel().getBlockEntity(pos);
				newRift.copyFrom(rift);
				newRift.updateType();
			}
		} else if (rift != null) {
			rift.setUnregisterDisabled(false);
		}

		return result;
	}

	public static boolean isRiftNear(Level world, BlockPos pos) {
		for (int x = pos.getX() - 5; x < pos.getX() + 5; x++) {
			for (int y = pos.getY() - 5; y < pos.getY() + 5; y++) {
				for (int z = pos.getZ() - 5; z < pos.getZ() + 5; z++) {
					BlockPos searchPos = new BlockPos(x, y, z);
					if (world.getBlockState(searchPos).getBlock() == ModBlocks.DETACHED_RIFT.get()) {
						DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) world.getBlockEntity(searchPos);
						if (Math.sqrt(pos.distSqr(searchPos)) < rift.size) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public void setupRift(EntranceRiftBlockEntity entranceRift) {
		this.setupFunction.accept(entranceRift);
	}
}
