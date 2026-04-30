package org.dimdev.dimdoors.client.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public final class DimDoorsConfigScreen extends Screen {
    private static final int RAIL_WIDTH = 122;
    private static final int ROW_HEIGHT = 24;
    private static final int FOOTER_HEIGHT = 32;

    private final Screen parent;
    private final ModConfig config;
    private final List<Label> labels = new ArrayList<>();
    private final List<PendingValue> pendingValues = new ArrayList<>();

    private Category category = Category.GENERAL;
    private Button doneButton;
    private int scrollOffset;
    private int maxScroll;

    public DimDoorsConfigScreen(Screen parent) {
        super(Component.translatable("text.autoconfig.dimdoors.title"));
        this.parent = parent;
        this.config = DimensionalDoors.getConfig().copy();
    }

    @Override
    protected void init() {
        rebuildWidgets();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);
        graphics.drawString(this.font, this.category.title(), RAIL_WIDTH + 14, 34, 0xFFFFFF);

        for (Label label : this.labels) {
            graphics.drawString(this.font, label.component(), label.x(), label.y(), label.color());
        }

        if (this.doneButton != null && !this.doneButton.active) {
            graphics.drawCenteredString(this.font, Component.translatable("dimdoors.config.screen.invalid"), this.width / 2, this.height - 48, 0xFF5555);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX > RAIL_WIDTH && this.maxScroll > 0) {
            int next = this.scrollOffset - (int) Math.signum(scrollY) * ROW_HEIGHT;
            this.scrollOffset = Math.max(0, Math.min(this.maxScroll, next));
            rebuildWidgets();
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    public void rebuildWidgets() {
        this.clearWidgets();
        this.labels.clear();
        this.pendingValues.clear();
        this.doneButton = null;

        int categoryY = 34;
        for (Category value : Category.values()) {
            Button button = Button.builder(value.title(), ignored -> switchCategory(value))
                    .bounds(8, categoryY, RAIL_WIDTH - 16, 20)
                    .build();
            button.active = value != this.category;
            this.addRenderableWidget(button);
            categoryY += 22;
        }

        int contentTop = 54;
        int contentBottom = this.height - FOOTER_HEIGHT - 20;
        int contentHeight = Math.max(ROW_HEIGHT, contentBottom - contentTop);
        int rows = rowCount(this.category);
        this.maxScroll = Math.max(0, rows * ROW_HEIGHT - contentHeight);
        this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, this.maxScroll));

        RowCursor cursor = new RowCursor(contentTop - this.scrollOffset, contentTop, contentBottom);
        addCategoryRows(cursor);

        this.doneButton = Button.builder(Component.translatable("gui.done"), ignored -> saveAndClose())
                .bounds(this.width / 2 - 154, this.height - 28, 150, 20)
                .build();
        this.addRenderableWidget(this.doneButton);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), ignored -> onClose())
                .bounds(this.width / 2 + 4, this.height - 28, 150, 20)
                .build());

        validateAll();
    }

    private void switchCategory(Category category) {
        this.category = category;
        this.scrollOffset = 0;
        rebuildWidgets();
    }

    private void addCategoryRows(RowCursor cursor) {
        switch (this.category) {
            case GENERAL -> addGeneralRows(cursor);
            case DOORS -> addDoorsRows(cursor);
            case POCKETS -> addPocketsRows(cursor);
            case WORLD -> addWorldRows(cursor);
            case DUNGEONS -> addDungeonsRows(cursor);
            case MONOLITHS -> addMonolithRows(cursor);
            case LIMBO -> addLimboRows(cursor);
            case GRAPHICS -> addGraphicsRows(cursor);
            case DECAY -> addDecayRows(cursor);
        }
    }

    private void addGeneralRows(RowCursor cursor) {
        ModConfig.General general = this.config.getGeneralConfig();
        addDoubleRow(cursor, "general.teleportOffset", general.teleportOffset, value -> general.teleportOffset = value);
        addBooleanRow(cursor, "general.riftBoundingBoxInCreative", general.riftBoundingBoxInCreative, value -> general.riftBoundingBoxInCreative = value);
        addDoubleRow(cursor, "general.riftCloseSpeed", general.riftCloseSpeed, value -> general.riftCloseSpeed = value);
        addDoubleRow(cursor, "general.riftGrowthSpeed", general.riftGrowthSpeed, value -> general.riftGrowthSpeed = value);
        addBooleanRow(cursor, "general.enableRiftDecay", general.enableRiftDecay, value -> general.enableRiftDecay = value);
        addIntRow(cursor, "general.depthSpreadFactor", general.depthSpreadFactor, value -> general.depthSpreadFactor = value);
        addDoubleRow(cursor, "general.endermanSpawnChance", general.endermanSpawnChance, value -> general.endermanSpawnChance = value);
        addDoubleRow(cursor, "general.endermanAggressiveChance", general.endermanAggressiveChance, value -> general.endermanAggressiveChance = value);
        addBooleanRow(cursor, "general.enableDebugMessages", general.enableDebugMessages, value -> general.enableDebugMessages = value);
    }

    private void addDoorsRows(RowCursor cursor) {
        ModConfig.Doors doors = this.config.getDoorsConfig();
        addBooleanRow(cursor, "doors.closeDoorBehind", doors.closeDoorBehind, value -> doors.closeDoorBehind = value);
        addEnumRow(cursor, "doors.doorList.mode", doors.doorList.mode, value -> doors.doorList.mode = value);
        addSetRow(cursor, "doors.doorList.doors", doors.doorList.doors, value -> doors.doorList.doors = value);
        addBooleanRow(cursor, "doors.placeRiftsInCreativeMode", doors.placeRiftsInCreativeMode, value -> doors.placeRiftsInCreativeMode = value);
    }

    private void addPocketsRows(RowCursor cursor) {
        ModConfig.Pockets pockets = this.config.getPocketsConfig();
        addIntRow(cursor, "pockets.pocketGridSize", pockets.pocketGridSize, value -> pockets.pocketGridSize = value);
        addIntRow(cursor, "pockets.maxPocketSize", pockets.maxPocketSize, value -> pockets.maxPocketSize = value);
        addIntRow(cursor, "pockets.privatePocketSize", pockets.privatePocketSize, value -> pockets.privatePocketSize = value);
        addIntRow(cursor, "pockets.publicPocketSize", pockets.publicPocketSize, value -> pockets.publicPocketSize = value);
        addStringRow(cursor, "pockets.defaultWeightEquation", pockets.defaultWeightEquation, value -> pockets.defaultWeightEquation = value);
        addIntRow(cursor, "pockets.fallbackWeight", pockets.fallbackWeight, value -> pockets.fallbackWeight = value);
        addBooleanRow(cursor, "pockets.asyncWorldEditPocketLoading", pockets.asyncWorldEditPocketLoading, value -> pockets.asyncWorldEditPocketLoading = value);
        addBooleanRow(cursor, "pockets.canUseRiftSignatureInPrivatePockets", pockets.canUseRiftSignatureInPrivatePockets, value -> pockets.canUseRiftSignatureInPrivatePockets = value);
    }

    private void addWorldRows(RowCursor cursor) {
        ModConfig.World world = this.config.getWorldConfig();
        addDoubleRow(cursor, "world.clusterGenChance", world.clusterGenChance, value -> world.clusterGenChance = value);
        addListRow(cursor, "world.clusterDimBlacklist", world.clusterDimBlacklist, value -> world.clusterDimBlacklist = value);
        addListRow(cursor, "world.gatewayDimBlacklist", world.gatewayDimBlacklist, value -> world.gatewayDimBlacklist = value);
    }

    private void addDungeonsRows(RowCursor cursor) {
        ModConfig.Dungeons dungeons = this.config.getDungeonsConfig();
        addIntRow(cursor, "dungeons.maxDungeonDepth", dungeons.maxDungeonDepth, value -> dungeons.maxDungeonDepth = value);
    }

    private void addMonolithRows(RowCursor cursor) {
        ModConfig.Monoliths monoliths = this.config.getMonolithsConfig();
        addBooleanRow(cursor, "monoliths.dangerousLimboMonoliths", monoliths.dangerousLimboMonoliths, value -> monoliths.dangerousLimboMonoliths = value);
        addBooleanRow(cursor, "monoliths.monolithTeleportation", monoliths.monolithTeleportation, value -> monoliths.monolithTeleportation = value);
    }

    private void addLimboRows(RowCursor cursor) {
        ModConfig.Limbo limbo = this.config.getLimboConfig();
        ModConfig.Limbo.WorldList worlds = limbo.getWorldsLeadingToLimbo();
        addResourceKeyListRow(cursor, "limbo.worldsLeadingToLimbo.list", worlds.list, value -> worlds.list = value);
        addBooleanRow(cursor, "limbo.worldsLeadingToLimbo.blacklist", worlds.blacklist, value -> worlds.blacklist = value);
        addBooleanRow(cursor, "limbo.hardcoreLimbo", limbo.hardcoreLimbo, value -> limbo.hardcoreLimbo = value);
        addIntRow(cursor, "limbo.limboReturnDistanceMax", limbo.limboReturnDistanceMax, value -> limbo.limboReturnDistanceMax = value);
        addIntRow(cursor, "limbo.limboReturnDistanceMin", limbo.limboReturnDistanceMin, value -> limbo.limboReturnDistanceMin = value);
        addBooleanRow(cursor, "limbo.decaySurroundings", limbo.decaySurroundings, value -> limbo.decaySurroundings = value);
        addBooleanRow(cursor, "limbo.tryPlayerBedSpawn", limbo.tryPlayerBedSpawn, value -> limbo.tryPlayerBedSpawn = value);
        addBooleanRow(cursor, "limbo.defaultToWorldSpawn", limbo.defaultToWorldSpawn, value -> limbo.defaultToWorldSpawn = value);
        addFloatRow(cursor, "limbo.limboBlocksCorruptingExitWorldAmount", limbo.limboBlocksCorruptingExitWorldAmount, value -> limbo.limboBlocksCorruptingExitWorldAmount = value);
        addNullableResourceKeyRow(cursor, "limbo.escapeTargetWorld", limbo.escapeTargetWorld, value -> limbo.escapeTargetWorld = value);
    }

    private void addGraphicsRows(RowCursor cursor) {
        ModConfig.Graphics graphics = this.config.getGraphicsConfig();
        addBooleanRow(cursor, "graphics.showRiftCore", graphics.showRiftCore, value -> graphics.showRiftCore = value);
        addIntRow(cursor, "graphics.highlightRiftCoreFor", graphics.highlightRiftCoreFor, value -> graphics.highlightRiftCoreFor = value);
        addDoubleRow(cursor, "graphics.riftSize", graphics.riftSize, value -> graphics.riftSize = value);
        addDoubleRow(cursor, "graphics.riftJitter", graphics.riftJitter, value -> graphics.riftJitter = value);
    }

    private void addDecayRows(RowCursor cursor) {
        ModConfig.Decay decay = this.config.getDecayConfig();
        addDoubleRow(cursor, "decay.decaySpreadChance", decay.decaySpreadChance, value -> decay.decaySpreadChance = value);
        addIntRow(cursor, "decay.decayDelay", decay.decayDelay, value -> decay.decayDelay = value);
        addBooleanRow(cursor, "decay.decaysIntoAir", decay.decaysIntoAir, value -> decay.decaysIntoAir = value);
    }

    private void addBooleanRow(RowCursor cursor, String key, boolean value, Consumer<Boolean> setter) {
        int y = cursor.next();
        if (!cursor.isVisible(y)) {
            return;
        }

        addLabel(key, y);
        CycleButton<Boolean> button = CycleButton.onOffBuilder(value)
                .displayOnlyValue()
                .create(valueX(), y, valueWidth(), 20, label(key), (ignored, newValue) -> setter.accept(newValue));
        addTooltip(button, key);
        this.addRenderableWidget(button);
    }

    private void addEnumRow(RowCursor cursor, String key, ModConfig.Doors.DoorList.Mode value, Consumer<ModConfig.Doors.DoorList.Mode> setter) {
        int y = cursor.next();
        if (!cursor.isVisible(y)) {
            return;
        }

        addLabel(key, y);
        CycleButton<ModConfig.Doors.DoorList.Mode> button = CycleButton
                .<ModConfig.Doors.DoorList.Mode>builder(mode -> Component.translatable(mode.getKey()))
                .withValues(ModConfig.Doors.DoorList.Mode.values())
                .withInitialValue(value)
                .displayOnlyValue()
                .create(valueX(), y, valueWidth(), 20, label(key), (ignored, newValue) -> setter.accept(newValue));
        addTooltip(button, key);
        this.addRenderableWidget(button);
    }

    private void addIntRow(RowCursor cursor, String key, int value, IntSetter setter) {
        addTextRow(cursor, key, Integer.toString(value), DimDoorsConfigScreen::isInt, text -> setter.accept(Integer.parseInt(text.trim())));
    }

    private void addFloatRow(RowCursor cursor, String key, float value, FloatSetter setter) {
        addTextRow(cursor, key, Float.toString(value), DimDoorsConfigScreen::isFloat, text -> setter.accept(Float.parseFloat(text.trim())));
    }

    private void addDoubleRow(RowCursor cursor, String key, double value, DoubleSetter setter) {
        addTextRow(cursor, key, Double.toString(value), DimDoorsConfigScreen::isDouble, text -> setter.accept(Double.parseDouble(text.trim())));
    }

    private void addStringRow(RowCursor cursor, String key, String value, Consumer<String> setter) {
        addTextRow(cursor, key, value, ignored -> true, setter);
    }

    private void addListRow(RowCursor cursor, String key, List<String> value, Consumer<List<String>> setter) {
        addTextRow(cursor, key, String.join(", ", value), ignored -> true, text -> setter.accept(parseStringList(text)));
    }

    private void addSetRow(RowCursor cursor, String key, Set<String> value, Consumer<Set<String>> setter) {
        addTextRow(cursor, key, String.join(", ", value), ignored -> true, text -> setter.accept(new HashSet<>(parseStringList(text))));
    }

    private void addResourceKeyListRow(RowCursor cursor, String key, List<ResourceKey<Level>> value, Consumer<List<ResourceKey<Level>>> setter) {
        String text = String.join(", ", value.stream().map(ResourceKey::location).map(ResourceLocation::toString).toList());
        addTextRow(cursor, key, text, DimDoorsConfigScreen::isResourceList, newValue -> setter.accept(parseResourceKeyList(newValue)));
    }

    private void addNullableResourceKeyRow(RowCursor cursor, String key, ResourceKey<Level> value, Consumer<ResourceKey<Level>> setter) {
        addTextRow(cursor, key, value == null ? "" : value.location().toString(), DimDoorsConfigScreen::isNullableResource, text -> {
            String trimmed = text.trim();
            setter.accept(trimmed.isEmpty() ? null : ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(trimmed)));
        });
    }

    private void addTextRow(RowCursor cursor, String key, String value, Predicate<String> validator, Consumer<String> setter) {
        int y = cursor.next();
        if (!cursor.isVisible(y)) {
            return;
        }

        addLabel(key, y);
        EditBox editBox = new EditBox(this.font, valueX(), y, valueWidth(), 20, label(key));
        editBox.setValue(value);
        editBox.setMaxLength(1024);
        addTooltip(editBox, key);
        this.pendingValues.add(new PendingValue(editBox, validator, setter));
        editBox.setResponder(ignored -> validateAll());
        this.addRenderableWidget(editBox);
    }

    private void addLabel(String key, int y) {
        this.labels.add(new Label(label(key), labelX(), y + 6, 0xE0E0E0));
    }

    private void addTooltip(AbstractWidget widget, String key) {
        widget.setTooltip(Tooltip.create(Component.translatable(tooltip(key))));
    }

    private boolean validateAll() {
        boolean valid = true;
        for (PendingValue pendingValue : this.pendingValues) {
            boolean fieldValid = pendingValue.validator().test(pendingValue.editBox().getValue());
            pendingValue.editBox().setTextColor(fieldValid ? 0xE0E0E0 : 0xFF5555);
            valid &= fieldValid;
        }

        if (this.doneButton != null) {
            this.doneButton.active = valid;
        }

        return valid;
    }

    private void saveAndClose() {
        if (!validateAll()) {
            return;
        }

        for (PendingValue pendingValue : this.pendingValues) {
            pendingValue.setter().accept(pendingValue.editBox().getValue());
        }

        DimensionalDoors.setConfig(this.config);
        DimensionalDoors.saveConfig();
        this.minecraft.setScreen(this.parent);
    }

    private int rowCount(Category category) {
        return switch (category) {
            case GENERAL -> 9;
            case DOORS -> 4;
            case POCKETS -> 8;
            case WORLD -> 3;
            case DUNGEONS -> 1;
            case MONOLITHS -> 2;
            case LIMBO -> 10;
            case GRAPHICS -> 4;
            case DECAY -> 3;
        };
    }

    private int labelX() {
        return RAIL_WIDTH + 14;
    }

    private int valueX() {
        return Math.max(RAIL_WIDTH + 150, this.width - valueWidth() - 24);
    }

    private int valueWidth() {
        return Math.max(120, Math.min(240, this.width - RAIL_WIDTH - 188));
    }

    private static Component label(String key) {
        return Component.translatable("text.autoconfig.dimdoors.option." + key);
    }

    private static String tooltip(String key) {
        return "text.autoconfig.dimdoors.option." + key + ".@Tooltip";
    }

    private static boolean isInt(String value) {
        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isFloat(String value) {
        try {
            Float.parseFloat(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDouble(String value) {
        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isNullableResource(String value) {
        String trimmed = value.trim();
        return trimmed.isEmpty() || ResourceLocation.tryParse(trimmed) != null;
    }

    private static boolean isResourceList(String value) {
        for (String id : parseStringList(value)) {
            if (ResourceLocation.tryParse(id) == null) {
                return false;
            }
        }

        return true;
    }

    private static List<String> parseStringList(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .toList();
    }

    private static List<ResourceKey<Level>> parseResourceKeyList(String value) {
        return parseStringList(value).stream()
                .map(ResourceLocation::parse)
                .map(id -> ResourceKey.create(Registries.DIMENSION, id))
                .collect(java.util.stream.Collectors.toCollection(LinkedList::new));
    }

    private enum Category {
        GENERAL("general"),
        DOORS("doors"),
        POCKETS("pockets"),
        WORLD("world"),
        DUNGEONS("dungeons"),
        MONOLITHS("monoliths"),
        LIMBO("limbo"),
        GRAPHICS("graphics"),
        DECAY("decay");

        private final String key;

        Category(String key) {
            this.key = key;
        }

        private Component title() {
            return Component.translatable("text.autoconfig.dimdoors.category." + this.key);
        }
    }

    private record Label(Component component, int x, int y, int color) {
    }

    private record PendingValue(EditBox editBox, Predicate<String> validator, Consumer<String> setter) {
    }

    private static final class RowCursor {
        private final int startY;
        private final int minY;
        private final int maxY;
        private int index;

        private RowCursor(int startY, int minY, int maxY) {
            this.startY = startY;
            this.minY = minY;
            this.maxY = maxY;
        }

        private int next() {
            return this.startY + this.index++ * ROW_HEIGHT;
        }

        private boolean isVisible(int y) {
            return y >= this.minY && y + 20 <= this.maxY;
        }
    }

    @FunctionalInterface
    private interface IntSetter {
        void accept(int value);
    }

    @FunctionalInterface
    private interface FloatSetter {
        void accept(float value);
    }

    @FunctionalInterface
    private interface DoubleSetter {
        void accept(double value);
    }
}
