package org.dimdev.dimdoors.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.HitResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.client.TileEntityFloatingRiftRenderer;
import org.dimdev.dimdoors.tileentities.RiftBlockEntity;

import java.util.List;

public class RiftConfigurationToolItem extends Item {

    public static final String ID = "rift_configuration_tool";

    RiftConfigurationToolItem() {
        setMaxStackSize(1);
        setMaxDamage(16);
        setCreativeTab(ModItemGroups.DIMENSIONAL_DOORS);
        setTranslationKey(ID);
        setRegistryName(new Identifier("dimdoors", ID));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        HitResult hit = player.rayTrace(RayTraceHelper.REACH_DISTANCE, 0, false);

        if (world.isClient) {
            if (!RayTraceHelper.hitsRift(hit, world)) {
                player.sendStatusMessage(new TextComponentTranslation("tools.rift_miss"), true);
                TileEntityFloatingRiftRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.GRAPHICS.highlightRiftCoreFor;
            }
            return new ActionResult<>(ActionResult.FAIL, stack);
        }

        if (RayTraceHelper.hitsRift(hit, world)) {
            RiftBlockEntity rift = (RiftBlockEntity) world.getBlockEntity(hit.getBlockPos());

            System.out.println(rift);

            //TODO: implement this tool's functionality
            return new ActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(ActionResult.FAIL, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (I18n.hasKey(getRegistryName() + ".info")) {
            tooltip.add(I18n.format(getRegistryName() + ".info"));
        }
    }
}
