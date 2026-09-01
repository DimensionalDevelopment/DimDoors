package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.shaders.Uniform;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.FastColor;
import org.dimdev.dimdoors.api.client.UniformExt;

import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ModShaders {
    private static UniformExt COLORS;
    private static ShaderInstance DIMENSIONAL_PORTAL = null;

    public static void setDimensionalPortal(ShaderInstance dimensionalPortal) {
        DIMENSIONAL_PORTAL = dimensionalPortal;

        COLORS = (UniformExt) dimensionalPortal.getUniform("Colors");
    }

    public static ShaderInstance getDimensionalPortal() {
        return DIMENSIONAL_PORTAL;
    }

    public void setPortalColors(int[] colors) {
        System.arraycopy(colors, 0, ModShaders.colors, 0, 48);

        if(COLORS != null) {
            COLORS.dimensionalDoors$set(colors);
        }
    }

    private static float[] scratchColor = new float[3];

    private static float COLOR_FLOAT_TO_INT = 0.003921569f;

    private static final int[] colors = new int[]{
            0X05191C, 0X031816, 0X071919, 0X0B1C1D,
            0X101E18, 0X10161F, 0X151C2A, 0X182717,
            0X1B2131, 0X181C2F, 0X222325, 0X113E3C,
            0X322436, 0X0C5052, 0X34634D, 0X1450A8
    };
//            0.044725f, 0.025600f, 0.023878f,
//            0.025385f, 0.026288f, 0.016495f,
//            0.035209f, 0.027940f, 0.020446f,
//            0.047046f, 0.030733f, 0.026180f,
//            0.058930f, 0.033115f, 0.014088f,
//            0.060546f, 0.013968f, 0.025737f,
//            0.074899f, 0.025182f, 0.046137f,
//            0.085326f, 0.045679f, 0.000087f,
//            0.094405f, 0.029059f, 0.054086f,
//            0.094839f, 0.012288f, 0.043914f,
//            0.122657f, 0.022517f, 0.013872f,
//            0.093969f, 0.077873f, 0.056081f,
//            0.185479f, 0.005979f, 0.030018f,
//            0.110053f, 0.102196f, 0.080244f,
//            0.253339f, 0.135722f, 0.037190f,
//            0.234129f, 0.040128f, 0.273465f


    public static void renderColors() {
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
            resetColors();
            update();
        }
        if(ImGui.button("Copy To Text")) pushToClipboard();

        ImGui.end();
    }

    private static void renderColor(String name, int index) {
        int color = 0X05191C;

        scratchColor[0] = color >> 16 & 0xff;
        scratchColor[1] = color >> 8 & 0xff;
        scratchColor[2] = color & 0xff;

        if(ImGui.colorEdit3(name, scratchColor)) {
            colors[base] = scratchColor[0];
            colors[base + 1] = scratchColor[1];
            colors[base + 2] = scratchColor[2];
            update();
        }
    }

    private static void update() {
        if(COLORS != null) {
            COLORS.set(colors);
        }
    }
    
    public static void pushToClipboard() {

        var contents = IntStream.range(0, 16).map(a -> a * 3).mapToObj(layer -> String.format("%.6f, %.6f, %.6f,", colors[layer], colors[layer + 1], colors[layer + 2])).collect(Collectors.joining(",\n"));


        
        Minecraft.getInstance().keyboardHandler.setClipboard(contents);
    }

    private static void resetColors() {


        colors[0] = 0.022087f;
        colors[1] = 0.098399f;
        colors[2] = 0.110818f;
        colors[3] = 0.011892f;
        colors[4] = 0.095924f;
        colors[5] = 0.089485f;
        colors[6] = 0.027636f;
        colors[7] = 0.101689f;
        colors[8] = 0.100326f;
        colors[9] = 0.046564f;
        colors[10] = 0.109883f;
        colors[11] = 0.114838f;
        colors[12] = 0.064901f;
        colors[13] = 0.117696f;
        colors[14] = 0.097189f;
        colors[15] = 0.063761f;
        colors[16] = 0.086895f;
        colors[17] = 0.123646f;
        colors[18] = 0.084817f;
        colors[19] = 0.111994f;
        colors[20] = 0.166380f;
        colors[21] = 0.097489f;
        colors[22] = 0.154120f;
        colors[23] = 0.091064f;
        colors[24] = 0.106152f;
        colors[25] = 0.131144f;
        colors[26] = 0.195191f;
        colors[27] = 0.097721f;
        colors[28] = 0.110188f;
        colors[29] = 0.187229f;
        colors[30] = 0.133516f;
        colors[31] = 0.138278f;
        colors[32] = 0.148582f;
        colors[33] = 0.070006f;
        colors[34] = 0.243332f;
        colors[35] = 0.235792f;
        colors[36] = 0.196766f;
        colors[37] = 0.142899f;
        colors[38] = 0.214696f;
        colors[39] = 0.047281f;
        colors[40] = 0.315338f;
        colors[41] = 0.321970f;
        colors[42] = 0.204675f;
        colors[43] = 0.390010f;
        colors[44] = 0.302066f;
        colors[45] = 0.080955f;
        colors[46] = 0.314821f;
        colors[47] = 0.661491f;
    }
}
