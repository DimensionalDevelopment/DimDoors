package org.dimdev.dimdoors.client.config;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class UnsupportedControl<T> extends AbstractConfigControl<T> {
    public UnsupportedControl(ConfigControlRegistry registry, OptionInfo<T> option) {
        super(registry, option);
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

        host.addLabel(this.option.label(host), host.labelX(), y + 6, 0xFF5555);

        Button button = Button.builder(Component.literal("Unsupported: " + this.option.type().getSimpleName()), ignored -> {
                })
                .bounds(host.valueX(), y, host.valueWidth(), 20)
                .build();

        button.active = false;
        host.addWidget(button);
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public T pendingValue() {
        return this.option.value();
    }

    @Override
    public void commit() {
    }
}
