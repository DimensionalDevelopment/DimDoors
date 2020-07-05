package org.dimdev.dimdoors.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
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

public class RiftRemoverItem extends Item {
    public static final String ID = "rift_remover";

    public RiftRemoverItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
        if (I18n.hasTranslation(getTranslationKey() + ".info")) {
            list.add(new TranslatableText(getTranslationKey() + ".info"));
        }
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        HitResult hit = player.rayTrace(RayTraceHelper.REACH_DISTANCE, 0, false);

        if (world.isClient) {
            if (!RayTraceHelper.hitsDetachedRift(hit, world)) {
                player.sendMessage(new TranslatableText("tools.rift_miss"), true);
                DetachedRiftBlockEntityRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.GRAPHICS.highlightRiftCoreFor;
            }
            return new TypedActionResult<>(ActionResult.FAIL, stack);
        }

        if (RayTraceHelper.hitsDetachedRift(hit, world)) {
            DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) world.getBlockEntity(new BlockPos(hit.getPos()));
            if (!rift.closing) {
                rift.setClosing(true);
                world.playSound(null, player.getBlockPos(), ModSoundEvents.RIFT_CLOSE, SoundCategory.BLOCKS, 0.6f, 1);
                stack.damage(10, player, a -> {});
                player.sendMessage(new TranslatableText(getTranslationKey() + ".closing"), true);
                return new TypedActionResult<>(ActionResult.SUCCESS, stack);
            } else {
                player.sendMessage(new TranslatableText(getTranslationKey() + ".already_closing"), true);
            }
        }
        return new TypedActionResult<>(ActionResult.FAIL, stack);
    }
}
