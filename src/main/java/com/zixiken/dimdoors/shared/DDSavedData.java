/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import java.io.File;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

/**
 *
 * @author Robijnvogel
 */
abstract class DDSavedData extends WorldSavedData {

    public DDSavedData(String name) {
        super(name);
    }

    public File getSaveLocation(World world) {
        File saveDir = world.getSaveHandler().getWorldDirectory();
        return new File(saveDir, "dimdoors/");
    }

}
