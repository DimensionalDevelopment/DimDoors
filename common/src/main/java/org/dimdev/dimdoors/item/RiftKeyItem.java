package org.dimdev.dimdoors.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.dimdev.dimdoors.block.RiftVariantProvider;
import org.dimdev.dimdoors.rift.targets.PrivatePocketTarget;
import org.dimdev.dimdoors.rift.targets.TempTarget;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.sound.ModSoundEvents;


public class RiftKeyItem extends Item {
    public RiftKeyItem(Properties settings) {
        super(settings.durability(10).component(ModDataComponentTypes.VIRTUAL_TARGET, PrivatePocketTarget.INSTANCE));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();

        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (context.getPlayer().isCrouching()) {
            ItemStack stack = context.getItemInHand();
            VirtualTarget<?> temp = stack.get(ModDataComponentTypes.VIRTUAL_TARGET);

            if (temp == null || temp == VirtualTarget.NoneTarget.INSTANCE) {
                return InteractionResult.FAIL;
            }

            var pos = context.getClickedPos();
            var state = level.getBlockState(pos);

            if (state.getBlock() instanceof RiftVariantProvider provider) {

                var riftOptional = provider.convertToRiftProvider((ServerLevel) level, pos, state);

                if (riftOptional.isPresent()) {
                    var rift = riftOptional.get();

                    VirtualTarget<?> original = rift.getData().getDestination();
                    rift.setDestination(new TempTarget(temp.copy(), original.copy()));
                    context.getLevel().playSound(null, rift.getBlockPos(), ModSoundEvents.KEY_LOCK, SoundSource.BLOCKS);

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.useOn(context);
    }
}
