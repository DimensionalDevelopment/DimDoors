package org.dimdev.ddutils;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public final class I18nUtils {
    @SideOnly(Side.CLIENT)
    public static List<String> translateMultiline(String key) {
        List<String> list = new ArrayList<>();
        int i = 0;
        while (I18n.hasKey(key + i)) {
            list.add(I18n.format(key + i));
            i++;
        }
        return list;
    }
}
