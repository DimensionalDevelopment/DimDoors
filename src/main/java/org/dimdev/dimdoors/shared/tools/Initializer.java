package org.dimdev.dimdoors.shared.tools;

import net.minecraft.block.Block;
import net.minecraft.init.Bootstrap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.proxy.CommonProxy;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;

public final class Initializer {
    public static void initialize() {
        Bootstrap.register();

        ModMetadata md = new ModMetadata();
        md.modId = DimDoors.MODID;
        ModContainer mc = new DummyModContainer(md);
        Loader.instance().setupTestHarness(mc);
        Loader.instance().setActiveModContainer(mc);

        DimDoors.instance = new DimDoors();
        DimDoors.proxy = new CommonProxy();

        DimDoors.instance.onPreInitialization(new FMLPreInitializationEvent());
        ModBlocks.registerBlocks(new RegistryEvent.Register<Block>(GameData.BLOCKS, RegistryManager.ACTIVE.getRegistry(GameData.BLOCKS)));

        Loader.instance().setActiveModContainer(null);
    }
}
