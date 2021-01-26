package org.dimdev.dimdoors.client.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.DoubleFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntListBuilder;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ScreenGenerator {
	public static Screen create(Screen parent, Object config, Runnable saveAction) {
		Class<?> configClass = config.getClass();
		Preconditions.checkArgument(configClass.isAnnotationPresent(Title.class));
		Preconditions.checkNotNull(saveAction);
		ConfigBuilder configBuilder = ConfigBuilder.create();
		ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
		configBuilder.setTitle(new TranslatableText(configClass.getAnnotation(Title.class).value()));
		configBuilder.setSavingRunnable(saveAction);
		configBuilder.setParentScreen(parent);
		for (Field field : configClass.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Category.class)
					|| Modifier.isStatic(field.getModifiers())
					|| !Modifier.isFinal(field.getModifiers())) {
				continue;
			}

			ConfigCategory category = configBuilder.getOrCreateCategory(new TranslatableText(configClass.getName().toLowerCase() + ":" + field.getName()));
			Object value;
			try {
				if (field.isAnnotationPresent(Private.class)) {
					field.setAccessible(true);
				}
				value = field.get(config);
			} catch (IllegalAccessException e) {
				throw new AssertionError(e);
			}

			Class<?> valueClass = value.getClass();
			for (Field innerField : valueClass.getFields()) {
				if (!field.isAnnotationPresent(Expose.class)
						|| Modifier.isStatic(field.getModifiers())
						|| Modifier.isFinal(field.getModifiers())) {
					continue;
				}

				Optional<Text[]> tooltipSupplier = Optional.of(Optional.of(innerField)
						.filter(f -> f.isAnnotationPresent(Tooltips.class))
						.map(f -> f.getAnnotation(Tooltips.class))
						.map(Tooltips::value)
						.map(Arrays::stream)
						.orElse(Stream.empty())
						.map(tooltip -> {
							if (tooltip.absolute()) return new LiteralText(tooltip.value());
							else return new TranslatableText(valueClass.getName().toLowerCase() + "." + innerField.getName() + ":" + tooltip.value());
						})
						.map(text -> (Text) text)
						.toArray(Text[]::new))
						.filter(texts -> texts.length > 0);

				try {
					if (innerField.getType() == boolean.class) {
						BooleanToggleBuilder builder = entryBuilder.startBooleanToggle(new TranslatableText(valueClass.getName().toLowerCase() + ":" + innerField.getName()), innerField.getBoolean(value))
								.setTooltip(tooltipSupplier)
								.setSaveConsumer(bool -> {
									try {
										innerField.setBoolean(value, bool);
									} catch (IllegalAccessException e) {
										throw new AssertionError();
									}
								});
						builder.requireRestart(field.isAnnotationPresent(RequiresRestart.class));
						category.addEntry(builder.build());
					} else if (innerField.getType() == int.class) {
						IntFieldBuilder builder = entryBuilder.startIntField(new TranslatableText(valueClass.getName().toLowerCase() + ":" + innerField.getName()), innerField.getInt(value))
								.setTooltip(tooltipSupplier)
								.setSaveConsumer(i -> {
									try {
										innerField.setInt(value, i);
									} catch (IllegalAccessException e) {
										throw new AssertionError();
									}
								});
						builder.requireRestart(field.isAnnotationPresent(RequiresRestart.class));
						category.addEntry(builder.build());
					} else if (innerField.getType() == double.class) {
						DoubleFieldBuilder builder = entryBuilder.startDoubleField(new TranslatableText(valueClass.getName().toLowerCase() + ":" + innerField.getName()), innerField.getDouble(value))
								.setTooltip(tooltipSupplier)
								.setSaveConsumer(d -> {
									try {
										innerField.setDouble(value, d);
									} catch (IllegalAccessException e) {
										throw new AssertionError();
									}
								});
						builder.requireRestart(field.isAnnotationPresent(RequiresRestart.class));
						category.addEntry(builder.build());
					} else if (innerField.isAnnotationPresent(IntSet.class)) {
						IntListBuilder builder = entryBuilder.startIntList(new TranslatableText(valueClass.getName().toLowerCase() + ":" + innerField.getName()), new ArrayList<>(((Set<Integer>) innerField.get(value))))
								.setTooltip(tooltipSupplier)
								.setSaveConsumer(set -> {
									try {
										innerField.set(value, set);
									} catch (IllegalAccessException e) {
										throw new AssertionError();
									}
								});
						builder.requireRestart(field.isAnnotationPresent(RequiresRestart.class));
						category.addEntry(builder.build());
					} else {
						throw new AssertionError();
					}
				} catch (IllegalAccessException e) {
					throw new AssertionError("Invalid type " + innerField.getType().getName(), e);
				}
			}
		}
		return configBuilder.build();
	}
}
