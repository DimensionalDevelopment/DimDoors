package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.architectury.event.events.client.ClientReloadShadersEvent;
import dev.architectury.registry.menu.MenuRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.config.ModMenu;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;
import org.dimdev.dimdoors.particle.ModParticleTypes;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class DimensionalDoorsClient {
	private static final ConfigEntryBuilder ENTRY_BUILDER = ConfigEntryBuilder.create();

	public static void init() {
		MenuRegistry.registerScreenFactory(ModScreenHandlerTypes.TESSELATING_LOOM.get(), TesselatingLoomScreen::new);
        ModEntityTypes.initClient();
//		ModFluids.initClient();
        ModBlockEntityTypes.initClient();
        ModBlocks.initClient();
		ModEntityModelLayers.initClient();

		AutoConfig.getGuiRegistry(ModConfig.class).registerPredicateProvider((i18n, field, config, defaults, registry) -> Collections.singletonList(ENTRY_BUILDER
				.startStrList(Component.translatable(i18n), ((Set<String>) Utils.getUnsafely(field, config, defaults)).stream().toList())
				.setSaveConsumer(newValue -> Utils.setUnsafely(field, config, newValue.stream().collect(Collectors.toSet()))).build()), isSetOfType(String.class));

//		DimensionRenderering.initClient();

		registerListeners();

		ClientPacketHandler.init();
    }

	private static Predicate<Field> isListOfType(Type... types) {
		return field -> {
			if (List.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
				Type[] args = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
				return args.length == 1 && Stream.of(types).anyMatch(type -> Objects.equals(args[0], type));
			} else {
				return false;
			}
		};
	}

	private static Predicate<Field> isSetOfType(Type... types) {
		return field -> {
			if (Set.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
				Type[] args = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
				return args.length == 1 && Stream.of(types).anyMatch(type -> Objects.equals(args[0], type));
			} else {
				return false;
			}
		};
	}

    private static void registerListeners() {
		ClientReloadShadersEvent.EVENT.register((provider, sink) -> {
			try {
				sink.registerShader(new ShaderInstance(provider, "dimensional_portal", DefaultVertexFormat.POSITION), ModShaders::setDimensionalPortal);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
