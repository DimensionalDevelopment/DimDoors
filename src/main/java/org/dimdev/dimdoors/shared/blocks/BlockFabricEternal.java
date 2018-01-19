package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.DimDoors;

public class BlockFabricEternal extends BlockEmptyDrops { // TODO: make this a glowing red liquid

    public static final Material ETERNAL_FABRIC = new Material(MapColor.PINK);
    public static final String ID = "eternal_fabric";

    public BlockFabricEternal() {
        super(ETERNAL_FABRIC);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setUnlocalizedName(ID);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setHardness(-1);
        setResistance(6000000.0F);
        disableStats();
        setLightLevel(1);
        setSoundType(SoundType.STONE);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        // TODO: implement using a destination
    }
}
