package org.dimdev.dimdoors;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.registry.Registry;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import org.dimdev.dimdoors.api.DimensionalDoorsApi;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.block.door.data.condition.Condition;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.command.ModCommands;
import org.dimdev.dimdoors.command.PocketCommand;
import org.dimdev.dimdoors.enchantment.FrayedEnchantment;
import org.dimdev.dimdoors.enchantment.ModEnchants;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.criteria.ModCriteria;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.DimensionalDoorItemRegistrar;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.listener.AttackBlockCallbackListener;
import org.dimdev.dimdoors.listener.ChunkLoadListener;
import org.dimdev.dimdoors.listener.UseDoorItemOnBlockCallbackListener;
import org.dimdev.dimdoors.listener.pocket.PlayerBlockBreakEventBeforeListener;
import org.dimdev.dimdoors.listener.pocket.PocketAttackBlockCallbackListener;
import org.dimdev.dimdoors.listener.pocket.UseBlockCallbackListener;
import org.dimdev.dimdoors.listener.pocket.UseItemCallbackListener;
import org.dimdev.dimdoors.listener.pocket.UseItemOnBlockCallbackListener;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.particle.ModParticleTypes;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.feature.ModFeatures;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;
import org.jetbrains.annotations.NotNull;

import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class DimensionalDoorsInitializer implements ModInitializer {
	public static List<DimensionalDoorsApi> apiSubscribers = Collections.emptyList();
	private static final Supplier<Path> CONFIG_ROOT = () -> FabricLoader.getInstance().getConfigDir().resolve("dimdoors").toAbsolutePath();
	private static final ConfigHolder<ModConfig> CONFIG_MANAGER = AutoConfig.register(ModConfig.class, ModConfig.SubRootJanksonConfigSerializer::new);
	private static MinecraftServer server;
	private static ModContainer dimDoorsMod;
	private static DimensionalDoorBlockRegistrar dimensionalDoorBlockRegistrar;
	private static DimensionalDoorItemRegistrar dimensionalDoorItemRegistrar;

    @NotNull
    public static MinecraftServer getServer() {
        if (server != null) {
            return server;
        }
        throw new UnsupportedOperationException("Accessed server too early!");
    }

	public static DimensionalDoorBlockRegistrar getDimensionalDoorBlockRegistrar() {
		return dimensionalDoorBlockRegistrar;
	}

	public static DimensionalDoorItemRegistrar getDimensionalDoorItemRegistrar() {
		return dimensionalDoorItemRegistrar;
	}

	public static ServerWorld getWorld(RegistryKey<World> key) {
        return getServer().getWorld(key);
    }

	public static ModConfig getConfig() {
		return CONFIG_MANAGER.get();
	}

	public static ModContainer getDimDoorsMod() {
		return dimDoorsMod;
	}

	public static Path getConfigRoot() {
		return CONFIG_ROOT.get();
	}

	@Override
    public void onInitialize() {
		apiSubscribers = FabricLoader.getInstance().getEntrypoints("dimdoors:api", DimensionalDoorsApi.class);
    	dimDoorsMod = FabricLoader.getInstance().getModContainer("dimdoors").orElseThrow(RuntimeException::new);
    	ServerLifecycleEvents.SERVER_STARTING.register((minecraftServer) -> {
            server = minecraftServer;
        });

		registerRegistries();

        ModBlocks.init();
        ModItems.init();
        ModFeatures.init();
        ModBiomes.init();
        ModDimensions.init();
        ModEntityTypes.init();
		ModStats.init();
        ModBlockEntityTypes.init();
        ModCommands.init();
		ModFluids.init();
        ModSoundEvents.init();
		ModParticleTypes.init();
		ModCriteria.init();
		ModEnchants.init();
		dimensionalDoorItemRegistrar = new DimensionalDoorItemRegistrar(Registry.ITEM);
		dimensionalDoorBlockRegistrar = new DimensionalDoorBlockRegistrar(Registry.BLOCK, dimensionalDoorItemRegistrar);

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(PocketLoader.getInstance());
		ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("dimdoors", "default"), dimDoorsMod, CONFIG_MANAGER.get().getPocketsConfig().defaultPocketsResourcePackActivationType.asResourcePackActivationType());
		ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("dimdoors", "classic"), dimDoorsMod, CONFIG_MANAGER.get().getPocketsConfig().classicPocketsResourcePackActivationType.asResourcePackActivationType());

		registerListeners();
		apiSubscribers.forEach(DimensionalDoorsApi::postInitialize);
    }

    public static void registerRegistries() {
		Targets.registerDefaultTargets();
		VirtualTarget.VirtualTargetType.register();
		ImplementedVirtualPocket.VirtualPocketType.register();
		Modifier.ModifierType.register();
		PocketGenerator.PocketGeneratorType.register();
		AbstractPocket.AbstractPocketType.register();
		PocketAddon.PocketAddonType.register();
		Condition.ConditionType.register();
	}

    private void registerListeners() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			((ExtendedServerPlayNetworkHandler) handler).getDimDoorsPacketHandler().init();
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			((ExtendedServerPlayNetworkHandler) handler).getDimDoorsPacketHandler().unregister();
			PocketCommand.logSetting.remove(handler.getPlayer().getUuid());
		});

		ServerChunkEvents.CHUNK_LOAD.register(new ChunkLoadListener()); // lazy pocket gen


		AttackBlockCallback.EVENT.register(new AttackBlockCallbackListener());


		AttackBlockCallback.EVENT.register(new PocketAttackBlockCallbackListener());
		PlayerBlockBreakEvents.BEFORE.register(new PlayerBlockBreakEventBeforeListener());
		UseItemCallback.EVENT.register(new UseItemCallbackListener());
		UseItemOnBlockCallback.EVENT.register(new UseItemOnBlockCallbackListener());
		UseBlockCallback.EVENT.register(new UseBlockCallbackListener());

		// placing doors on rifts
		UseItemOnBlockCallback.EVENT.register(new UseDoorItemOnBlockCallbackListener());
	}
}
