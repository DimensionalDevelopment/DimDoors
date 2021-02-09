package org.dimdev.dimdoors.item;

import java.util.List;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.api.Environment;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimdoors.util.EntityUtils;
import org.dimdev.dimdoors.world.level.Counter;

import static net.fabricmc.api.EnvType.CLIENT;

public class RiftConfigurationToolItem extends Item {

	public static final String ID = "rift_configuration_tool";

	RiftConfigurationToolItem() {
		super(new Item.Settings().group(ModItems.DIMENSIONAL_DOORS).maxCount(1).maxDamage(16));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		HitResult hit = player.raycast(RaycastHelper.REACH_DISTANCE, 0, false);

		if (world.isClient) {
			if (!RaycastHelper.hitsRift(hit, world)) {
				EntityUtils.chat(player, new TranslatableText("tools.rift_miss"));
				RiftBlockEntity.showRiftCoreUntil = System.currentTimeMillis() + DimensionalDoorsInitializer.CONFIG.getGraphicsConfig().highlightRiftCoreFor;
			}
			return new TypedActionResult<>(ActionResult.FAIL, stack);
		} else {
			Counter counter = Counter.get(stack);

			if (RaycastHelper.hitsRift(hit, world)) {
				RiftBlockEntity rift = (RiftBlockEntity) world.getBlockEntity(new BlockPos(hit.getPos()));

				if (rift.getDestination() instanceof IdMarker) {
					EntityUtils.chat(player, Text.of("Id: " + ((IdMarker) rift.getDestination()).getId()));
				} else {
					int id = counter.increment();
					EntityUtils.chat(player, Text.of("Rift stripped of data and set to target id: : " + id));

					rift.setDestination(new IdMarker(id));
				}

				return new TypedActionResult<>(ActionResult.SUCCESS, stack);
			} else {
				if(player.isSneaking()) {
					counter.clear();
					EntityUtils.chat(player, Text.of("Counter has been reset."));
				} else {
					EntityUtils.chat(player, Text.of("Current Count: " + counter.count()));
				}
			}
		}

		return new TypedActionResult<>(ActionResult.SUCCESS, stack);
	}

	@Override
	@Environment(CLIENT)
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		if (I18n.hasTranslation(this.getTranslationKey() + ".info")) {
			list.add(new TranslatableText(this.getTranslationKey() + ".info"));
		}
	}
}
