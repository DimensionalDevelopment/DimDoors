package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public final class PreventBlockModificationAddon implements PocketAddon, UseItemOnBlockCallback {
    private static final PreventBlockModificationAddon INSTANCE = new PreventBlockModificationAddon();
    public static final MapCodec<PreventBlockModificationAddon> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, PreventBlockModificationAddon> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static PreventBlockModificationAddon instance() {
        return INSTANCE;
    }

    private PreventBlockModificationAddon() {
    }


    public InteractionResult attackBlock(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        return preventsBlockModification(player) ? InteractionResult.FAIL : InteractionResult.PASS;
    }

    public boolean preventsBlockModification(Player player) {
        return !player.isCreative();
    }

    public InteractionResult useItem(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public InteractionResult useBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isCreative()) return InteractionResult.PASS;
        if (player.getItemInHand(hand).getItem() instanceof BlockItem) {
            BlockPos blockPos = hitResult.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            InteractionResult result = blockState.useWithoutItem(world, player, hitResult);
            if (result.consumesAction()) return result;

            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useItemOnBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        return useBlock(player, world, hand, hitResult);
    }

    @Override
    public PocketAddonType<?, ?> getType() {
        return PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON;
    }

    public static class PreventBlockModificationBuilderAddon implements PocketBuilderAddon<PreventBlockModificationAddon, PreventBlockModificationBuilderAddon> {
        public static MapCodec<PreventBlockModificationBuilderAddon> CODEC = MapCodec.unit(PreventBlockModificationBuilderAddon::new);

        @Override
        public void apply(Pocket<?, ?> pocket) {
            pocket.addAddon(PreventBlockModificationAddon.instance());
        }

        @Override
        public PocketAddonType<PreventBlockModificationAddon, PreventBlockModificationBuilderAddon> getType() {
        return PocketAddonType.PREVENT_BLOCK_MODIFICATION_ADDON;
        }
    }
}
