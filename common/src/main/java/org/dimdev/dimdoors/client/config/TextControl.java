package org.dimdev.dimdoors.client.config;

import net.minecraft.client.gui.components.EditBox;

import java.util.function.Function;
import java.util.function.Predicate;

public final class TextControl<T> extends AbstractConfigControl<T> {
    private final Predicate<String> validator;
    private final Function<String, T> parser;
    private String text;
    private EditBox visibleBox;

    private TextControl(
            ConfigControlRegistry registry,
            OptionInfo<T> option,
            String text,
            Predicate<String> validator,
            Function<String, T> parser
    ) {
        super(registry, option);
        this.text = text;
        this.validator = validator;
        this.parser = parser;
    }

    public static ConfigControl<Integer> integer(ConfigControlRegistry registry, OptionInfo<Integer> option) {
        Integer value = option.value();
        return new TextControl<>(registry, option, value == null ? "0" : Integer.toString(value), TextControl::isInt, text -> Integer.parseInt(text.trim()));
    }

    public static ConfigControl<Float> floatingPoint(ConfigControlRegistry registry, OptionInfo<Float> option) {
        Float value = option.value();
        return new TextControl<>(registry, option, value == null ? "0.0" : Float.toString(value), TextControl::isFloat, text -> Float.parseFloat(text.trim()));
    }

    public static ConfigControl<Double> decimal(ConfigControlRegistry registry, OptionInfo<Double> option) {
        Double value = option.value();
        return new TextControl<>(registry, option, value == null ? "0.0" : Double.toString(value), TextControl::isDouble, text -> Double.parseDouble(text.trim()));
    }

    public static ConfigControl<String> string(ConfigControlRegistry registry, OptionInfo<String> option) {
        String value = option.value();
        return new TextControl<>(registry, option, value == null ? "" : value, ignored -> true, text -> text);
    }

    @Override
    public int rowCount() {
        return 1;
    }

    @Override
    public void addRows(ControlHost host, RowCursor cursor) {
        int y = cursor.next();

        if (!cursor.isVisible(y)) {
            this.visibleBox = null;
            return;
        }

        host.addLabel(this.option.label(host), host.labelX(), y + 6, 0xE0E0E0);

        EditBox editBox = new EditBox(host.font(), host.valueX(), y, host.valueWidth(), 20, this.option.label(host));
        editBox.setValue(this.text);
        editBox.setMaxLength(1024);
        editBox.setResponder(value -> {
            this.text = value;
            host.validateAll();
        });

        this.visibleBox = editBox;
        host.addTooltip(editBox, this.option.tooltipKey());
        host.addWidget(editBox);
    }

    @Override
    public boolean validate() {
        boolean valid = this.validator.test(this.text);

        if (this.visibleBox != null) {
            this.visibleBox.setTextColor(valid ? 0xE0E0E0 : 0xFF5555);
        }

        return valid;
    }

    @Override
    public T pendingValue() {
        return this.parser.apply(this.text);
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
            float parsed = Float.parseFloat(value.trim());
            return Float.isFinite(parsed);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDouble(String value) {
        try {
            double parsed = Double.parseDouble(value.trim());
            return Double.isFinite(parsed);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
