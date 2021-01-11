package org.dimdev.dimdoors.client.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.client.util.ScreenGenerator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> ScreenGenerator.create(screen, ModConfig.INSTANCE, ModConfig::serialize);
    }
}
