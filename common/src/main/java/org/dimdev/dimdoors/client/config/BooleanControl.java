package org.dimdev.dimdoors.client.config;

import net.minecraft.client.gui.components.CycleButton;

public final class BooleanControl extends AbstractConfigControl<Boolean> {
    private boolean value;

    public BooleanControl(ConfigControlRegistry registry, OptionInfo<Boolean> option) {
        super(registry, option);
        this.value = Boolean.TRUE.equals(option.value());
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

        CycleButton<Boolean> button = CycleButton.onOffBuilder(this.value)
                .displayOnlyValue()
                .create(
                        host.valueX(),
                        y,
                        host.valueWidth(),
                        20,
                        this.option.label(host),
                        (ignored, newValue) -> this.value = newValue
                );

        host.addTooltip(button, this.option.tooltipKey());
        host.addWidget(button);
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public Boolean pendingValue() {
        return this.value;
    }
}
