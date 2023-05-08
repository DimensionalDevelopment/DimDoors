package org.dimdev.dimdoors;

import dev.architectury.event.events.common.ChunkEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.GameInstance;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.config.ModMenu;
import org.dimdev.dimdoors.command.ModCommands;
import org.dimdev.dimdoors.command.PocketCommand;
import org.dimdev.dimdoors.criteria.ModCriteria;
import org.dimdev.dimdoors.enchantment.ModEnchants;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.item.door.DoorRiftDataLoader;
import org.dimdev.dimdoors.item.door.data.condition.Condition;
import org.dimdev.dimdoors.listener.AttackBlockCallbackListener;
import org.dimdev.dimdoors.listener.ChunkLoadListener;
import org.dimdev.dimdoors.listener.UseDoorItemOnBlockCallbackListener;
import org.dimdev.dimdoors.listener.pocket.PocketAttackBlockCallbackListener;
import org.dimdev.dimdoors.listener.pocket.UseBlockCallbackListener;
import org.dimdev.dimdoors.listener.pocket.UseItemCallbackListener;
import org.dimdev.dimdoors.listener.pocket.UseItemOnBlockCallbackListener;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.packet.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.particle.ModParticleTypes;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.recipe.ModRecipeSerializers;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;
import org.dimdev.dimdoors.rift.registry.RegistryVertex;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.util.schematic.SchemFixer;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.decay.DecayPredicate;
import org.dimdev.dimdoors.world.decay.DecayProcessor;
import org.dimdev.dimdoors.world.decay.LimboDecay;
import org.dimdev.dimdoors.world.feature.ModFeatures;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.nio.file.Path;
import java.util.function.Supplier;

public class DimensionalDoors {
	public static final String MOD_ID = "dimdoors";
	private static Mod dimDoorsMod;

	public static ResourceLocation id(String id) {
		return new ResourceLocation(MOD_ID, id);
	}

	public static MinecraftServer getServer() {
		return GameInstance.getServer();
	}

	public static ServerLevel getWorld(ResourceKey<Level> world) {
		return getServer().getLevel(world);
	}

	private static Supplier<Path> CONFIG_ROOT = () -> dimDoorsMod.getFilePaths().get(0);

	private static final ConfigHolder<ModConfig> CONFIG_MANAGER = AutoConfig.register(ModConfig.class, ModConfig.SubRootJanksonConfigSerializer::new);

	public static ModConfig getConfig() {
		return CONFIG_MANAGER.get();
	}

	public static Path getConfigRoot() {
		return CONFIG_ROOT.get();
	}

	public static final NetworkChannel NETWORK = NetworkChannel.create(DimensionalDoors.id("server"));

	public static void init() {
		dimDoorsMod = Platform.getMod(MOD_ID);

		dimDoorsMod.registerConfigurationScreen(ModMenu::getConfigScreen); //TODO: Move to client.

		registerRegistries();

		ModRecipeTypes.init();
		ModRecipeSerializers.init();
		ModScreenHandlerTypes.init();
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

		ServerPacketHandler.init();

		ReloadListenerRegistry.register(PackType.SERVER_DATA, PocketLoader.getInstance());
		ReloadListenerRegistry.register(PackType.SERVER_DATA, LimboDecay.DecayLoader.getInstance());
		ReloadListenerRegistry.register(PackType.SERVER_DATA, DoorRiftDataLoader.getInstance());
//		ResourceManagerHelper.registerBuiltinResourcePack(id("default"), dimDoorsMod, CONFIG_MANAGER.get().getPocketsConfig().defaultPocketsResourcePackActivationType.asResourcePackActivationType()); TODO:Figure out how to do this multiplat
//		ResourceManagerHelper.registerBuiltinResourcePack(id("classic"), dimDoorsMod, CONFIG_MANAGER.get().getPocketsConfig().classicPocketsResourcePackActivationType.asResourcePackActivationType());

		registerListeners();
		SchemFixer.run();
	}

	public static void registerRegistries() {
		Targets.registerDefaultTargets();
		VirtualTarget.VirtualTargetType.register();
		ImplementedVirtualPocket.VirtualPocketType.register();
		RegistryVertex.RegistryVertexType.register();
		Modifier.ModifierType.register();
		PocketGenerator.PocketGeneratorType.register();
		AbstractPocket.AbstractPocketType.register();
		PocketAddon.PocketAddonType.register();
		Condition.ConditionType.register();
		DecayPredicate.DecayPredicateType.register();
		DecayProcessor.DecayProcessorType.register();
	}

	private static void registerListeners() {
		PlayerEvent.PLAYER_JOIN.register((handler) -> {
			ServerPacketHandler.sendPacket(handler, new NetworkHandlerInitializedC2SPacket());
			((ExtendedServerPlayNetworkHandler) handler).getDimDoorsPacketHandler().init();
		});

		PlayerEvent.PLAYER_QUIT.register((handler) -> {
			((ExtendedServerPlayNetworkHandler) handler).getDimDoorsPacketHandler().unregister();
			PocketCommand.logSetting.remove(handler.getUUID());
		});

		ChunkEvent.LOAD_DATA.register(new ChunkLoadListener()); // lazy pocket gen


		InteractionEvent.LEFT_CLICK_BLOCK.register(new AttackBlockCallbackListener());


		InteractionEvent.LEFT_CLICK_BLOCK.register(new PocketAttackBlockCallbackListener());
//		PlayerBlockBreakEvents.BEFORE.register(new PlayerBlockBreakEventBeforeListener()); TODO: Fix
		InteractionEvent.RIGHT_CLICK_ITEM.register(new UseItemCallbackListener());
		UseItemOnBlockCallback.EVENT.register(new UseItemOnBlockCallbackListener());
		InteractionEvent.RIGHT_CLICK_BLOCK.register(new UseBlockCallbackListener());

		// placing doors on rifts
		UseItemOnBlockCallback.EVENT.register(new UseDoorItemOnBlockCallbackListener());
	}
}
