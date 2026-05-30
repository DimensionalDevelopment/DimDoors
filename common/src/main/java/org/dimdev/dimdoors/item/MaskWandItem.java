package org.dimdev.dimdoors.item;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.dimdev.dimdoors.api.item.AttackBlockResult;
import org.dimdev.dimdoors.api.item.ExtendedItem;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.MaskHomeBlockEntity;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.mask.MaskEntity;
import org.dimdev.dimdoors.entity.mask.MaskType;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MaskWandItem extends Item implements ExtendedItem {
    private static final int MAX_WAYPOINTS = 10;

    public MaskWandItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        if (player.isShiftKeyDown()) {
            setWaypoints(stack, List.of());
            sync(player, stack, hand);
            player.displayClientMessage(Component.literal("Mask waypoints cleared"), true);
            return InteractionResultHolder.success(stack);
        }

        HitResult hit = player.pick(RaycastHelper.REACH_DISTANCE, 0.0F, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos clickedPos = blockHit.getBlockPos().immutable();
            BlockPos waypoint = targetPoint(clickedPos, blockHit.getDirection());
            MaskHomeBlockEntity home = findHome(world, clickedPos, blockHit.getDirection());
            if (home != null) {
                home.showRoute(20 * 60);
                player.displayClientMessage(Component.literal("Showing mask route"), true);
                return InteractionResultHolder.success(stack);
            }

            List<BlockPos> waypoints = new ArrayList<>(getWaypoints(stack));
            if (waypoints.size() >= MAX_WAYPOINTS) {
                player.displayClientMessage(Component.literal("Mask waypoint list is full"), true);
                return InteractionResultHolder.fail(stack);
            }

            waypoints.add(waypoint);
            setWaypoints(stack, waypoints);
            sync(player, stack, hand);
            if (world instanceof ServerLevel serverLevel) {
                showStoredWaypointIndicators(serverLevel, waypoints);
            }
            player.displayClientMessage(Component.literal("Stored mask waypoint " + waypoints.size() + ": " + formatPos(waypoint)), true);
            return InteractionResultHolder.success(stack);
        }

        MaskType nextType = getSelectedType(stack).nextEditable();
        setSelectedType(stack, nextType);
        sync(player, stack, hand);
        player.displayClientMessage(Component.literal("Mask type: " + typeName(nextType)), true);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public AttackBlockResult onAttackBlock(Level world, Player player, InteractionHand hand, BlockPos pos, Direction direction) {
        if (world.isClientSide) {
            return AttackBlockResult.success(true);
        }

        if (!(world instanceof ServerLevel serverLevel)) {
            return AttackBlockResult.fail(false);
        }

        ItemStack stack = player.getItemInHand(hand);
        BlockPos homePos = targetPoint(pos, direction);
        MaskHomeBlockEntity home = findHome(world, pos, direction);
        if (home != null) {
            if (player.isShiftKeyDown()) {
                home.replaceWaypoints(getWaypoints(stack));
                home.showRoute(20 * 60);
                player.displayClientMessage(Component.literal("Mask home waypoints replaced"), true);
                return AttackBlockResult.success(true);
            }

            player.displayClientMessage(Component.literal("Shift-left click to replace this mask home's waypoints"), true);
            return AttackBlockResult.success(true);
        }

        BlockState homeState = serverLevel.getBlockState(homePos);
        if (!homeState.canBeReplaced()) {
            player.displayClientMessage(Component.literal("Mask home point is blocked: " + formatPos(homePos)), true);
            return AttackBlockResult.success(true);
        }

        MaskEntity mask = ModEntityTypes.MASK.create(serverLevel);
        if (mask == null) {
            return AttackBlockResult.fail(false);
        }

        List<BlockPos> waypoints = getWaypoints(stack);
        mask.configureFromWand(homePos, waypoints, getSelectedType(stack));
        if (!serverLevel.setBlock(homePos, ModBlocks.MASK_HOME.defaultBlockState(), 3)) {
            return AttackBlockResult.fail(false);
        }
        if (serverLevel.getBlockEntity(homePos) instanceof MaskHomeBlockEntity homeEntity) {
            homeEntity.configure(waypoints, mask.getUUID());
            homeEntity.showRoute(20 * 60);
        }
        serverLevel.addFreshEntity(mask);

        String modeName = waypoints.isEmpty() ? "guard" : "patrol";
        player.displayClientMessage(Component.literal("Spawned " + typeName(mask.getMaskType()) + " mask in " + modeName + " mode"), true);
        return AttackBlockResult.success(true);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof MaskEntity mask)) {
            return InteractionResult.PASS;
        }

        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) {
            mask.recallHomeAndToggleFrozen();
            player.displayClientMessage(Component.literal(mask.isFrozen() ? "Mask frozen" : "Mask released"), true);
        } else {
            MaskType nextType = mask.getMaskType().nextEditable();
            mask.setMaskType(nextType);
            player.displayClientMessage(Component.literal("Mask type: " + typeName(nextType)), true);
        }

        return InteractionResult.SUCCESS;
    }

    public static boolean isHoldingMaskWand(Player player) {
        return player.getMainHandItem().is(ModItems.MASK_WAND) || player.getOffhandItem().is(ModItems.MASK_WAND);
    }

    private static MaskType getSelectedType(ItemStack stack) {
        MaskType type = stack.get(ModDataComponentTypes.MASK_WAND_TYPE);
        return type == null || !type.isEditableSpawnType() ? MaskType.CYCLOP : type;
    }

    private static void setSelectedType(ItemStack stack, MaskType type) {
        stack.set(ModDataComponentTypes.MASK_WAND_TYPE, type);
    }

    private static List<BlockPos> getWaypoints(ItemStack stack) {
        List<BlockPos> waypoints = stack.get(ModDataComponentTypes.MASK_WAND_WAYPOINTS);
        return waypoints == null ? List.of() : List.copyOf(waypoints);
    }

    private static BlockPos targetPoint(BlockPos clickedPos, Direction direction) {
        return clickedPos.relative(direction).immutable();
    }

    @Nullable
    private static MaskHomeBlockEntity findHome(Level world, BlockPos clickedPos, Direction direction) {
        if (world.getBlockEntity(clickedPos) instanceof MaskHomeBlockEntity home) {
            return home;
        }

        if (world.getBlockEntity(targetPoint(clickedPos, direction)) instanceof MaskHomeBlockEntity home) {
            return home;
        }

        return null;
    }

    private static void setWaypoints(ItemStack stack, List<BlockPos> waypoints) {
        stack.set(ModDataComponentTypes.MASK_WAND_WAYPOINTS, List.copyOf(waypoints));
    }

    private static void sync(Player player, ItemStack stack, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            ServerPacketHandler.sync(serverPlayer, stack, hand);
        }
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

    private static String formatPos(BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }

    private static void showStoredWaypointIndicators(ServerLevel level, List<BlockPos> waypoints) {
        for (int i = 0; i < waypoints.size(); i++) {
            BlockPos waypoint = waypoints.get(i);
            boolean newest = i == waypoints.size() - 1;
            level.sendParticles(
                    newest ? ParticleTypes.END_ROD : ParticleTypes.WAX_ON,
                    waypoint.getX() + 0.5,
                    waypoint.getY() + 0.5,
                    waypoint.getZ() + 0.5,
                    newest ? 24 : 8,
                    0.18,
                    0.18,
                    0.18,
                    0.02
            );
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable TooltipContext level, List<Component> list, TooltipFlag tooltipFlag) {
        if (I18n.exists(this.getDescriptionId() + ".info")) {
            list.add(Component.translatable(this.getDescriptionId() + ".info"));
        }

        List<BlockPos> waypoints = getWaypoints(itemStack);
        list.add(Component.literal("Type: " + typeName(getSelectedType(itemStack))));
        list.add(Component.literal("Waypoints: " + waypoints.size() + "/" + MAX_WAYPOINTS));
        if (!waypoints.isEmpty()) {
            list.add(Component.literal("Next: " + formatPos(waypoints.get(waypoints.size() - 1))));
        }
    }
}
