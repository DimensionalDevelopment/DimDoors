package org.dimdev.dimdoors;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HALF;
import static org.dimdev.dimdoors.block.door.WaterLoggableDoorBlock.WATERLOGGED;

public class DimensionalDoorsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DimensionalDoors.init();


		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (player.isCreative() && !DimensionalDoors.getConfig().getDoorsConfig().placeRiftsInCreativeMode) {
				return;
			}
			if (blockEntity instanceof EntranceRiftBlockEntity && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
				world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.get().defaultBlockState().setValue(WATERLOGGED, state.getValue(WATERLOGGED)));
				((DetachedRiftBlockEntity) world.getBlockEntity(pos)).setData(((EntranceRiftBlockEntity) blockEntity).getData());
			}
		});
    }
}
