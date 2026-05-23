package org.dimdev.dimdoors.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.tag.ModBlockTags;

import static net.minecraft.world.level.block.Blocks.STONE;

public class FabricBlock extends Block {

    FabricBlock(DyeColor color) {
    super(Properties.ofFullCopy(STONE).mapColor(color).strength(1.2F).lightLevel(state -> 15));
    }

    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
        if (context.getPlayer().isShiftKeyDown()) return false;
        Block heldBlock = Block.byItem(context.getPlayer().getItemInHand(context.getHand()).getItem());


        if (heldBlock.builtInRegistryHolder().is(ModBlockTags.DOES_NOT_REPLACE_FABRIC) || !heldBlock.defaultBlockState().isCollisionShapeFullBlock(context.getLevel(), context.getClickedPos()))
            return false;
        return !(heldBlock instanceof EntityBlock) && !(heldBlock instanceof FabricBlock);
    }
}
