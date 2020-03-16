package org.dimdev.dimdoors.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.client.DetachedRiftBlockEntityRenderer;
import org.dimdev.dimdoors.sound.ModSoundEvents;


import java.util.List;

public class RiftStabilizerItem extends Item {
    public static final String ID = "rift_stabilizer";

    public RiftStabilizerItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        HitResult hit = player.rayTrace(RayTraceHelper.REACH_DISTANCE, 0, false);

        if (world.isClient) {
            if (RayTraceHelper.hitsDetachedRift(hit, world)) {
                // TODO: not necessarily success, fix this and all other similar cases to make arm swing correct
                return new TypedActionResult<>(ActionResult.SUCCESS, stack);
            } else {
                player.sendMessage(new TranslatableText("tools.rift_miss"));
                DetachedRiftBlockEntityRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.GRAPHICS.highlightRiftCoreFor;
                return new TypedActionResult<>(ActionResult.FAIL, stack);
            }
        }

        if (RayTraceHelper.hitsDetachedRift(hit, world)) {
            DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) world.getBlockEntity(new BlockPos(hit.getPos()));
            if (!rift.stabilized && !rift.closing) {
                rift.setStabilized(true);
                world.playSound(null, player.getSenseCenterPos(), ModSoundEvents.RIFT_CLOSE, SoundCategory.BLOCKS, 0.6f, 1); // TODO: different sound
                stack.damage(1, player, a -> {});
                player.sendMessage(new TranslatableText(getTranslationKey() + ".stabilized"));
                return new TypedActionResult<>(ActionResult.SUCCESS, stack);
            } else {
                player.sendMessage(new TranslatableText(getTranslationKey() + ".already_stabilized"));
            }
        }
        return new TypedActionResult<>(ActionResult.FAIL, stack);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack itemStack,  World world, List<Text> list, TooltipContext tooltipContext) {
        list.add(new TranslatableText(getTranslationKey() + ".info"));
    }
}
