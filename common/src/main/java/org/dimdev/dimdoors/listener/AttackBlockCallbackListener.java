package org.dimdev.dimdoors.listener;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.dimdev.dimdoors.ISided;
import org.dimdev.dimdoors.api.item.AttackBlockResult;
import org.dimdev.dimdoors.api.item.ExtendedItem;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;

public class AttackBlockCallbackListener implements ISided.AttackBlockCallback {
    @Override
    public InteractionResult attack(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
        var world = player.level();

        Item item = player.getItemInHand(hand).getItem();
        if (!(item instanceof ExtendedItem extendedItem)) {
            return InteractionResult.PASS;
        }

        if (!world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        AttackBlockResult result = extendedItem.onAttackBlock(world, player, hand, pos, direction);
        if (result.sendPacket()) {
            if (!ClientPacketListener.tryToSendPacket(new HitBlockWithItemC2SPacket(hand, pos, direction))) {
                return InteractionResult.FAIL;
            }
        }

        return result.result();
    }
}
