package org.dimdev.dimdoors.client.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.dimdev.dimdoors.ModConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public final class DimDoorsConfigScreen extends Screen {
    private final Screen parent;
    private final ModConfig config;
    private final Consumer<ModConfig> save;
    private final String translationPrefix;
    private final ConfigControlRegistry controls;

    private ConfigScreenWidget configWidget;

    private DimDoorsConfigScreen(Builder builder) {
        super(builder.title);
        this.parent = builder.parent;
        this.config = builder.config;
        this.save = builder.save;
        this.translationPrefix = builder.translationPrefix;
        this.controls = builder.controls.copy();
    }

    public static Builder builder(ModConfig config) {
        return new Builder(config);
    }

    @Override
    protected void init() {
        this.clearWidgets();

        if (this.configWidget == null) {
            this.configWidget = new ConfigScreenWidget(
                    0,
                    0,
                    this.width,
                    this.height,
                    this.font,
                    this.title,
                    this.config,
                    ConfigReflection.scan(this.config),
                    this.translationPrefix,
                    this.controls,
                    this::saveAndClose,
                    this::onClose
            );
        } else {
            this.configWidget.setArea(0, 0, this.width, this.height);
        }

        this.addRenderableWidget(this.configWidget);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        Objects.requireNonNull(this.minecraft).setScreen(this.parent);
    }

    private void saveAndClose() {
        this.save.accept(this.config);
        Objects.requireNonNull(this.minecraft).setScreen(this.parent);
    }

    public static final class Builder {
        private final ModConfig config;
        private Screen parent;
        private Component title = Component.translatable("text.autoconfig.dimdoors.title");
        private String translationPrefix = "text.autoconfig.dimdoors";
        private Consumer<ModConfig> save;
        private final ConfigControlRegistry controls = ConfigControlRegistry.defaults();

        private Builder(ModConfig config) {
            this.config = Objects.requireNonNull(config, "config");
        }

        public Builder parent(Screen parent) {
            this.parent = parent;
            return this;
        }

        public Builder title(Component title) {
            this.title = Objects.requireNonNull(title, "title");
            return this;
        }

        public Builder translationPrefix(String translationPrefix) {
            this.translationPrefix = Objects.requireNonNull(translationPrefix, "translationPrefix");
            return this;
        }

        public Builder save(Consumer<ModConfig> save) {
            this.save = Objects.requireNonNull(save, "save");
            return this;
        }

        public Builder register(Class<?> type, ConfigControlFactory factory) {
            this.controls.register(type, factory);
            return this;
        }

        public Builder configureControls(Consumer<ConfigControlRegistry> configurator) {
            configurator.accept(this.controls);
            return this;
        }

        public DimDoorsConfigScreen build() {
            if (this.save == null) {
                throw new IllegalStateException("Missing config save callback");
            }

            return new DimDoorsConfigScreen(this);
        }
    }
}
