package org.dimdev.dimdoors.item;

import java.util.List;

import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.DetachedRiftBlockEntityRenderer;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
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
import static net.fabricmc.api.EnvType.CLIENT;

public class RiftConfigurationToolItem extends Item {

    public static final String ID = "rift_configuration_tool";

    RiftConfigurationToolItem() {
        super(new Item.Settings().group(ModItems.DIMENSIONAL_DOORS).maxCount(1).maxDamage(16));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        HitResult hit = player.rayTrace(RayTraceHelper.REACH_DISTANCE, 0, false);

        if (world.isClient) {
            if (!RayTraceHelper.hitsRift(hit, world)) {
                player.sendMessage(new TranslatableText("tools.rift_miss"), true);
                DetachedRiftBlockEntityRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.GRAPHICS.highlightRiftCoreFor;
            }
            return new TypedActionResult<>(ActionResult.FAIL, stack);
        }

        if (RayTraceHelper.hitsRift(hit, world)) {
            RiftBlockEntity rift = (RiftBlockEntity) world.getBlockEntity(new BlockPos(hit.getPos()));

            System.out.println(rift);

            //TODO: implement this tool's functionality
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new TypedActionResult<>(ActionResult.FAIL, stack);
    }

    @Override
    @Environment(CLIENT)
    public void appendTooltip(ItemStack itemStack, World world, List<Text> list, TooltipContext tooltipContext) {
        if (I18n.hasTranslation(this.getTranslationKey() + ".info")) {
            list.add(new TranslatableText(this.getTranslationKey() + ".info"));
        }
    }
}
