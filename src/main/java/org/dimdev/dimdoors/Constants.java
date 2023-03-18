package org.dimdev.dimdoors;

import java.io.File;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.dimdev.dimdoors.api.capability.ComponentProvider;
import org.dimdev.dimdoors.api.capability.IComponent;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class Constants {
	public static final String MODID = "dimdoors";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final ConfigHolder<ModConfig> CONFIG_MANAGER = AutoConfig.register(ModConfig.class, ModConfig.SubRootJanksonConfigSerializer::new);
	public static final File CONFIG_ROOT = new File("config/DimDoors");
	public static final ComponentProvider<DimensionalRegistry> DIMENSIONAL_REGISTRY_PROVIDER = new ComponentProvider<>(new DimensionalRegistry());
	public static final Capability<IComponent> COMPONENT = get(new CapabilityToken<>(){});
}
