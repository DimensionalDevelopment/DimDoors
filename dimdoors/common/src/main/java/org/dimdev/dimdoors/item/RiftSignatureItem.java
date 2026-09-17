package org.dimdev.dimdoors.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.dimdev.dimdoors.ModGameRules;
import org.dimdev.dimdoors.api.util.RotatedLocation;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftVariantProvider;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimcore.api.client.ToolTipHelper;
import org.dimdev.dimdoors.util.LevelSpaceHelper;
import org.dimdev.dimdoors.rift.RiftUtils;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModDimensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class RiftSignatureItem extends Item {
    public static final String ID = "rift_signature";
    public boolean shouldclear;

    public RiftSignatureItem(Properties settings, boolean clear) {

        super(settings);
        shouldclear = clear;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(ModDataComponentTypes.DESTINATION);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext itemUsageContext) {
        Level world = itemUsageContext.getLevel();

        if (world.isClientSide()) return InteractionResult.SUCCESS;

        Player player = itemUsageContext.getPlayer();

        if (ModDimensions.isPrivatePocketDimension(world) && !world.getGameRules().getBoolean(ModGameRules.RIFT_SIGNATURE_WORKS_IN_PRIVATE_POCKETS)) {
            player.displayClientMessage(Component.translatable("tools.signature_blocked").withStyle(ChatFormatting.BLACK), true);
            return InteractionResult.FAIL;
        }

        // get block one block above the clicked block
        BlockPos pos = itemUsageContext.getClickedPos();
        BlockState state = world.getBlockState(pos);
        Direction side = itemUsageContext.getClickedFace();


        ItemStack stack = itemUsageContext.getItemInHand();

        if (!((state.canBeReplaced() || state.getBlock() instanceof RiftVariantProvider) && player.mayUseItemAt(pos, side.getOpposite(), stack))) {
            pos = pos.relative(side);
            state = world.getBlockState(pos);
        }

        pos = normalizeRiftProviderPos(world, pos);
        state = world.getBlockState(pos);

        if (!(state.canBeReplaced() || state.getBlock() instanceof RiftVariantProvider)) {
            return InteractionResult.FAIL;
        }

        RotatedLocation rotatedLocation = getSource(stack);

        if (rotatedLocation == null) {
            // The link signature has not been used. Store its current target as the first location.
            setSource(stack, new RotatedLocation(world.dimension(), pos, player.getYRot(), 0));
            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".stored"), true);
            world.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_START, SoundSource.BLOCKS, 0.6f, 1);
        } else {
            rotatedLocation = normalizeRiftProviderLocation(rotatedLocation);
            var source = new RotatedLocation(world.dimension(), pos, player.getYRot(), 0);

            var target = rotatedLocation.asTarget();
            getOrCreateRift((ServerLevel) world, pos).ifPresent(a -> a.setDestination(target));
            getOrCreateRift(rotatedLocation.getWorld(), rotatedLocation.pos).ifPresent(a -> a.setDestination(source.asTarget()));

            var serverPlayer = (ServerPlayer) player;

            stack.hurtAndBreak(1, ((ServerPlayer) player).serverLevel(), serverPlayer, a -> {
            }); // TODO: calculate damage based on position?
            if (shouldclear) {
                clearSource(stack);
            }
            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".created"), true);
            // null = send sound to the player too, we have to do this because this code is not run client-side
            world.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_END, SoundSource.BLOCKS, 0.6f, 1);
        }

        return InteractionResult.SUCCESS;
    }

    private static BlockPos normalizeRiftProviderPos(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.hasProperty(DoorBlock.HALF) && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            return pos.below();
        }
        return pos;
    }

    private static RotatedLocation normalizeRiftProviderLocation(RotatedLocation location) {
        ServerLevel world = location.getWorld();
        if (world == null) {
            return location;
        }

        BlockPos normalizedPos = normalizeRiftProviderPos(world, location.pos);
        if (normalizedPos.equals(location.pos)) {
            return location;
        }
        return new RotatedLocation(location.world, normalizedPos, location.yaw, location.pitch);
    }

    public static void setSource(ItemStack itemStack, RotatedLocation destination) {
        itemStack.set(ModDataComponentTypes.DESTINATION, destination);
    }

    public static void clearSource(ItemStack itemStack) {
        itemStack.remove(ModDataComponentTypes.DESTINATION);
    }

    public static @Nullable RotatedLocation getSource(ItemStack itemStack) {
        return itemStack.get(ModDataComponentTypes.DESTINATION);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> list, TooltipFlag tooltipContext) {
        RotatedLocation transform = getSource(itemStack);
        if (transform != null) {
            list.add(Component.translatable(this.getDescriptionId() + ".bound.info0", transform.getX(), transform.getY(), transform.getZ(), transform.getWorldId().location().toString()));
            list.add(Component.translatable(this.getDescriptionId() + ".bound.info1", transform.getWorldId().location().toString()));
        } else {
            ToolTipHelper.processTranslation(list, this.getDescriptionId() + ".unbound.info");
        }
    }

    public static Optional<? extends Rift> getOrCreateRift(ServerLevel world, BlockPos pos) {
        Optional<? extends Rift> rift;

        pos = normalizeRiftProviderPos(world, pos);

        if (!LevelSpaceHelper.INSTANCE.prepareRiftCreation(world, pos)) {
            return Optional.empty();
        }

        var state = world.getBlockState(pos);

        if (state.getBlock() instanceof RiftVariantProvider variantProvider)
            rift = variantProvider.convertToRiftProvider(world, pos, state).map(RiftUtils::registerFunction);
        else if (state.canBeReplaced()) {
            world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.defaultBlockState());
            rift = world.getBlockEntity(pos, ModBlockEntityTypes.DETACHED_RIFT).map(RiftUtils::registerFunction);
        } else rift = Optional.empty();

        return rift;
    }

}
