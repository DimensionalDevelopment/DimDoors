package org.dimdev.dimdoors.fabric.forge.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import org.dimdev.dimdoors.forge.client.config.ModMenu;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenu::getConfigScreen;
    }
}
