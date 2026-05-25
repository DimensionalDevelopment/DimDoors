package org.dimdev.dimdoors.client.config;

import com.google.common.reflect.TypeToken;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class ResourceKeyControl<T> extends AbstractConfigControl<ResourceKey<T>> {
    private String text;
    private ResourceKey<Registry<T>> registryKey;

    public ResourceKeyControl(ConfigControlRegistry registry, OptionInfo<ResourceKey<T>> option) {
        super(registry, option);

        ResourceKey<T> value = option.value();
        this.text = value == null ? "" : value.location().toString();
        this.registryKey = this.resolveRegistryKey(option, value);
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

        EditBox box = new EditBox(
                host.font(),
                host.valueX(),
                y,
                host.valueWidth(),
                20,
                this.option.label(host)
        );

        box.setValue(this.text);
        box.setResponder(value -> {
            this.text = value;
            host.validateAll();
        });

        if (!this.validate()) {
            box.setTextColor(0xFF5555);
        }

        host.addTooltip(box, this.option.tooltipKey());
        host.addWidget(box);
    }

    @Override
    public boolean validate() {
        String trimmed = this.text.trim();

        if (trimmed.isEmpty()) {
            return true;
        }

        if (this.registryKey == null) {
            return false;
        }

        try {
            ResourceLocation.parse(trimmed);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    @Override
    public ResourceKey<T> pendingValue() {
        String trimmed = this.text.trim();

        if (trimmed.isEmpty()) {
            return null;
        }

        if (this.registryKey == null) {
            throw new IllegalStateException("Cannot create ResourceKey without resolved registry for " + this.option.key());
        }

        return ResourceKey.create(this.registryKey, ResourceLocation.parse(trimmed));
    }

    @Override
    public void commit() {
        this.option.setValue(this.pendingValue());
    }

    @SuppressWarnings("unchecked")
    private ResourceKey<Registry<T>> resolveRegistryKey(OptionInfo<ResourceKey<T>> option, ResourceKey<T> currentValue) {
        if (currentValue != null) {
            ResourceKey<? extends Registry<T>> registry = currentValue.registryKey();
            return (ResourceKey<Registry<T>>) registry;
        }

        TypeToken<?> optionToken = option.typeToken();
        Type valueType = this.resolveResourceKeyValueType(optionToken);

        if (valueType == null && option.hasField()) {
            valueType = this.resolveResourceKeyValueType(option.field().getGenericType());
        }

        if (valueType == null) {
            return null;
        }

        return this.findRegistryKeyForValueType(valueType);
    }

    private Type resolveResourceKeyValueType(TypeToken<?> token) {
        if (token == null) {
            return null;
        }

        return this.resolveResourceKeyValueType(token.getType());
    }

    private Type resolveResourceKeyValueType(Type type) {
        if (!(type instanceof ParameterizedType parameterizedType)) {
            return null;
        }

        Type rawType = parameterizedType.getRawType();

        if (rawType != ResourceKey.class) {
            return null;
        }

        Type[] arguments = parameterizedType.getActualTypeArguments();

        if (arguments.length != 1) {
            return null;
        }

        return arguments[0];
    }

    @SuppressWarnings("unchecked")
    private ResourceKey<Registry<T>> findRegistryKeyForValueType(Type valueType) {
        Class<?> rawValueClass = this.rawClass(valueType);

        if (rawValueClass == null) {
            return null;
        }

        for (Field field : Registries.class.getDeclaredFields()) {
            if (!ResourceKey.class.isAssignableFrom(field.getType())) {
                continue;
            }

            Type genericType = field.getGenericType();

            if (!(genericType instanceof ParameterizedType keyType)) {
                continue;
            }

            Type[] keyArguments = keyType.getActualTypeArguments();

            if (keyArguments.length != 1) {
                continue;
            }

            Type registryArgument = keyArguments[0];

            if (!(registryArgument instanceof ParameterizedType registryType)) {
                continue;
            }

            Type registryRawType = registryType.getRawType();

            if (registryRawType != Registry.class) {
                continue;
            }

            Type[] registryArguments = registryType.getActualTypeArguments();

            if (registryArguments.length != 1) {
                continue;
            }

            Class<?> registryValueClass = this.rawClass(registryArguments[0]);

            if (registryValueClass == null) {
                continue;
            }

            if (!registryValueClass.equals(rawValueClass)) {
                continue;
            }

            try {
                field.setAccessible(true);
                return (ResourceKey<Registry<T>>) field.get(null);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot access registry key field " + field.getName(), e);
            }
        }

        return null;
    }

    private Class<?> rawClass(Type type) {
        if (type instanceof Class<?> clazz) {
            return clazz;
        }

        if (type instanceof ParameterizedType parameterizedType && parameterizedType.getRawType() instanceof Class<?> clazz) {
            return clazz;
        }

        return null;
    }
}