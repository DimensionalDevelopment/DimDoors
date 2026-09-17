package org.dimdev.dimdoors.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.listener.UseDoorItemOnBlockCallbackListener;
import org.jetbrains.annotations.NotNull;

public class PlaceOnlyOnRiftBlockItem extends BlockItem {

    public PlaceOnlyOnRiftBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }

        return placeOnDetachedRift(player, context.getHand(), context.getItemInHand());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult result = placeOnDetachedRift(player, hand, stack);
        if (result.consumesAction()) {
            return new InteractionResultHolder<>(result, stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext ctx) {
        if (ctx.getPlayer() == null) {
            return InteractionResult.FAIL;
        }

        UseDoorItemOnBlockCallbackListener.DimDoorBlockPlaceContext context = ctx instanceof UseDoorItemOnBlockCallbackListener.DimDoorBlockPlaceContext dimDoorContext ? dimDoorContext : new UseDoorItemOnBlockCallbackListener.DimDoorBlockPlaceContext(ctx, RaycastHelper.findDetachRift(ctx.getPlayer(), RaycastHelper.DETACH));

        if (!context.getLevel().getBlockState(context.getClickedPos()).is(ModBlocks.DETACHED_RIFT)) {
            return InteractionResult.FAIL;
        }

        if (context.getLevel().isClientSide) {
            return super.place(context);
        }

        if (!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof DetachedRiftBlockEntity detachedRiftBlockEntity)) {
            return InteractionResult.FAIL;
        }

        InteractionResult result = super.place(context);
        if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
            if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof Rift rift) {
                rift.copyFrom(detachedRiftBlockEntity);
                rift.updateType();
            }
        }

        return result;
    }

    private InteractionResult placeOnDetachedRift(Player player, InteractionHand hand, ItemStack stack) {
        var hitResult = RaycastHelper.findDetachRift(player, RaycastHelper.DETACH);
        if (!RaycastHelper.hitsDetachedRift(hitResult, player.level())) {
            return InteractionResult.FAIL;
        }

        return place(new UseDoorItemOnBlockCallbackListener.DimDoorBlockPlaceContext(player, hand, stack, hitResult));
    }
}