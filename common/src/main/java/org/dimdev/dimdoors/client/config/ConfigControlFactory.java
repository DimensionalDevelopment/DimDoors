package org.dimdev.dimdoors.client.config;

@FunctionalInterface
public interface ConfigControlFactory<T> {
    ConfigControl<T> create(ConfigControlRegistry registry, OptionInfo<T> option);
}
