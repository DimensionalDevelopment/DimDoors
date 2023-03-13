package org.dimdev.dimdoors;

import java.io.File;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {
	public static final String MODID = "dimdoors";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final ConfigHolder<ModConfig> CONFIG_MANAGER = AutoConfig.register(ModConfig.class,
			ModConfig.SubRootJanksonConfigSerializer::new);
	public static final File CONFIG_ROOT = new File("config/DimDoors");
}
