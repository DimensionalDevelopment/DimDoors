package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.BlockColored;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.dimdev.dimdoors.DimDoors;

import java.util.Random;

public class BlockFabricAncient extends BlockColored {

    public static final String ID = "ancient_fabric";
    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

    public BlockFabricAncient() {
        super(Material.ROCK);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setUnlocalizedName(ID);
        setDefaultState(getDefaultState().withProperty(COLOR, EnumDyeColor.BLACK));
        setHardness(-1);
        setResistance(6000000.0F);
        disableStats();
        setSoundType(SoundType.STONE);
        setLightLevel(1);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }
}
