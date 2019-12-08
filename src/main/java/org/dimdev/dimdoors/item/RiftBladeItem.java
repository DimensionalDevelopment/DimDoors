package org.dimdev.dimdoors.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.client.TileEntityFloatingRiftRenderer;
import org.dimdev.dimdoors.tileentities.RiftBlockEntity;
import org.dimdev.util.Location;
import org.dimdev.util.TeleportUtil;

import java.util.List;

public class RiftBladeItem extends SwordItem {
    public static final String ID = "rift_blade";

    public RiftBladeItem(Settings settings) {
        super(ToolMaterials.IRON, 3, -2.4F, settings);

    }
    @Override
    public boolean hasEnchantmentGlint(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean canRepair(ItemStack item, ItemStack repairingItem) {
        return ModItems.STABLE_FABRIC == repairingItem.getItem();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        HitResult hit = player.rayTrace(16, 1.0F, false); //TODO: make the range of the Rift Blade configurable
        if (hit == null) {
            hit = player.rayTrace(RayTraceHelper.REACH_DISTANCE, 0, false);
        }

        if (world.isClient) {
            if (RayTraceHelper.hitsLivingEntity(hit) || RayTraceHelper.hitsRift(hit, world)) {
                return new ActionResult<>(ActionResult.SUCCESS, stack);
            } else {
                player.sendStatusMessage(new TextComponentTranslation(getRegistryName() + ".rift_miss"), true);
                TileEntityFloatingRiftRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.GRAPHICS.highlightRiftCoreFor;
                return new ActionResult<>(ActionResult.FAIL, stack);
            }
        }

        if (RayTraceHelper.hitsLivingEntity(hit)) {
            double damageMultiplier = (double) stack.getItemDamage() /  (double) stack.getMaxDamage();
            // TODO: gaussian, instead or random
            double offsetDistance = Math.random() * damageMultiplier * 7 + 2; //TODO: make these offset distances configurable
            double offsetRotationYaw = (Math.random() - 0.5) * damageMultiplier * 360;            
            
            Vec3d playerVec = player.getPositionVector();
            Vec3d entityVec = hit.hitVec;
            Vec3d offsetDirection = playerVec.subtract(entityVec).normalize();
            offsetDirection = offsetDirection.rotateYaw((float) (offsetRotationYaw * Math.PI) / 180);

            BlockPos tpPos = new BlockPos(entityVec.add(offsetDirection.scale(offsetDistance)));
            while (world.getBlockState(tpPos).getMaterial().blocksMovement()) tpPos = tpPos.up(); // TODO: move to ddutils
            TeleportUtil.teleport(player, new Location(world, tpPos), (player.rotationYaw - (float) offsetRotationYaw) % 360, player.rotationPitch);
            
            stack.damageItem(1, player);
            return new ActionResult<>(ActionResult.SUCCESS, stack);
        } else if (RayTraceHelper.hitsRift(hit, world)) {
            RiftBlockEntity rift = (RiftBlockEntity) world.getBlockEntity(hit.getBlockPos());
            rift.teleport(player);

            stack.damageItem(1, player);
            return new ActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(ActionResult.FAIL, stack);
    }
}
