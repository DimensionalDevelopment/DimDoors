package org.dimdev.dimdoors.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.limlib.api.client.ToolTipHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.dimdev.dimdoors.item.RaycastHelper.DETACH;

public class RiftStabilizerItem extends Item {
    public RiftStabilizerItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = RaycastHelper.findDetachRift(player, DETACH);

        if (world.isClientSide) {
            if (RaycastHelper.hitsDetachedRift(hit, world)) {
                // TODO: not necessarily success, fix this and all other similar cases to make arm swing correct
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            } else {
                player.displayClientMessage(Component.translatable("tools.rift_miss"), true);
                RiftBlockEntity.showRiftCoreUntil = System.currentTimeMillis() + DimensionalDoors.getConfig().getGraphicsConfig().highlightRiftCoreFor;
                return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
            }
        }

        if (RaycastHelper.hitsDetachedRift(hit, world)) {
            DetachedRiftBlockEntity rift = (DetachedRiftBlockEntity) world.getBlockEntity(hit.getBlockPos());
            if (!rift.stabilized && !rift.closing) {
                rift.setStabilized(true);
                world.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_CLOSE, SoundSource.BLOCKS, 0.6f, 1); // TODO: different sound

                var serverPlayer = (ServerPlayer) player;

                stack.hurtAndBreak(1, serverPlayer.serverLevel(), serverPlayer, a -> {
                });
                player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".stabilized"), true);
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            } else {
                player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".already_stabilized"), true);
            }
        }
        return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable TooltipContext level, List<Component> list, TooltipFlag tooltipFlag) {
        ToolTipHelper.processTranslation(list, this.getDescriptionId() + ".info");
    }
}
