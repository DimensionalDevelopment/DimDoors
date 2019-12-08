package org.dimdev.dimdoors.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.HitResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.client.TileEntityFloatingRiftRenderer;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.tileentities.DetachedRiftBlockEntity;

import java.util.List;

public class RiftStabilizerItem extends Item {
    public static final String ID = "rift_stabilizer";

    public RiftStabilizerItem(Settings settings) {
        setMaxStackSize(1);
        setMaxDamage(6); // TODO: Add more uses and make it reduce rift growth speed instead?
        setCreativeTab(ModItemGroups.DIMENSIONAL_DOORS);
        setTranslationKey(ID);
        setRegistryName(new Identifier("dimdoors", ID));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        HitResult hit = player.rayTrace(RayTraceHelper.REACH_DISTANCE, 0, false);

        if (world.isClient) {
            if (RayTraceHelper.hitsDetachedRift(hit, world)) {
                // TODO: not necessarily success, fix this and all other similar cases to make arm swing correct
                return new ActionResult<>(ActionResult.SUCCESS, stack);
            } else {
                player.sendStatusMessage(new TextComponentTranslation("tools.rift_miss"), true);
                TileEntityFloatingRiftRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.GRAPHICS.highlightRiftCoreFor;
                return new ActionResult<>(ActionResult.FAIL, stack);
            }
        }

        if (RayTraceHelper.hitsDetachedRift(hit, world)) {
            DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) world.getBlockEntity(hit.getBlockPos());
            if (!rift.stabilized && !rift.closing) {
                rift.setStabilized(true);
                world.playSound(null, player.getPosition(), ModSoundEvents.RIFT_CLOSE, SoundCategory.BLOCKS, 0.6f, 1); // TODO: different sound
                stack.damageItem(1, player);
                player.sendStatusMessage(new TextComponentTranslation(getRegistryName() + ".stabilized"), true);
                return new ActionResult<>(ActionResult.SUCCESS, stack);
            } else {
                player.sendStatusMessage(new TextComponentTranslation(getRegistryName() + ".already_stabilized"), true);
            }
        }
        return new ActionResult<>(ActionResult.FAIL, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(I18n.format(I18n.format(getRegistryName() + ".info")));
    }
}
