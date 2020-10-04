package org.dimdev.dimdoors.client.config;

import org.dimdev.dimdoors.ModConfig;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LoadConfigScreen extends Screen {
    private final Screen parent;

    protected LoadConfigScreen(Screen parent) {
        super(new TranslatableText("dimdoors.config.title"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        MinecraftClient.getInstance().openScreen(this.getParent());
    }

    public Screen getParent() {
        return this.parent;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ButtonWidget(10, 10, 100, 20, new TranslatableText("dimdoors.config.screen.reload"), (button) -> {
            ModConfig.deserialize();
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
