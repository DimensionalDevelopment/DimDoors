package org.dimdev.dimdoors.item;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import static net.fabricmc.api.Dist.CLIENT;

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

		if (world.isClientSide) {
			return InteractionResultHolder.fail(stack);
		} else {
			if(hit.getType().equals(HitResult.Type.BLOCK)) {
//				MaskEntity mask = ModEntityTypes.MASK.create((ServerWorld) world, null, LiteralText.EMPTY, player, ((BlockHitResult) hit).getBlockPos(), SpawnReason.SPAWNER, true, false);
//				world.spawnEntity(mask);
			}
		}

		return InteractionResultHolder.success(stack);
	}

	@Override
	@Environment(CLIENT)
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> list, TooltipFlag tooltipContext) {
		if (I18n.exists(this.getDescriptionId() + ".info")) {
			list.add(MutableComponent.create(new TranslatableContents(this.getDescriptionId() + ".info")));
		}
	}
}
