package org.dimdev.dimdoors.item;

import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.dimdev.dimdoors.entity.mask.MaskEntity;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.fabricmc.api.EnvType.CLIENT;

public class MaskWandItem extends Item {
    public static final String ID = "rift_configuration_tool";

    private static final byte MODE_GUARD = 0;
    private static final byte MODE_PATROL = 1;
    private static final byte MODE_WANDER = 2;

    public MaskWandItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        if (player.isShiftKeyDown()) {
            byte nextMode = getNextMode(getSelectedMode(stack));
            setSelectedMode(stack, nextMode);
            player.displayClientMessage(Component.literal("Mask mode: " + getModeName(nextMode)), true);
            return InteractionResultHolder.success(stack);
        }

        HitResult hit = player.pick(RaycastHelper.REACH_DISTANCE, 0.0F, false);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.fail(stack);
        }

        BlockPos clickedPos = ((BlockHitResult) hit).getBlockPos();
        byte selectedMode = getSelectedMode(stack);

        if (selectedMode == MODE_PATROL) {
            BlockPos patrolA = getPatrolPointA(stack);
            if (patrolA == null) {
                setPatrolPointA(stack, clickedPos);
                player.displayClientMessage(Component.literal("Set patrol point A: " + formatPos(clickedPos)), true);
                return InteractionResultHolder.success(stack);
            }

            BlockPos patrolB = getPatrolPointB(stack);
            if (patrolB == null) {
                setPatrolPointB(stack, clickedPos);
                player.displayClientMessage(Component.literal("Set patrol point B: " + formatPos(clickedPos)), true);
                return InteractionResultHolder.success(stack);
            }

            MaskEntity mask = ModEntityTypes.MASK.get().create((ServerLevel) world, a -> {}, clickedPos, MobSpawnType.SPAWN_EGG, true, false);
            if (mask == null) {
                return InteractionResultHolder.fail(stack);
            }

            mask.configurePatrol(clickedPos, patrolA, patrolB);
            world.addFreshEntity(mask);
            player.displayClientMessage(
                    Component.literal("Spawned patrol mask at " + formatPos(clickedPos) + " using A " + formatPos(patrolA) + " and B " + formatPos(patrolB)),
                    true
            );
            return InteractionResultHolder.success(stack);
        }

        MaskEntity mask = ModEntityTypes.MASK.get().create((ServerLevel) world, a -> {}, clickedPos, MobSpawnType.SPAWN_EGG, true, false);
        if (mask == null) {
            return InteractionResultHolder.fail(stack);
        }

        mask.configurePassiveMode(selectedMode, clickedPos);
        world.addFreshEntity(mask);
        player.displayClientMessage(Component.literal("Spawned mask in " + getModeName(selectedMode) + " mode at " + formatPos(clickedPos)), true);
        return InteractionResultHolder.success(stack);
    }

    private static byte getSelectedMode(ItemStack stack) {
        Byte mode = stack.get(ModDataComponentTypes.MASK_WAND_MODE.get());
        if (mode == null || mode < MODE_GUARD || mode > MODE_WANDER) {
            return MODE_GUARD;
        }
        return mode;
    }

    private static void setSelectedMode(ItemStack stack, byte mode) {
        stack.set(ModDataComponentTypes.MASK_WAND_MODE.get(), mode);
    }

    @Nullable
    private static BlockPos getPatrolPointA(ItemStack stack) {
        return stack.get(ModDataComponentTypes.MASK_WAND_PATROL_A.get());
    }

    private static void setPatrolPointA(ItemStack stack, BlockPos pos) {
        stack.set(ModDataComponentTypes.MASK_WAND_PATROL_A.get(), pos.immutable());
    }

    @Nullable
    private static BlockPos getPatrolPointB(ItemStack stack) {
        return stack.get(ModDataComponentTypes.MASK_WAND_PATROL_B.get());
    }

    private static void setPatrolPointB(ItemStack stack, BlockPos pos) {
        stack.set(ModDataComponentTypes.MASK_WAND_PATROL_B.get(), pos.immutable());
    }

    private static byte getNextMode(byte mode) {
        return switch (mode) {
            case MODE_GUARD -> MODE_PATROL;
            case MODE_PATROL -> MODE_WANDER;
            default -> MODE_GUARD;
        };
    }

    private static String getModeName(byte mode) {
        return switch (mode) {
            case MODE_GUARD -> "Guard";
            case MODE_PATROL -> "Patrol";
            case MODE_WANDER -> "Wander";
            default -> "Guard";
        };
    }

    private static String formatPos(BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }

    @Environment(CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable TooltipContext level, List<Component> list, TooltipFlag tooltipFlag) {
        if (I18n.exists(this.getDescriptionId() + ".info")) {
            list.add(Component.translatable(this.getDescriptionId() + ".info"));
        }

        list.add(Component.literal("Mode: " + getModeName(getSelectedMode(itemStack))));

        BlockPos patrolA = getPatrolPointA(itemStack);
        BlockPos patrolB = getPatrolPointB(itemStack);

        if (patrolA != null) {
            list.add(Component.literal("Patrol A: " + formatPos(patrolA)));
        }
        if (patrolB != null) {
            list.add(Component.literal("Patrol B: " + formatPos(patrolB)));
        }

        list.add(Component.literal("Sneak-use: cycle mode"));
        list.add(Component.literal("Patrol mode: click to set A, then B, then spawn"));
        list.add(Component.literal("Other modes: click to spawn"));
    }
}