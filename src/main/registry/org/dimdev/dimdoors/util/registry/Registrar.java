package org.dimdev.dimdoors.util.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Registrar {
	@SuppressWarnings({"unchecked", "rawtypes"})
	Consumer<Class<?>> REGISTER = (clazz) -> {
		Registrar registrar = clazz.getAnnotation(Registrar.class);
		if (registrar == null) {
			return;
		}

		String modid = registrar.modid();
		Class<?> element = registrar.element();
		Registry<?> registry;
		try {
			registry = (Registry<?>) clazz.getField("REGISTRY").get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new AssertionError();
		}

		Arrays.stream(clazz.getFields())
				.filter(field -> field.getType() == element
						&& field.isAnnotationPresent(RegistryObject.class)
						&& Modifier.isPublic(field.getModifiers())
						&& Modifier.isStatic(field.getModifiers())
						&& Modifier.isFinal(field.getModifiers())
				)
				.forEach(field -> {
					try {
						Object value = field.get(null);
						Registry.register((Registry) registry, new Identifier(modid, field.getAnnotation(RegistryObject.class).value()), element.cast(value));
					} catch (IllegalAccessException e) {
						throw new AssertionError(e);
					}
				});
	};

	Class<?> element();

	String modid() default "dimdoors";
}
