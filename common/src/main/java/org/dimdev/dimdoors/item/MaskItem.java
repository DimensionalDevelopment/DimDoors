package org.dimdev.dimdoors.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.entity.mask.MaskType;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.packet.s2c.MaskCatchAnimS2CPacket;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MaskItem extends Item {
    public MaskItem(Properties properties) {
        super(properties);
    }

    public static boolean isMask(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ModItems.MASK;
    }

    public static boolean isWearingMask(LivingEntity entity) {
        return isMask(entity.getItemBySlot(EquipmentSlot.HEAD));
    }

    public static MaskType getMaskType(ItemStack stack) {
        MaskType type = stack.get(ModDataComponentTypes.MASK_TYPE);
        return type == null || type == MaskType.RANDOM ? MaskType.CYCLOP : type;
    }

    public static int getMaskStacks(ItemStack stack) {
        Integer stacks = stack.get(ModDataComponentTypes.MASK_STACKS);
        return Mth.clamp(stacks == null ? 1 : stacks, 1, MaskWearerEffects.maxStacks());
    }

    public static void applyCaught(Player player, MaskType caughtType) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ItemStack helmet = serverPlayer.getItemBySlot(EquipmentSlot.HEAD);
        if (isMask(helmet)) {
            int stacks = Mth.clamp(getMaskStacks(helmet) + 1, 1, MaskWearerEffects.maxStacks());
            helmet.set(ModDataComponentTypes.MASK_STACKS, stacks);
            serverPlayer.displayClientMessage(Component.literal("Another mask clings to you"), true);
        } else {
            if (!helmet.isEmpty()) {
                serverPlayer.drop(helmet.copy(), true, false);
            }

            serverPlayer.setItemSlot(EquipmentSlot.HEAD, MaskWearerEffects.createMaskStack(serverPlayer, caughtType, 1));
        }

        ServerPacketHandler.sendPacket(serverPlayer, new MaskCatchAnimS2CPacket(caughtType));
        serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 0.45F);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player) || player.getItemBySlot(EquipmentSlot.HEAD) != stack) {
            return;
        }

        MaskWearerEffects.tickWornMask(player, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Type: " + typeName(getMaskType(stack))));
        tooltip.add(Component.literal("Clinging masks: " + getMaskStacks(stack) + "/" + MaskWearerEffects.maxStacks()));
    }

    private static String typeName(MaskType type) {
        return switch (type) {
            case CYCLOP -> "Cyclop";
            case ECHO -> "Echo";
            case ENLIGHTENED -> "Enlightened";
            case FORESIGHT -> "Foresight";
            case SCULKING -> "Sculking";
            case RANDOM -> "Random";
            case BLACK -> "Black";
        };
    }
}
