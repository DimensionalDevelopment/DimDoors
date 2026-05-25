package org.dimdev.dimdoors.client.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ConfigScreenWidget extends AbstractWidget implements ControlHost {
    private static final int RAIL_WIDTH = 122;
    private static final int ROW_HEIGHT = 24;
    private static final int FOOTER_HEIGHT = 32;

    private final Font font;
    private final Object config;
    private final List<CategoryInfo> categories;
    private final String translationPrefix;
    private final Runnable saveAndClose;
    private final Runnable cancel;

    private final List<AbstractWidget> widgets = new ArrayList<>();
    private final List<RowLabel> labels = new ArrayList<>();

    private int categoryIndex;
    private int scrollOffset;
    private int maxScroll;
    private Button doneButton;
    private AbstractWidget focusedWidget;

    public ConfigScreenWidget(
            int x,
            int y,
            int width,
            int height,
            Font font,
            Component title,
            Object config,
            List<CategoryInfo> categories,
            String translationPrefix,
            ConfigControlRegistry registry,
            Runnable saveAndClose,
            Runnable cancel
    ) {
        super(x, y, width, height, title);
        this.font = Objects.requireNonNull(font, "font");
        this.config = Objects.requireNonNull(config, "config");
        this.categories = new ArrayList<>(Objects.requireNonNull(categories, "categories"));
        this.translationPrefix = Objects.requireNonNull(translationPrefix, "translationPrefix");
        this.saveAndClose = Objects.requireNonNull(saveAndClose, "saveAndClose");
        this.cancel = Objects.requireNonNull(cancel, "cancel");

        for (CategoryInfo category : this.categories) {
            category.buildControls(registry);
        }

        rebuild();
    }

    public void setArea(int x, int y, int width, int height) {
        this.setX(x);
        this.setY(y);
        this.width = width;
        this.height = height;
        rebuild();
    }

    public Object config() {
        return this.config;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.drawCenteredString(this.font, this.getMessage(), this.getX() + this.width / 2, this.getY() + 12, 0xFFFFFF);

        CategoryInfo category = currentCategory();
        if (category != null) {
            graphics.drawString(this.font, categoryTitle(category.key()), this.labelX(), this.getY() + 34, 0xFFFFFF);
        }

        for (RowLabel label : this.labels) {
            graphics.drawString(this.font, label.component(), label.x(), label.y(), label.color());
        }

        for (AbstractWidget widget : this.widgets) {
            widget.render(graphics, mouseX, mouseY, delta);
        }

        if (this.doneButton != null && !this.doneButton.active) {
            graphics.drawCenteredString(this.font, Component.translatable(this.translationPrefix + ".screen.invalid"), this.getX() + this.width / 2, this.getY() + this.height - 48, 0xFF5555);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = this.widgets.size() - 1; i >= 0; i--) {
            AbstractWidget widget = this.widgets.get(i);

            if (widget.mouseClicked(mouseX, mouseY, button)) {
                focus(widget);
                return true;
            }
        }

        focus(null);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.focusedWidget != null && this.focusedWidget.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }

        for (AbstractWidget widget : this.widgets) {
            if (widget.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.focusedWidget != null && this.focusedWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX > this.getX() + RAIL_WIDTH && this.maxScroll > 0) {
            int next = this.scrollOffset - (int) Math.signum(scrollY) * ROW_HEIGHT;
            this.scrollOffset = Mth.clamp(next, 0, this.maxScroll);
            rebuild();
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.focusedWidget != null && this.focusedWidget.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return this.focusedWidget != null && this.focusedWidget.charTyped(codePoint, modifiers);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }

    @Override
    public Font font() {
        return this.font;
    }

    @Override
    public int rowHeight() {
        return ROW_HEIGHT;
    }

    @Override
    public int labelX() {
        return this.getX() + RAIL_WIDTH + 14;
    }

    @Override
    public int valueX() {
        return Math.max(this.getX() + RAIL_WIDTH + 150, this.getX() + this.width - valueWidth() - 24);
    }

    @Override
    public int valueWidth() {
        return Mth.clamp(this.width - RAIL_WIDTH - 188, 120, 240);
    }

    @Override
    public Component optionLabel(String key) {
        return Component.translatable(this.translationPrefix + ".option." + key);
    }

    @Override
    public String optionTooltip(String key) {
        return this.translationPrefix + ".option." + key + ".@Tooltip";
    }

    @Override
    public void addLabel(Component component, int x, int y, int color) {
        this.labels.add(new RowLabel(component, x, y, color));
    }

    @Override
    public void addWidget(AbstractWidget widget) {
        this.widgets.add(widget);
    }

    @Override
    public void addTooltip(AbstractWidget widget, String optionKey) {
        widget.setTooltip(Tooltip.create(Component.translatable(optionTooltip(optionKey))));
    }

    @Override
    public void rebuild() {
        this.widgets.clear();
        this.labels.clear();
        this.doneButton = null;
        focus(null);

        int categoryY = this.getY() + 34;
        for (int i = 0; i < this.categories.size(); i++) {
            int index = i;
            CategoryInfo category = this.categories.get(i);
            Button button = Button.builder(categoryTitle(category.key()), ignored -> switchCategory(index))
                    .bounds(this.getX() + 8, categoryY, RAIL_WIDTH - 16, 20)
                    .build();
            button.active = i != this.categoryIndex;
            this.widgets.add(button);
            categoryY += 22;
        }

        int contentTop = this.getY() + 54;
        int contentBottom = this.getY() + this.height - FOOTER_HEIGHT - 20;
        int contentHeight = Math.max(ROW_HEIGHT, contentBottom - contentTop);
        int rows = rowCount();
        this.maxScroll = Math.max(0, rows * ROW_HEIGHT - contentHeight);
        this.scrollOffset = Mth.clamp(this.scrollOffset, 0, this.maxScroll);

        RowCursor cursor = new RowCursor(contentTop - this.scrollOffset, contentTop, contentBottom, ROW_HEIGHT);
        CategoryInfo category = currentCategory();
        if (category != null) {
            for (ConfigControl<?> control : category.controls()) {
                control.addRows(this, cursor);
            }
        }

        this.doneButton = Button.builder(Component.translatable("gui.done"), ignored -> commitAndSave())
                .bounds(this.getX() + this.width / 2 - 154, this.getY() + this.height - 28, 150, 20)
                .build();
        this.widgets.add(this.doneButton);

        this.widgets.add(Button.builder(Component.translatable("gui.cancel"), ignored -> this.cancel.run())
                .bounds(this.getX() + this.width / 2 + 4, this.getY() + this.height - 28, 150, 20)
                .build());

        validateAll();
    }

    @Override
    public boolean validateAll() {
        boolean valid = true;

        for (CategoryInfo category : this.categories) {
            for (ConfigControl<?> control : category.controls()) {
                valid &= control.validate();
            }
        }

        if (this.doneButton != null) {
            this.doneButton.active = valid;
        }

        return valid;
    }

    private Component categoryTitle(String key) {
        return Component.translatable(this.translationPrefix + ".category." + key);
    }

    private void switchCategory(int index) {
        this.categoryIndex = index;
        this.scrollOffset = 0;
        rebuild();
    }

    private CategoryInfo currentCategory() {
        if (this.categories.isEmpty()) {
            return null;
        }

        this.categoryIndex = Mth.clamp(this.categoryIndex, 0, this.categories.size() - 1);
        return this.categories.get(this.categoryIndex);
    }

    private int rowCount() {
        CategoryInfo category = currentCategory();

        if (category == null) {
            return 0;
        }

        int rows = 0;
        for (ConfigControl<?> control : category.controls()) {
            rows += control.rowCount();
        }
        return rows;
    }

    private void commitAndSave() {
        if (!validateAll()) {
            return;
        }

        for (CategoryInfo category : this.categories) {
            for (ConfigControl<?> control : category.controls()) {
                control.commit();
            }
        }

        this.saveAndClose.run();
    }

    private void focus(AbstractWidget widget) {
        if (this.focusedWidget != null) {
            this.focusedWidget.setFocused(false);
        }

        this.focusedWidget = widget;

        if (this.focusedWidget != null) {
            this.focusedWidget.setFocused(true);
        }
    }

    private record RowLabel(Component component, int x, int y, int color) {
    }
}
