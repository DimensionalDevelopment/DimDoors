package org.dimdev.dimdoors.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import org.dimdev.dimdoors.item.component.ModDataComponents;
import org.dimdev.dimdoors.item.component.RiftKeyIds;
import org.dimdev.dimdoors.network.Networking;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import java.util.*;

public class RiftKeyItem extends Item {
	public RiftKeyItem(Item.Properties settings) {
		super(settings.component(ModDataComponents.RIFT_KEY_IDS.value(), new RiftKeyIds(new HashSet<>())));
	}


	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
		if (isEmpty(stack)) {
			tooltip.add(Component.translatable("item.dimdoors.rift_key.no_links"));
		} else if (tooltipFlag.isAdvanced()) {
			for (UUID id : getIds(stack)) {
				tooltip.add(Component.literal(" " + id.toString()));
			}
		}
		super.appendHoverText(stack, context, tooltip, tooltipFlag);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return !isEmpty(stack);
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 30;
	}

	@Override
	public void onCraftedBy(ItemStack stack, Level world, Player player) {
		stack.applyComponents(this.getDefaultInstance().getComponents());
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
					Networking.sync((ServerPlayer) player, context.getItemInHand(), context.getHand());
					return InteractionResult.SUCCESS;
				} else {
					EntityUtils.chat(player, Component.translatable("rifts.cantUnlock"));
				}
			} else {
				entranceRiftBlockEntity.setLocked(true);
				add(context.getItemInHand(), rift.getId());
				entranceRiftBlockEntity.setChanged();
				EntityUtils.chat(player, Component.translatable("rifts.locked"));
				Networking.sync((ServerPlayer) player, context.getItemInHand(), context.getHand());
 				return InteractionResult.SUCCESS;
			}
		}
		return super.useOn(context);
	}

	public static RiftKeyIds getRifKeyIds(ItemStack stack) {
		if (stack.has(ModDataComponents.RIFT_KEY_IDS.value()))
			return stack.get(ModDataComponents.RIFT_KEY_IDS.value());
		else return null;
	}

	public static Set<UUID> getIds(ItemStack stack) {
		var data = getRifKeyIds(stack);

		if(data != null)
			return data.ids();
		else return Collections.emptySet();
	}

	public static boolean tryRemove(ItemStack stack, UUID id) {
		var data = getRifKeyIds(stack);

		if(data != null) {
			return data.ids().remove(id);
		} else {
			return false;
		}
	}

	public boolean add(ItemStack stack, UUID id) {
		var data = getRifKeyIds(stack);

		if(data != null) {
			return data.ids().add(id);
		} else {
			return false;
		}
	}

	public static boolean has(ItemStack stack, UUID id) {
		var data = getRifKeyIds(stack);

		if(data != null) {
			return data.ids().contains(id);
		} else {
			return false;
		}
	}

	public static boolean isEmpty(ItemStack stack) {
		var data = getRifKeyIds(stack);

		if(data != null) {
			return data.ids().isEmpty();
		} else {
			return false;
		}
	}
}
