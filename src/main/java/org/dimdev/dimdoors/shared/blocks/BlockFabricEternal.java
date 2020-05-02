package org.dimdev.dimdoors.shared.blocks;

import io.github.opencubicchunks.cubicchunks.core.world.ICubeProviderInternal;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.terraingen.InitNoiseGensEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import org.apache.logging.log4j.core.jmx.Server;
import org.dimdev.ddutils.TeleportUtils;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.fluids.ModFluids;
import org.dimdev.dimdoors.shared.items.ModCreativeTabs;
import org.dimdev.dimdoors.shared.rifts.targets.EscapeTarget;
import org.dimdev.dimdoors.shared.rifts.targets.ITarget;
import org.lwjgl.util.Dimension;

public class BlockFabricEternal extends BlockFluidClassic { // TODO: make this a glowing red liquid

    public static final Material ETERNAL_FABRIC = new Material(MapColor.PINK);
    public static final String ID = "eternal_fabric";
    public static EscapeTarget exitLimbo = new EscapeTarget(true);



    public BlockFabricEternal() {
        super(ModFluids.ETERNAL_FABRIC, ETERNAL_FABRIC);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
        setTranslationKey(ID);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setHardness(-1);
        setResistance(6000000.0F);
        disableStats();
        setLightLevel(1);
        setSoundType(SoundType.STONE);
    }

    //This is meant to be a fix for a cubic chunks version. Currently crashes for some reason and don't want to try and fix right now.

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        MinecraftServer minecraftServer = world.getMinecraftServer();
        if (world.isRemote) return;
        entity.setPortal(entity.getPosition());
        entity.changeDimension(-1);
    }
}


