package org.dimdev.dimdoors.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return org.dimdev.dimdoors.client.config.ModMenu::getConfigScreen;
    }
}
