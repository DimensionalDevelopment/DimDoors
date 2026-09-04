package org.dimdev.dimdoors.compat.imgui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.dimdev.dimcore.api.util.function.StreamUtils;
import org.dimdev.dimdoors.PortalColors;
import org.dimdev.dimdoors.client.ModShaders;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PortalColorGui {
    private static final Screen SCREEN = new ImGuiScreen();

    private static final float[] scratchColor = new float[3];

    private static final int[] colors = new int[16];

    private static final ImInt dyeId = new ImInt(-1);

    public static void toggle() {
        if(Minecraft.getInstance().screen == null) {
            if(ModShaders.getPortalColors(colors)) Minecraft.getInstance().setScreen(SCREEN);
        }
    }

    public static void render() {
        if(Minecraft.getInstance().screen != SCREEN) return;

        ImGui.begin("Portal Colors Input", ImGuiWindowFlags.AlwaysAutoResize);

        renderColor("Color 1", 0); renderColor("Color 2", 1);
        renderColor("Color 3", 2); renderColor("Color 4", 3);
        renderColor("Color 5", 4); renderColor("Color 6", 5);
        renderColor("Color 7", 6); renderColor("Color 8", 7);
        renderColor("Color 9", 8); renderColor("Color 10", 9);
        renderColor("Color 11", 10); renderColor("Color 12", 11);
        renderColor("Color 13", 12); renderColor("Color 14", 13);
        renderColor("Color 15", 14); renderColor("Color 16", 15);

        if(ImGui.button("Reset")) {
            System.arraycopy(PortalColors.base(), 0, colors, 0, 16);
            ModShaders.setPortalColors(colors);
        }
        if(ImGui.button("Copy To Text")) pushToClipboard();

        ImGui.end();

//        ImGui.begin("Dyes");
//        if(ImGui.inputInt("Value", dyeId)) {
//            var value = dyeId.get();
//
//            if(MathUtil.between(value, 0, 15)) {
//                var dye = DyeColor.values()[value];
//
//                var ints = PortalColors.DYES.getOrDefault(dye, PortalColors.OVERWORLD);
//                System.arraycopy(ints, 0, colors, 0, 16);
//
//                ModShaders.setPortalColors(ints);
//            }
//        }
        ImGui.end();
    }

    private static void renderColor(String name, int index) {
        int color = colors[index];

        scratchColor[0] = ((color >> 16) & 0xFF) * 0.003921569f;
        scratchColor[1] = ((color >>  8) & 0xFF) * 0.003921569f;
        scratchColor[2] = (color & 0xFF) * 0.003921569f;

        if (ImGui.colorEdit3(name, scratchColor)) {
            colors[index] = ((int) (scratchColor[0] * 255.0f) << 16)
                    | ((int) (scratchColor[1] * 255.0f) <<  8)
                    |  (int) (scratchColor[2] * 255.0f);

            ModShaders.setPortalColors(colors);
        }
    }

    private static void update() {
        ModShaders.setPortalColors(colors);
    }

    public static void pushToClipboard() {
        Arrays.stream(colors).mapToObj(Integer::toHexString).collect(
                StreamUtils.consuming(Collectors.joining(",\n"), s -> Minecraft.getInstance().keyboardHandler.setClipboard(s)));
    }
}
