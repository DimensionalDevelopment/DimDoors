package org.dimdev.dimdoors.client.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.common.base.Preconditions;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class ScreenGenerator {
	public static Screen create(Screen parent, Object config, Runnable saveAction) {
		Class<?> configClass = config.getClass();
		Preconditions.checkArgument(configClass.isAnnotationPresent(Title.class));
		Preconditions.checkArgument(configClass.isAnnotationPresent(GetStrategy.class));
		Preconditions.checkNotNull(saveAction);
		ConfigBuilder configBuilder = ConfigBuilder.create();
		configBuilder.setTitle(new TranslatableText(configClass.getAnnotation(Title.class).value()));
		configBuilder.setSavingRunnable(saveAction);
		GetStrategy strategy = configClass.getAnnotation(GetStrategy.class);
		for (Field field : configClass.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Category.class)
					|| Modifier.isStatic(field.getModifiers())
					|| !Modifier.isFinal(field.getModifiers())) {
				continue;
			}

			ConfigCategory category = configBuilder.getOrCreateCategory(new TranslatableText(configClass.getName().toLowerCase() + ":" + field.getName()));
			Method getter;
			try {
				getter = configClass.getMethod(getGetter(field.getName(), strategy));
			} catch (NoSuchMethodException e) {
				throw new AssertionError(e);
			}
			getter.setAccessible(true);
			Object value;
			try {
				value = getter.invoke(config);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new AssertionError(e);
			}
		}
		// TODO
		return parent;
	}

	private static String getGetter(String fieldName, GetStrategy strategy) {
		String actualFieldName = strategy.modifier() == GetStrategy.FieldNameModifier.CAPITALIZE_FIRST ? "" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1) : fieldName;
		return strategy.prefix() + actualFieldName + strategy.suffix();
	}
}
