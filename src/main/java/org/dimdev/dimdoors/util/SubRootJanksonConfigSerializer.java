package org.dimdev.dimdoors.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import blue.endless.jankson.Jankson;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.serializer.ConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1u.util.Utils;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;

public class SubRootJanksonConfigSerializer<T extends ConfigData> implements ConfigSerializer<T> {
	private static final Jankson JANKSON = Jankson.builder().build();
	private final Config definition;
	private final Class<T> configClass;

	public SubRootJanksonConfigSerializer(Config definition, Class<T> configClass) {
		this.definition = definition;
		this.configClass = configClass;
	}

	private Path getConfigPath() {
		return DimensionalDoorsInitializer.getConfigRoot().resolve(definition.name() + "-config.json5");
	}

	@Override
	public void serialize(T config) throws SerializationException {
		Path configPath = getConfigPath();
		try {
			Files.createDirectories(configPath.getParent());
			BufferedWriter writer = Files.newBufferedWriter(configPath);
			writer.write(JANKSON.toJson(config).toJson(true, true));
			writer.close();
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public T deserialize() throws SerializationException {
		Path configPath = getConfigPath();
		if (Files.exists(configPath)) {
			try {
				return JANKSON.fromJson(JANKSON.load(getConfigPath().toFile()), configClass);
			} catch (Throwable e) {
				throw new SerializationException(e);
			}
		} else {
			return createDefault();
		}
	}

	@Override
	public T createDefault() {
		return Utils.constructUnsafely(configClass);
	}
}
