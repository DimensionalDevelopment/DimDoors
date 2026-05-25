package org.dimdev.dimdoors.client.config;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public final class EnumControl<E extends Enum<E>> extends AbstractConfigControl<E> {
    private E value;

    public EnumControl(ConfigControlRegistry registry, OptionInfo<E> option) {
        super(registry, option);
        this.value = option.value();
    }

    @Override
    public int rowCount() {
        return 1;
    }

    @Override
    public void addRows(ControlHost host, RowCursor cursor) {
        int y = cursor.next();

        if (!cursor.isVisible(y)) {
            return;
        }

        host.addLabel(this.option.label(host), host.labelX(), y + 6, 0xE0E0E0);

        List<E> values = Arrays.asList(this.option.type().getEnumConstants());

        if (this.value == null && !values.isEmpty()) {
            this.value = values.getFirst();
        }

        CycleButton<E> button = CycleButton.<E>builder(EnumControl::enumLabel)
                .withValues(values)
                .withInitialValue(this.value)
                .displayOnlyValue()
                .create(host.valueX(), y, host.valueWidth(), 20, this.option.label(host), (ignored, newValue) -> this.value = newValue);

        host.addTooltip(button, this.option.tooltipKey());
        host.addWidget(button);
    }

    @Override
    public boolean validate() {
        return this.value != null;
    }

    @Override
    public E pendingValue() {
        return this.value;
    }

    private static Component enumLabel(Enum<?> value) {
        try {
            Method method = value.getClass().getMethod("getKey");
            Object result = method.invoke(value);

            if (result instanceof Component component) {
                return component;
            }

            if (result instanceof String key) {
                return Component.translatable(key);
            }
        } catch (ReflectiveOperationException ignored) {
        }

        return Component.literal(value.name());
    }
}
