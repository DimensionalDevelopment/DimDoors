package org.dimdev.dimdoors.item;

import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.entity.ModEntityTypes;

import java.util.List;

import static net.fabricmc.api.EnvType.CLIENT;

	public class MaskWandItem extends Item {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String ID = "mask_wand_tool";

	public MaskWandItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		HitResult hit = player.raycast(RaycastHelper.REACH_DISTANCE, 0, false);

		if (world.isClient) {
			return TypedActionResult.fail(stack);
		} else {
			if(hit.getType().equals(HitResult.Type.BLOCK))
				ModEntityTypes.CYCLOPS_MASK.spawn((ServerWorld) world, null, Text.empty(), player, ((BlockHitResult) hit).getBlockPos(), SpawnReason.SPAWNER, true, false);
		}

		return TypedActionResult.success(stack);
	}

	@Override
	@Environment(CLIENT)
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		if (I18n.hasTranslation(this.getTranslationKey() + ".info")) {
			list.add(MutableText.of(new TranslatableTextContent(this.getTranslationKey() + ".info")));
		}
	}
}
