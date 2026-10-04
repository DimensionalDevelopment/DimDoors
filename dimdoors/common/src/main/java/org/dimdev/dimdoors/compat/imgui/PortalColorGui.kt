package org.dimdev.dimdoors.compat.imgui

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import org.dimdev.dimcore.api.util.function.StreamUtils
import org.dimdev.dimdoors.PortalColors.base
import org.dimdev.dimdoors.client.ModShaders
import java.util.*
import java.util.function.IntFunction
import java.util.stream.Collectors

object PortalColorGui {
    private val SCREEN: Screen = ImGuiScreen()

    private val scratchColor = FloatArray(3)

    private val colors = IntArray(16)

    fun toggle() {
        if (Minecraft.getInstance().screen == null) {
            System.arraycopy(ModShaders.portalColors, 0, colors, 0, 16)
            Minecraft.getInstance().setScreen(SCREEN)
        }
    }

    fun render() {
        if (Minecraft.getInstance().screen !== SCREEN) return

        ImGui.begin("Portal Colors Input", ImGuiWindowFlags.AlwaysAutoResize)

        renderColor("Color 1", 0)
        renderColor("Color 2", 1)
        renderColor("Color 3", 2)
        renderColor("Color 4", 3)
        renderColor("Color 5", 4)
        renderColor("Color 6", 5)
        renderColor("Color 7", 6)
        renderColor("Color 8", 7)
        renderColor("Color 9", 8)
        renderColor("Color 10", 9)
        renderColor("Color 11", 10)
        renderColor("Color 12", 11)
        renderColor("Color 13", 12)
        renderColor("Color 14", 13)
        renderColor("Color 15", 14)
        renderColor("Color 16", 15)

        if (ImGui.button("Reset")) {
            System.arraycopy(base(), 0, colors, 0, 16)
            ModShaders.setPortalColors(colors)
        }
        if (ImGui.button("Copy To Text")) pushToClipboard()

        ImGui.end()

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
//        ImGui.end();
    }

    private fun renderColor(name: String, index: Int) {
        val color = colors[index]

        scratchColor[0] = ((color shr 16) and 0xFF) * 0.003921569f
        scratchColor[1] = ((color shr 8) and 0xFF) * 0.003921569f
        scratchColor[2] = (color and 0xFF) * 0.003921569f

        if (ImGui.colorEdit3(name, scratchColor)) {
            colors[index] = (((scratchColor[0] * 255.0f).toInt() shl 16)
                    or ((scratchColor[1] * 255.0f).toInt() shl 8)
                    or (scratchColor[2] * 255.0f).toInt())

            ModShaders.setPortalColors(colors)
        }
    }

    private fun update() {
        ModShaders.setPortalColors(colors)
    }

    fun pushToClipboard() {
        Arrays.stream(colors).mapToObj(IntFunction { i: Int -> Integer.toHexString(i) }).collect(
            StreamUtils.consuming(
                Collectors.joining(",\n"),
                { s -> Minecraft.getInstance().keyboardHandler.clipboard = s })
        )
    }
}
