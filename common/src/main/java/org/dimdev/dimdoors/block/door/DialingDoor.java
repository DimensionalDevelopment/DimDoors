package org.dimdev.dimdoors.block.door;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.dimdev.dimdoors.block.entity.DialingDoorBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class DialingDoor extends DimensionalDoorBlock<DialingDoorBlockEntity> {
    public DialingDoor(Properties settings, BlockSetType blockSetType) {
        super(settings, blockSetType, true);
    }

    @Override
    public BlockEntityType<DialingDoorBlockEntity> getRiftBlockEnityType() {
        return ModBlockEntityTypes.DIALING_DOOR;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.MODEL;
    }

    //Note: This is needed because of how the inheritance works.
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ResourceKey<LootTable> resourcekey = this.getLootTable();
        if (resourcekey == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootParams lootparams = params.withParameter(LootContextParams.BLOCK_STATE, state).create(LootContextParamSets.BLOCK);
            ServerLevel serverlevel = lootparams.getLevel();
            LootTable loottable = serverlevel.getServer().reloadableRegistries().getLootTable(resourcekey);
            return loottable.getRandomItems(lootparams);
        }
    }
}
