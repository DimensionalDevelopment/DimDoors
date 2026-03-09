package org.dimdev.dimdoors.item.door;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.client.ToolTipHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class DimensionalTrapdoorItem extends BlockItem {
	private final Consumer<? super EntranceRiftBlockEntity> setupFunction;
    private boolean hasToolTip = false;


    public DimensionalTrapdoorItem(Block block, Properties settings, Consumer<? super EntranceRiftBlockEntity> setupFunction) {
        this(block, settings, setupFunction, false);
    }

    public DimensionalTrapdoorItem(Block block, Properties settings, Consumer<? super EntranceRiftBlockEntity> setupFunction, boolean hasToolTip) {
        super(block, settings);
        this.setupFunction = setupFunction;
        this.hasToolTip = hasToolTip;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable TooltipContext world, List<Component> list, TooltipFlag tooltipContext) {
        if(hasToolTip) {
            ToolTipHelper.processTranslation(list, this.getDescriptionId() + ".info");
        }
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
