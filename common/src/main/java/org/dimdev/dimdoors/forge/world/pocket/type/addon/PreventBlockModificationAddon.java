package org.dimdev.dimdoors.forge.world.pocket.type.addon;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;
import org.dimdev.dimdoors.forge.world.pocket.type.Pocket;

public class PreventBlockModificationAddon implements AutoSyncedAddon { //InteractionEvent.LeftClickBlock/*, PlayerBlockBreakEvents.Before TODO: Figure out*/, UseItemOnBlockCallback {
	public static ResourceLocation ID = DimensionalDoors.id("prevent_block_modification");

	//AttackBlockCallback

//	@Override
//	public EventResult click(Player player, InteractionHand hand, BlockPos pos, Direction face) {
//		if (player.isCreative()) return EventResult.pass();
//		return EventResult.interruptFalse();
//	}

//	@Override TODO: Figure out
//	public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
//		if (player.isCreative()) return true;
//		return false;
//	}


//	@Override
//	public InteractionResult useItemOnBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
//		if (player.isCreative()) return InteractionResult.PASS;
//		if (player.getItemInHand(hand).getItem() instanceof BlockItem) {
//			BlockPos blockPos = hitResult.getBlockPos();
//			BlockState blockState = world.getBlockState(blockPos);
//			InteractionResult result = blockState.use(world, player, hand, hitResult);
//			if (result.consumesAction()) return result;
//
//			return InteractionResult.FAIL;
//		}
//		return InteractionResult.PASS;
//	}

	@Override
	public AutoSyncedAddon read(FriendlyByteBuf buf) {
		return this;
	}

	@Override
	public FriendlyByteBuf write(FriendlyByteBuf buf) {
		return buf;
	}

	@Override
	public PocketAddon fromNbt(CompoundTag nbt) {
		return this;
	}

	@Override
	public PocketAddonType<? extends PocketAddon> getType() {
		return PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON.get();
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public static class PreventBlockModificationBuilderAddon implements PocketBuilderAddon<PreventBlockModificationAddon> {

		@Override
		public void apply(Pocket pocket) {
			pocket.addAddon(new PreventBlockModificationAddon());
		}

		@Override
		public ResourceLocation getId() {
			return ID;
		}

		@Override
		public PocketBuilderAddon<PreventBlockModificationAddon> fromNbt(CompoundTag nbt) {
			return this;
		}

		@Override
		public PocketAddonType<PreventBlockModificationAddon> getType() {
			return PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON.get();
		}
	}
}
