package org.dimdev.dimdoors.item;

import java.util.List;
import java.util.Objects;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.ToolTipHelper;
import org.dimdev.dimdoors.sound.ModSoundEvents;

public class RiftRemoverItem extends Item {
	public static final String ID = "rift_remover";
	public static final Identifier REMOVED_RIFT_LOOT_TABLE = DimensionalDoors.id("removed_rift");

	public RiftRemoverItem(Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
		ToolTipHelper.processTranslation(list, this.getTranslationKey() + ".info");
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		HitResult hit = player.raycast(RaycastHelper.REACH_DISTANCE, 0, false);

		if (world.isClient) {
			if (!RaycastHelper.hitsDetachedRift(hit, world)) {
				player.sendMessage(MutableText.of(new TranslatableTextContent("tools.rift_miss")), true);
				RiftBlockEntity.showRiftCoreUntil = System.currentTimeMillis() + DimensionalDoors.getConfig().getGraphicsConfig().highlightRiftCoreFor;
			}
			return new TypedActionResult<>(ActionResult.FAIL, stack);
		}

		if (RaycastHelper.hitsDetachedRift(hit, world)) {
			// casting to BlockHitResult is mostly safe since RaycastHelper#hitsDetachedRift already checks hit type
			DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) world.getBlockEntity(((BlockHitResult) hit).getBlockPos());
			if (!Objects.requireNonNull(rift).closing) {
				rift.setClosing(true);
				world.playSound(null, player.getBlockPos(), ModSoundEvents.RIFT_CLOSE, SoundCategory.BLOCKS, 0.6f, 1);
				stack.damage(10, player, a -> a.sendToolBreakStatus(hand));
				LootContext ctx = new LootContext.Builder((ServerWorld) world).random(world.random).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(((BlockHitResult) hit).getBlockPos())).optionalParameter(LootContextParameters.THIS_ENTITY, player).build(LootContextTypes.GENERIC);
				((ServerWorld) world).getServer().getLootManager().getTable(REMOVED_RIFT_LOOT_TABLE).generateLoot(ctx).forEach(stack1 -> {
					ItemScatterer.spawn(world, ((BlockHitResult) hit).getBlockPos().getX(), ((BlockHitResult) hit).getBlockPos().getY(), ((BlockHitResult) hit).getBlockPos().getZ(), stack1);
				});

				player.sendMessage(MutableText.of(new TranslatableTextContent(this.getTranslationKey() + ".closing")), true);
				return new TypedActionResult<>(ActionResult.SUCCESS, stack);
			} else {
				player.sendMessage(MutableText.of(new TranslatableTextContent(this.getTranslationKey() + ".already_closing")), true);
			}
		}
		return new TypedActionResult<>(ActionResult.FAIL, stack);
	}
}
