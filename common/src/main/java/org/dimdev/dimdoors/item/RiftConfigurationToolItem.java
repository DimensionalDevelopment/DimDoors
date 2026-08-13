package org.dimdev.dimdoors.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.item.AttackBlockResult;
import org.dimdev.dimdoors.api.item.ExtendedItem;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.item.component.IdCounter;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.rift.targets.IdMarker;
import org.dimdev.dimcore.api.client.ToolTipHelper;
import org.dimdev.dimcore.api.util.EntityUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RiftConfigurationToolItem extends Item implements ExtendedItem {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String ID = "rift_configuration_tool";

    RiftConfigurationToolItem(Properties properties) {
        super(properties.stacksTo(1).durability(16));
    }



    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hit = RaycastHelper.findDetachRift(player, RaycastHelper.RIFT);

        if (world.isClientSide) {
            return InteractionResultHolder.fail(stack);
        } else {
            if (RaycastHelper.hitsRift(hit, world)) {
                Rift rift = (Rift) world.getBlockEntity(((BlockHitResult) hit).getBlockPos());

                var destination = rift.getData().getDestination();


                if (destination instanceof IdMarker idMarker && idMarker.getId() < IdCounter.get(stack)) {
                    EntityUtils.chat(player, Component.literal("Id: " + idMarker.getId()));
                } else {
                    int id = IdCounter.getAndIncrement(stack);

                    ServerPacketHandler.sync((ServerPlayer) player, stack, hand);

                    EntityUtils.chat(player, Component.literal("Rift stripped of data and set to target id: " + id));

                    rift.setDestination(new IdMarker(id));
                }

                return InteractionResultHolder.success(stack);
            } else {
                EntityUtils.chat(player, Component.literal("Current Count: " + IdCounter.count(stack)));
            }
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public AttackBlockResult onAttackBlock(Level world, Player player, InteractionHand hand, BlockPos pos, Direction direction) {
        var stack = player.getItemInHand(hand);

        if (world.isClientSide) {
            if (player.isShiftKeyDown()) {
                if (IdCounter.get(stack) != 0 || world.getBlockEntity(pos) instanceof Rift) {
                    return AttackBlockResult.success(true);
                }

                return AttackBlockResult.fail(false);
            }
        } else {
            if (player.isShiftKeyDown()) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof Rift rift) {


                    if (!(rift.getData().getDestination() instanceof IdMarker idMarker) || idMarker.getId() != -1) {
                        rift.setDestination(new IdMarker(-1));
                        EntityUtils.chat(player, Component.literal("Rift stripped of data and set to invalid id: -1"));
                        return AttackBlockResult.success(false);
                    }
                } else if (IdCounter.get(stack) != 0) {
                    IdCounter.set(stack, 0);

                    ServerPacketHandler.sync((ServerPlayer) player, stack, hand);

                    EntityUtils.chat(player, Component.literal("Counter has been reset."));
                    return AttackBlockResult.success(false);
                }
            }
        }
        return AttackBlockResult.success(false);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable TooltipContext level, List<Component> list, TooltipFlag tooltipFlag) {
        ToolTipHelper.processTranslation(list, this.getDescriptionId() + ".info");
    }
}
