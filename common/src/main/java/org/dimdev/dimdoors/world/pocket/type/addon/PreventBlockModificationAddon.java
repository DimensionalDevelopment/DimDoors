package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public enum PreventBlockModificationAddon implements PocketAddon { //InteractionEvent.LeftClickBlock/*, PlayerBlockBreakEvents.Before TODO: Figure out*/, UseItemOnBlockCallback {
	INSTANCE;

	public static ResourceLocation ID = DimensionalDoors.id("prevent_block_modification");

	public static MapCodec<PreventBlockModificationAddon> CODEC = MapCodec.unit(INSTANCE);
	public static StreamCodec<RegistryFriendlyByteBuf, PreventBlockModificationAddon> STREAM_CODEC = StreamCodec.unit(INSTANCE);

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
	public PocketAddonType<PreventBlockModificationAddon, PreventBlockModificationBuilderAddon> getType() {
		return PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON.get();
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public static class PreventBlockModificationBuilderAddon implements PocketBuilderAddon<PreventBlockModificationAddon, PreventBlockModificationBuilderAddon> {
		public static MapCodec<PreventBlockModificationBuilderAddon> CODEC = MapCodec.unit(new PreventBlockModificationBuilderAddon());

		@Override
		public void apply(Pocket pocket) {
			pocket.addAddon(INSTANCE);
		}

		@Override
		public ResourceLocation getId() {
			return ID;
		}

		@Override
		public PocketAddonType<PreventBlockModificationAddon, PreventBlockModificationBuilderAddon> getType() {
			return PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON.get();
		}
	}
}
