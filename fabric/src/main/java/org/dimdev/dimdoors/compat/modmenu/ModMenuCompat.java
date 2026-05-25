package org.dimdev.dimdoors.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.network.chat.Component;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.client.config.DimDoorsConfigScreen;

import static org.dimdev.dimdoors.client.config.DimDoorsConfigScreen.builder;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ConfigScreen.createScreen(parent);
    }
}
