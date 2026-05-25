package org.dimdev.dimdoors.client.config;

import net.minecraft.resources.ResourceKey;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ConfigControlRegistry {
    private final Map<Class<?>, ConfigControlFactory<?>> factories = new LinkedHashMap<>();

    public static ConfigControlRegistry defaults() {
        ConfigControlRegistry registry = new ConfigControlRegistry();

        registry.register(Boolean.class, BooleanControl::new);

        registry.register(Integer.class, TextControl::integer);
        registry.register(Float.class, TextControl::floatingPoint);
        registry.register(Double.class, TextControl::decimal);

        registry.register(String.class, TextControl::string);
        registry.register(ResourceKey.class, ResourceKeyControl::new);

        registry.register(Collection.class, CollectionControl::create);

        return registry;
    }

    public ConfigControlRegistry copy() {
        ConfigControlRegistry copy = new ConfigControlRegistry();
        copy.factories.putAll(this.factories);
        return copy;
    }

    public <T> ConfigControlRegistry register(Class<T> type, ConfigControlFactory<T> factory) {
        this.factories.put(Objects.requireNonNull(type, "type"), Objects.requireNonNull(factory, "factory"));
        return this;
    }

    public <T> ConfigControl<T> create(OptionInfo<T> option) {
        Class<?> actualType = option.actualType();
        ConfigControlFactory<T> factory = this.findFactory(actualType);

        if (factory != null) {
            return factory.create(this, option);
        }

        if (option.type().isEnum() || actualType.isEnum()) {
            return this.createEnum(option);
        }

        if (ConfigReflection.isExpandableConfigObjectType(actualType)) {
            T value = option.value();

            if (value != null) {
                return new ObjectControl<>(this, option);
            }
        }

        return new UnsupportedControl<>(this, option);
    }

    public ConfigControl<?> createCaptured(OptionInfo<?> option) {
        return this.createCaptured0(option);
    }

    private <T> ConfigControl<T> createCaptured0(OptionInfo<T> option) {
        return this.create(option);
    }

    @SuppressWarnings("unchecked")
    private <T> ConfigControlFactory<T> findFactory(Class<?> type) {
        ConfigControlFactory<?> exact = this.factories.get(type);

        if (exact != null) {
            return (ConfigControlFactory<T>) exact;
        }

        for (Map.Entry<Class<?>, ConfigControlFactory<?>> entry : this.factories.entrySet()) {
            Class<?> registered = entry.getKey();

            if (!registered.isPrimitive() && registered.isAssignableFrom(type)) {
                return (ConfigControlFactory<T>) entry.getValue();
            }
        }

        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> ConfigControl<T> createEnum(OptionInfo<T> option) {
        return (ConfigControl<T>) new EnumControl(this, (OptionInfo) option);
    }
}
