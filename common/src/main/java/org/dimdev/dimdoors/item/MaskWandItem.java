package org.dimdev.dimdoors.item;

import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.entity.MaskEntity;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.fabricmc.api.EnvType.CLIENT;

	public class MaskWandItem extends Item {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String ID = "rift_configuration_tool";

	public MaskWandItem(Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		HitResult hit = player.pick(RaycastHelper.REACH_DISTANCE, 0, false);

		if (world.isClientSide()) {
			return InteractionResultHolder.fail(stack);
		} else {
			if(hit.getType().equals(HitResult.Type.BLOCK)) {
				MaskEntity mask = ModEntityTypes.MASK.get().create((ServerLevel) world, a -> {}, ((BlockHitResult) hit).getBlockPos(), MobSpawnType.SPAWN_EGG, true, false);
				world.addFreshEntity(mask);
			}
		}

		return InteractionResultHolder.success(stack);
	}

	@Environment(CLIENT)
	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable TooltipContext level, List<Component> list, TooltipFlag tooltipFlag) {
		if (I18n.exists(this.getDescriptionId() + ".info")) {
			list.add(Component.translatable(this.getDescriptionId() + ".info"));
		}
	}
}
