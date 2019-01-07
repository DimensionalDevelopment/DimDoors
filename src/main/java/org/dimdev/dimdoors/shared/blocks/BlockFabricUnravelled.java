package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.items.ModCreativeTabs;
import org.dimdev.dimdoors.shared.world.limbo.LimboDecay;
import org.dimdev.dimdoors.shared.world.limbo.WorldProviderLimbo;

import java.util.Random;

public class BlockFabricUnravelled extends BlockEmptyDrops {

    public static final Material UNRAVELLED_FABRIC = new Material(MapColor.GRAY);
    public static final String ID = "unravelled_fabric";

    public BlockFabricUnravelled() {
        super(UNRAVELLED_FABRIC);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setTranslationKey(ID);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setHardness(0.1F);
        setSoundType(SoundType.STONE);

        setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        // Spread decay in Limbo
        if (world.provider instanceof WorldProviderLimbo) {
            LimboDecay.applySpreadDecay(world, pos);
        }
    }
}
