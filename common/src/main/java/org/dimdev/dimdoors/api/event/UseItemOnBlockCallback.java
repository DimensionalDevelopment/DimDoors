package org.dimdev.dimdoors.api.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.limlib.api.util.SimpleEvent;

public interface UseItemOnBlockCallback {
    SimpleEvent<UseItemOnBlockCallback> EVENT = SimpleEvent.of(
        listeners -> (player, world, hand, hitResult) -> {
            for (UseItemOnBlockCallback listener : listeners) {
                InteractionResult result = listener.useItemOnBlock(player, world, hand, hitResult);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }

            return InteractionResult.PASS;
        }
    );

    InteractionResult useItemOnBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult);
}
