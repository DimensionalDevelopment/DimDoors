package org.dimdev.dimdoors.item;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.api.Environment;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.entity.MaskEntity;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.item.component.CounterComponent;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import static net.fabricmc.api.EnvType.CLIENT;
import static net.minecraft.util.math.Direction.*;

	public class MaskWandItem extends Item {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String ID = "rift_configuration_tool";

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
			if(hit.getType().equals(HitResult.Type.BLOCK)) {
				MaskEntity mask = ModEntityTypes.MASK.create((ServerWorld) world, null, LiteralText.EMPTY, player, ((BlockHitResult) hit).getBlockPos(), SpawnReason.SPAWNER, true, false);
				world.spawnEntity(mask);
			}
		}

		return TypedActionResult.success(stack);
	}

	@Override
	@Environment(CLIENT)
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		if (I18n.hasTranslation(this.getTranslationKey() + ".info")) {
			list.add(new TranslatableText(this.getTranslationKey() + ".info"));
		}
	}
}
