package org.dimdev.dimdoors.compat.create;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.compat.create.block.LiminaCouplerBlock;
import org.dimdev.dimdoors.compat.create.block.LiminalCouplerBlockEntity;
import org.dimdev.dimdoors.item.RaycastHelper;
import org.dimdev.dimdoors.listener.UseDoorItemOnBlockCallbackListener;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.Blocks.IRON_BLOCK;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;
import static org.dimdev.dimdoors.item.ModItems.DIMENSIONAL_DOORS;

public final class CreateCompatBlocks {
    public static final LiminaCouplerBlock LIMINAL_COUPLER = register("liminal_coupler", new LiminaCouplerBlock(ofFullCopy(IRON_BLOCK).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    private CreateCompatBlocks() {
    }

    private static <T extends Block> T register(String name, T block) {
        var sided = DimensionalDoors.getSided();
        var registeredBlock = sided.register(Registries.BLOCK, name, block);
        var item = sided.register(Registries.ITEM, name, new LiminalCouplerBlockItem(registeredBlock, new Item.Properties()));
        sided.appendStack(DIMENSIONAL_DOORS, item.getDefaultInstance());
        return registeredBlock;
    }

    public static void init() {
        CreateCompatBlockEntityTypes.LIMINAL_COUPLER.addBlock(LIMINAL_COUPLER);
    }

    private static class LiminalCouplerBlockItem extends BlockItem {
        private LiminalCouplerBlockItem(Block block, Properties properties) {
            super(block, properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Player player = context.getPlayer();
            if (player == null) {
                return InteractionResult.FAIL;
            }

            return placeOnDetachedRift(player, context.getHand(), context.getItemInHand());
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
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

            UseDoorItemOnBlockCallbackListener.DimDoorBlockPlaceContext context = ctx instanceof UseDoorItemOnBlockCallbackListener.DimDoorBlockPlaceContext dimDoorContext
                    ? dimDoorContext
                    : new UseDoorItemOnBlockCallbackListener.DimDoorBlockPlaceContext(ctx, RaycastHelper.findDetachRift(ctx.getPlayer(), RaycastHelper.DETACH));

            if (!context.getLevel().getBlockState(context.getClickedPos()).is(ModBlocks.DETACHED_RIFT)) {
                return InteractionResult.FAIL;
            }

            if (context.getLevel().isClientSide) {
                return super.place(context);
            }

            if (!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof DetachedRiftBlockEntity rift)) {
                return InteractionResult.FAIL;
            }

            rift.setUnregisterDisabled(true);
            InteractionResult result = super.place(context);
            if (result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME) {
                if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof LiminalCouplerBlockEntity coupler) {
                    coupler.copyFrom(rift);
                    coupler.updateType();
                }
            } else {
                rift.setUnregisterDisabled(false);
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
}
