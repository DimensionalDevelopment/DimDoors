package org.dimdev.dimdoors.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.client.DetachedRiftBlockEntityRenderer;
import org.dimdev.util.Location;
import org.dimdev.util.TeleportUtil;

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
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        HitResult hit = player.rayTrace(16, 1.0F, false); //TODO: make the range of the Rift Blade configurable
        if (hit == null) {
            hit = player.rayTrace(RayTraceHelper.REACH_DISTANCE, 0, false);
        }

        if (world.isClient) {
            if (RayTraceHelper.hitsLivingEntity(hit) || RayTraceHelper.hitsRift(hit, world)) {
                return new TypedActionResult<>(ActionResult.SUCCESS, stack);
            } else {
                player.addChatMessage(new TranslatableText(getTranslationKey() + ".rift_miss"), true);
                DetachedRiftBlockEntityRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.GRAPHICS.highlightRiftCoreFor;
                return new TypedActionResult<>(ActionResult.FAIL, stack);
            }
        }

        if (RayTraceHelper.hitsLivingEntity(hit)) {
            double damageMultiplier = (double) stack.getDamage() /  (double) stack.getMaxDamage();
            // TODO: gaussian, instead or random
            double offsetDistance = Math.random() * damageMultiplier * 7 + 2; //TODO: make these offset distances configurable
            double offsetRotationYaw = (Math.random() - 0.5) * damageMultiplier * 360;            
            
            Vec3d playerVec = player.getPosVector();
            Vec3d entityVec = hit.getPos();
            Vec3d offsetDirection = playerVec.subtract(entityVec).normalize();
            offsetDirection = offsetDirection.rotateY((float) (offsetRotationYaw * Math.PI) / 180);

            BlockPos tpPos = new BlockPos(entityVec.add(offsetDirection.multiply(offsetDistance)));
            while (world.getBlockState(tpPos).getMaterial().blocksMovement()) tpPos = tpPos.up(); // TODO: move to ddutils
            TeleportUtil.teleport(player, new Location(world, tpPos), (player.yaw - (float) offsetRotationYaw) % 360, player.pitch);
            
            stack.damage(1, player, a -> {});
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        } else if (RayTraceHelper.hitsRift(hit, world)) {
            RiftBlockEntity rift = (RiftBlockEntity) world.getBlockEntity(new BlockPos(hit.getPos()));
            rift.teleport(player);

            stack.damage(1, player, a -> {});
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new TypedActionResult<>(ActionResult.FAIL, stack);
    }
}
