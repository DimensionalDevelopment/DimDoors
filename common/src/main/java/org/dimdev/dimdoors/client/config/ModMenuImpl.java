package org.dimdev.dimdoors.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuImpl implements ModMenuApi {//TODO: Move to fabric
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenu::getConfigScreen;
    }
}
