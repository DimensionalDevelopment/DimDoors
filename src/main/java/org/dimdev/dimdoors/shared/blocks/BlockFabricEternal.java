package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.items.ModCreativeTabs;
import org.dimdev.dimdoors.shared.rifts.targets.EscapeTarget;

public class BlockFabricEternal extends BlockEmptyDrops { // TODO: make this a glowing red liquid

    public static final Material ETERNAL_FABRIC = new Material(MapColor.PINK);
    public static final String ID = "eternal_fabric";
    public static EscapeTarget exitLimbo = new EscapeTarget(true);

    public BlockFabricEternal() {
        super(ETERNAL_FABRIC);
        setRegistryName(DimDoors.getResource(ID));
        setTranslationKey(ID);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setHardness(-1);
        setResistance(6000000.0F);
        disableStats();
        setLightLevel(1);
        setSoundType(SoundType.STONE);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (world.isRemote) return;
        exitLimbo.receiveEntity(entity, entity.rotationYaw / 90 * 90, 0);
    }
}
