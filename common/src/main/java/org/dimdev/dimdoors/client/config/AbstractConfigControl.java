package org.dimdev.dimdoors.client.config;

public abstract class AbstractConfigControl<T> implements ConfigControl<T> {
    protected final ConfigControlRegistry registry;
    protected final OptionInfo<T> option;

    protected AbstractConfigControl(ConfigControlRegistry registry, OptionInfo<T> option) {
        this.registry = registry;
        this.option = option;
    }

    @Override
    public OptionInfo<T> option() {
        return this.option;
    }
}
