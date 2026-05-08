package org.dimdev.dimdoors.compat.sable.mixins;

import dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.dimdev.dimdoors.block.DetachedRiftBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DetachedRiftBlock.class)
public class DetachedRiftSubLevelCollisionMixin/* implements BlockSubLevelCollisionShape*/ {
//    @Override
//    public VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state) {
//        return Shapes.block();
//    }
}
