package org.dimdev.dimdoors;

import dev.architectury.event.events.common.ChunkEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.GameInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.api.DimensionalDoorsApi;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.config.ModMenu;
import org.dimdev.dimdoors.command.ModCommands;
import org.dimdev.dimdoors.command.PocketCommand;
import org.dimdev.dimdoors.criteria.ModCriteria;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.item.door.DoorRiftDataLoader;
import org.dimdev.dimdoors.item.door.data.condition.Condition;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.recipe.ModRecipeSerializers;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.decay.DecayPredicate;
import org.dimdev.dimdoors.world.decay.DecayProcessor;
import org.dimdev.dimdoors.world.decay.LimboDecay;
import org.dimdev.dimdoors.world.feature.ModFeatures;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.List;

import static net.fabricmc.loader.api.FabricLoader.getInstance;

public class DimensionalDoors {
	public static final String MOD_ID = "dimdoors";
	public static List<DimensionalDoorsApi> apiSubscribers;
	private Mod dimDoorsMod;

	public static ResourceLocation id(String id) {
		return new ResourceLocation(MOD_ID, id);
	}

	public static MinecraftServer getServer() {
		return GameInstance.getServer();
	}

	public static ServerLevel getWorld(ResourceKey<Level> world) {
		return getServer().getLevel(world);
	}

	public void onInitialize() {
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

		ReloadListenerRegistry.register(PackType.SERVER_DATA, PocketLoader.getInstance());
		ReloadListenerRegistry.register(PackType.SERVER_DATA, LimboDecay.DecayLoader.getInstance());
		ReloadListenerRegistry.register(PackType.SERVER_DATA, DoorRiftDataLoader.getInstance());
//		ResourceManagerHelper.registerBuiltinResourcePack(id("default"), dimDoorsMod, CONFIG_MANAGER.get().getPocketsConfig().defaultPocketsResourcePackActivationType.asResourcePackActivationType()); TODO:Figure out how to do this multiplat
//		ResourceManagerHelper.registerBuiltinResourcePack(id("classic"), dimDoorsMod, CONFIG_MANAGER.get().getPocketsConfig().classicPocketsResourcePackActivationType.asResourcePackActivationType());

		registerListeners();
		apiSubscribers.forEach(DimensionalDoorsApi::postInitialize);
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

	private void registerListeners() {
		PlayerEvent.PLAYER_JOIN.register((handler) -> ((ExtendedServerPlayNetworkHandler) handler).getDimDoorsPacketHandler().init());

		PlayerEvent.PLAYER_QUIT.register((handler) -> {
			((ExtendedServerPlayNetworkHandler) handler).getDimDoorsPacketHandler().unregister();
			PocketCommand.logSetting.remove(handler.getUUID());
		});

		ChunkEvent.CHUNK_LOAD.register(new ChunkLoadListener()); // lazy pocket gen


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
