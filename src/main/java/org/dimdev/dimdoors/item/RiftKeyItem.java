package org.dimdev.dimdoors.item;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.mixin.accessor.ListTagAccessor;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RiftKeyItem extends Item {
	public RiftKeyItem(Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (isEmpty(stack)) {
			tooltip.add(MutableText.of(new TranslatableTextContent("item.dimdoors.rift_key.no_links")));
		} else if (context.isAdvanced()) {
			for (UUID id : getIds(stack)) {
				tooltip.add(Text.of(" " + id.toString()));
			}
		}
		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return !isEmpty(stack);
	}

	@Override
	public boolean isNbtSynced() {
		return super.isNbtSynced();
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 30;
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		stack.setNbt(this.getDefaultStack().getNbt());
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack stack = super.getDefaultStack();
		stack.setSubNbt("Ids", ListTagAccessor.createListTag(new ArrayList<>(), (byte) NbtType.INT_ARRAY));
		return stack;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getWorld().isClient) {
			return ActionResult.CONSUME;
		}
		PlayerEntity player = context.getPlayer();
		BlockState state = context.getWorld().getBlockState(context.getBlockPos());
		if (player != null && state.getBlock() instanceof RiftProvider && player.isSneaky()) {
			RiftBlockEntity riftBlockEntity = ((RiftProvider<?>) state.getBlock()).getRift(context.getWorld(), context.getBlockPos(), state);
			if (riftBlockEntity.isDetached()) {
				return super.useOnBlock(context);
			}
			EntranceRiftBlockEntity entranceRiftBlockEntity = ((EntranceRiftBlockEntity) riftBlockEntity);
			Rift rift = DimensionalRegistry.getRiftRegistry().getRift(new Location(entranceRiftBlockEntity.getWorld().getRegistryKey(), entranceRiftBlockEntity.getPos()));
			if (entranceRiftBlockEntity.isLocked()) {
				if (tryRemove(context.getStack(), rift.getId())) {
					entranceRiftBlockEntity.setLocked(false);
					entranceRiftBlockEntity.markDirty();
					EntityUtils.chat(player, MutableText.of(new TranslatableTextContent("rifts.unlocked")));
					ServerPacketHandler.get((ServerPlayerEntity) player).sync(context.getStack(), context.getHand());
					return ActionResult.SUCCESS;
				} else {
					EntityUtils.chat(player, MutableText.of(new TranslatableTextContent("rifts.cantUnlock")));
				}
			} else {
				entranceRiftBlockEntity.setLocked(true);
				add(context.getStack(), rift.getId());
				entranceRiftBlockEntity.markDirty();
				EntityUtils.chat(player, MutableText.of(new TranslatableTextContent("rifts.locked")));
				ServerPacketHandler.get((ServerPlayerEntity) player).sync(context.getStack(), context.getHand());
 				return ActionResult.SUCCESS;
			}
		}
		return super.useOnBlock(context);
	}

	public static boolean tryRemove(ItemStack stack, UUID id) {
		NbtIntArray arrayTag = new NbtIntArray(Uuids.toIntArray(id));
		return stack.getNbt().getList("Ids", NbtType.INT_ARRAY).remove(arrayTag);
	}

	public static void add(ItemStack stack, UUID id) {
		if (!has(stack, id)) {
			stack.getOrCreateNbt().getList("Ids", NbtType.INT_ARRAY).add(new NbtIntArray(Uuids.toIntArray(id)));
		}
	}

	public static boolean has(ItemStack stack, UUID id) {
		return stack.getOrCreateNbt().getList("Ids", NbtType.INT_ARRAY).contains(new NbtIntArray(Uuids.toIntArray(id)));
	}

	public static boolean isEmpty(ItemStack stack) {
		return stack.getOrCreateNbt().getList("Ids", NbtType.INT_ARRAY).isEmpty();
	}

	public static List<UUID> getIds(ItemStack stack) {
		return stack.getOrCreateNbt()
				.getList("Ids", NbtType.INT_ARRAY)
				.stream()
				.map(NbtIntArray.class::cast)
				.map(NbtIntArray::getIntArray)
				.map(Uuids::toUuid)
				.collect(Collectors.toList());
	}
}
