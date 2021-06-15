package org.dimdev.dimdoors.item;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.BlockItem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.ToolTipHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DimensionalDoorItem extends BlockItem {
	private final Consumer<? super EntranceRiftBlockEntity> setupFunction;
	private boolean hasToolTip = false;

	public DimensionalDoorItem(Block block, Settings settings, Consumer<? super EntranceRiftBlockEntity> setupFunction) {
		this(block, settings, setupFunction, false);
	}

	public DimensionalDoorItem(Block block, Settings settings, Consumer<? super EntranceRiftBlockEntity> setupFunction, boolean hasToolTip) {
		super(block, settings);
		this.setupFunction = setupFunction;
		this.hasToolTip = hasToolTip;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		if(hasToolTip) {
			ToolTipHelper.processTranslation(list, this.getTranslationKey() + ".info");
		}
	}

	@Override
	public ActionResult place(ItemPlacementContext context) {
		BlockPos pos = context.getBlockPos();

		if (!context.getWorld().getBlockState(pos).canReplace(context)) {
			pos = pos.offset(context.getPlayerFacing());
		}

		boolean placedOnRift = context.getWorld().getBlockState(pos).getBlock() == ModBlocks.DETACHED_RIFT;

		if (!placedOnRift && !context.getPlayer().isSneaking() && isRiftNear(context.getWorld(), pos)) {
			// Allowing on second right click would require cancelling client-side, which
			// is impossible (see https://github.com/MinecraftForge/MinecraftForge/issues/3272)
			// without sending custom packets.

			if (context.getWorld().isClient) {
				context.getPlayer().sendMessage(new TranslatableText("rifts.entrances.rift_too_close"), true);
				RiftBlockEntity.showRiftCoreUntil = System.currentTimeMillis() + DimensionalDoorsInitializer.getConfig().getGraphicsConfig().highlightRiftCoreFor;
			}

			return ActionResult.FAIL;
		}

		if (context.getWorld().isClient) {
			return super.place(context);
		}

		// Store the rift entity if there's a rift block there that may be broken
		DetachedRiftBlockEntity rift = null;
		if (placedOnRift) {
			rift = (DetachedRiftBlockEntity) context.getWorld().getBlockEntity(pos);
			rift.setUnregisterDisabled(true);
		}

		ActionResult result = super.place(context);
		if (result == ActionResult.SUCCESS || result == ActionResult.CONSUME) {
			BlockState state = context.getWorld().getBlockState(pos);
			if (rift == null) {
				// Get the rift entity (not hard coded, works with any door size)
				@SuppressWarnings("unchecked") // Guaranteed to be IRiftProvider<TileEntityEntranceRift> because of constructor
				EntranceRiftBlockEntity entranceRift = ((RiftProvider<EntranceRiftBlockEntity>) state.getBlock()).getRift(context.getWorld(), pos, state);

				// Configure the rift to its default functionality
				this.setupRift(entranceRift);

				// Register the rift in the registry
				entranceRift.markDirty();
				entranceRift.register();
			} else {
				// Copy from the old rift
				EntranceRiftBlockEntity newRift = (EntranceRiftBlockEntity) context.getWorld().getBlockEntity(pos);
				newRift.copyFrom(rift);
				newRift.updateType();
			}
		} else if (rift != null) {
			rift.setUnregisterDisabled(false);
		}

		return result;
	}

	public static boolean isRiftNear(World world, BlockPos pos) {
		for (int x = pos.getX() - 5; x < pos.getX() + 5; x++) {
			for (int y = pos.getY() - 5; y < pos.getY() + 5; y++) {
				for (int z = pos.getZ() - 5; z < pos.getZ() + 5; z++) {
					BlockPos searchPos = new BlockPos(x, y, z);
					if (world.getBlockState(searchPos).getBlock() == ModBlocks.DETACHED_RIFT) {
						DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) world.getBlockEntity(searchPos);
						if (Math.sqrt(pos.getSquaredDistance(searchPos)) < rift.size) {
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
