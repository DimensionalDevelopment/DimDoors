package org.dimdev.dimdoors.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.EntityCollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.dimdev.dimdoors.block.entity.MaskHomeBlockEntity
import org.dimdev.dimdoors.entity.mask.MaskMode
import org.dimdev.dimdoors.item.ModItems

class MaskHomeBlock(properties: Properties) : Block(properties), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = MaskHomeBlockEntity(pos, state)

    override fun getRenderShape(state: BlockState) = RenderShape.INVISIBLE

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
        if ((context as? EntityCollisionContext)?.entity.let { it is Player && it.isHolding(ModItems.MASK_WAND) }) Shapes.block() else Shapes.empty()

    override fun getCollisionShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = Shapes.empty()

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean) {
        if (!state.`is`(newState.block) && level is ServerLevel) {
            (level.getBlockEntity(pos) as? MaskHomeBlockEntity)?.mask(level)?.let {
                it.home = null
                it.mode = MaskMode.WANDER
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston)
    }
}
