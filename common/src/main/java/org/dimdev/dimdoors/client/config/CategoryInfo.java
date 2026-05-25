package org.dimdev.dimdoors.client.config;

import org.dimdev.dimdoors.config.Category;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class CategoryInfo {
    private final String key;
    private final Field field;
    private final Object owner;
    private final Object value;
    private final Category annotation;
    private final List<OptionInfo<?>> options;
    private final List<ConfigControl<?>> controls = new ArrayList<>();

    public CategoryInfo(String key, Field field, Object owner, Object value, Category annotation, List<OptionInfo<?>> options) {
        this.key = key;
        this.field = field;
        this.owner = owner;
        this.value = value;
        this.annotation = annotation;
        this.options = List.copyOf(options);
    }

    public String key() {
        return this.key;
    }

    public Field field() {
        return this.field;
    }

    public Object owner() {
        return this.owner;
    }

    public Object value() {
        return this.value;
    }

    public Category annotation() {
        return this.annotation;
    }

    public List<OptionInfo<?>> options() {
        return this.options;
    }

    public List<ConfigControl<?>> controls() {
        return this.controls;
    }

    public void buildControls(ConfigControlRegistry registry) {
        this.controls.clear();

        for (OptionInfo<?> option : this.options) {
            this.controls.add(registry.create(option));
        }
    }
}
