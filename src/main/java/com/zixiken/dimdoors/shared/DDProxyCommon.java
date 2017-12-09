package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import com.zixiken.dimdoors.shared.entities.MobMonolith;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.tileentities.*;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class DDProxyCommon implements IDDProxy {

    @Override
    public void onPreInitialization(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new DDEventHandler());
        MinecraftForge.EVENT_BUS.register(ModBlocks.class);
        MinecraftForge.EVENT_BUS.register(ModItems.class);
        MinecraftForge.EVENT_BUS.register(CraftingManager.class);

        DimDoorDimensions.init();

        GameRegistry.registerTileEntity(TileEntityVerticalEntranceRift.class, "TileEntityVerticalEntranceRift"); // TODO: use new registry
        GameRegistry.registerTileEntity(TileEntityFloatingRift.class, "TileEntityFloatingRift");
        GameRegistry.registerTileEntity(TileEntityHorizontalEntranceRift.class, "TileEntityHorizontalEntranceRift");

        EntityRegistry.registerModEntity(new ResourceLocation(DimDoors.MODID, "mob_monolith"), MobMonolith.class, "monolith", 0, DimDoors.instance, 70, 1, true);
        EntityRegistry.registerEgg(new ResourceLocation(DimDoors.MODID, "mob_monolith"), 0, 0xffffff);
    }

    @Override
    public void onInitialization(FMLInitializationEvent event) {
        SchematicHandler.INSTANCE.loadSchematics();
    }

    public void updateDoorTE(BlockDimDoorBase door, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityVerticalEntranceRift) {
            TileEntityVerticalEntranceRift dimTile = (TileEntityVerticalEntranceRift) tile;
            IBlockState state = world.getBlockState(pos.down());
            dimTile.orientation = state.getBlock() instanceof BlockDimDoorBase
                    ? state.getValue(BlockDoor.FACING).getOpposite()
                    : ModBlocks.DIMENSIONAL_DOOR.getDefaultState().getValue(BlockDoor.FACING);
            dimTile.doorIsOpen = door.isDoorOnRift(world, pos) && door.isUpperDoorBlock(world.getBlockState(pos));
            dimTile.lockStatus = 0; //@todo
            dimTile.markDirty();
        }
    }

    @Override
    public World getDefWorld() {
        return getWorldServer(0); //gets the client world dim 0 handler
    }
}
