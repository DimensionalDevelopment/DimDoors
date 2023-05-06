package org.dimdev.dimdoors.world.pocket.type.addon;

import java.io.IOException;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class PreventBlockModificationAddon implements AutoSyncedAddon, AttackBlockCallback, PlayerBlockBreakEvents.Before, UseItemOnBlockCallback {
	public static ResourceLocation ID = DimensionalDoors.id("prevent_block_modification");

	//AttackBlockCallback
	@Override
	public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		if (player.isCreative()) return ActionResult.PASS;
		return ActionResult.FAIL;
	}

	@Override
	public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (player.isCreative()) return true;
		return false;
	}

	@Override
	public ActionResult useItemOnBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		if (player.isCreative()) return ActionResult.PASS;
		if (player.getStackInHand(hand).getItem() instanceof BlockItem) {
			BlockPos blockPos = hitResult.getBlockPos();
			BlockState blockState = world.getBlockState(blockPos);
			ActionResult result = blockState.onUse(world, player, hand, hitResult);
			if (result.isAccepted()) return result;

			return ActionResult.FAIL;
		}
		return ActionResult.PASS;
	}

	@Override
	public AutoSyncedAddon read(PacketByteBuf buf) throws IOException {
		return this;
	}

	@Override
	public PacketByteBuf write(PacketByteBuf buf) throws IOException {
		return buf;
	}

	@Override
	public PocketAddon fromNbt(NbtCompound nbt) {
		return this;
	}

	@Override
	public PocketAddonType<? extends PocketAddon> getType() {
		return PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON;
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
		public Identifier getId() {
			return ID;
		}

		@Override
		public PocketBuilderAddon<PreventBlockModificationAddon> fromNbt(NbtCompound nbt) {
			return this;
		}

		@Override
		public PocketAddonType<PreventBlockModificationAddon> getType() {
			return PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON;
		}
	}
}
