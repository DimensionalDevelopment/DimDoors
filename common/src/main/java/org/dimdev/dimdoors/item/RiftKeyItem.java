package org.dimdev.dimdoors.item;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

public class RiftKeyItem extends Item {
	public RiftKeyItem(Item.Properties settings) {
		super(settings);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		if (isEmpty(stack)) {
			tooltip.add(Component.translatable("item.dimdoors.rift_key.no_links"));
		} else if (context.isAdvanced()) {
			for (UUID id : getIds(stack)) {
				tooltip.add(Component.literal(" " + id.toString()));
			}
		}
		super.appendHoverText(stack, world, tooltip, context);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return !isEmpty(stack);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 30;
	}

	@Override
	public void onCraftedBy(ItemStack stack, Level world, Player player) {
		stack.setTag(this.getDefaultInstance().getTag());
	}

	@Override
	public ItemStack getDefaultInstance() {
		ItemStack stack = super.getDefaultInstance();
		stack.addTagElement("Ids", ListTagAccessor.createListTag(new ArrayList<>(), (byte) Tag.TAG_INT_ARRAY));
		return stack;
	}

	@Override
	public InteractionResult useOn(UseOnContext  context) {
		if (context.getLevel().isClientSide) {
			return InteractionResult.CONSUME;
		}
		Player player = context.getPlayer();
		BlockState state = context.getLevel().getBlockState(context.getClickedPos());
		if (player != null && state.getBlock() instanceof RiftProvider && player.isShiftKeyDown()) {
			RiftBlockEntity riftBlockEntity = ((RiftProvider<?>) state.getBlock()).getRift(context.getLevel(), context.getClickedPos(), state);
			if (riftBlockEntity.isDetached()) {
				return super.useOn(context);
			}
			EntranceRiftBlockEntity entranceRiftBlockEntity = ((EntranceRiftBlockEntity) riftBlockEntity);
			Rift rift = DimensionalRegistry.getRiftRegistry().getRift(new Location(entranceRiftBlockEntity.getLevel().dimension(), entranceRiftBlockEntity.getBlockPos()));
			if (entranceRiftBlockEntity.isLocked()) {
				if (tryRemove(context.getItemInHand(), rift.getId())) {
					entranceRiftBlockEntity.setLocked(false);
					entranceRiftBlockEntity.setChanged();
					EntityUtils.chat(player, Component.translatable("rifts.unlocked"));
					ServerPacketHandler.get((ServerPlayer) player).sync(context.getItemInHand(), context.getHand());
					return InteractionResult.SUCCESS;
				} else {
					EntityUtils.chat(player, Component.translatable("rifts.cantUnlock"));
				}
			} else {
				entranceRiftBlockEntity.setLocked(true);
				add(context.getItemInHand(), rift.getId());
				entranceRiftBlockEntity.setChanged();
				EntityUtils.chat(player, Component.translatable("rifts.locked"));
				ServerPacketHandler.get((ServerPlayer) player).sync(context.getItemInHand(), context.getHand());
 				return InteractionResult.SUCCESS;
			}
		}
		return super.useOn(context);
	}

	public static boolean tryRemove(ItemStack stack, UUID id) {
		IntArrayTag arrayTag = new IntArrayTag(UUIDUtil.uuidToIntArray(id));
		return stack.getTag().getList("Ids", Tag.TAG_INT_ARRAY).remove(arrayTag);
	}

	public static void add(ItemStack stack, UUID id) {
		if (!has(stack, id)) {
			stack.getOrCreateTag().getList("Ids", Tag.TAG_INT_ARRAY).add(new IntArrayTag(UUIDUtil.uuidToIntArray(id)));
		}
	}

	public static boolean has(ItemStack stack, UUID id) {
		return stack.getOrCreateTag().getList("Ids", Tag.TAG_INT_ARRAY).contains(new IntArrayTag(UUIDUtil.uuidToIntArray(id)));
	}

	public static boolean isEmpty(ItemStack stack) {
		return stack.getOrCreateTag().getList("Ids", Tag.TAG_INT_ARRAY).isEmpty();
	}

	public static List<UUID> getIds(ItemStack stack) {
		return stack.getOrCreateTag()
				.getList("Ids", Tag.TAG_INT_ARRAY)
				.stream()
				.map(IntArrayTag.class::cast)
				.map(IntArrayTag::getAsIntArray)
				.map(UUIDUtil::uuidFromIntArray)
				.toList();
	}
}
