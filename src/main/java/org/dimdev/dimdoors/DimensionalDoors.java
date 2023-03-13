package org.dimdev.dimdoors;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import org.dimdev.dimdoors.api.DimensionalDoorsApi;
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.client.ClientEvents;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.command.PocketCommand;
import org.dimdev.dimdoors.item.DimensionalDoorItemRegistrar;
import org.dimdev.dimdoors.listener.AttackBlockCallbackListener;
import org.dimdev.dimdoors.listener.ChunkLoadListener;
import org.dimdev.dimdoors.listener.UseDoorItemOnBlockCallbackListener;
import org.dimdev.dimdoors.listener.pocket.PlayerBlockBreakEventBeforeListener;
import org.dimdev.dimdoors.listener.pocket.PocketAttackBlockCallbackListener;
import org.dimdev.dimdoors.listener.pocket.UseBlockCallbackListener;
import org.dimdev.dimdoors.listener.pocket.UseItemCallbackListener;
import org.dimdev.dimdoors.listener.pocket.UseItemOnBlockCallbackListener;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.registry.RegistryHandler;

@Mod(Constants.MODID)
public class DimensionalDoors {
	public static List<DimensionalDoorsApi> apiSubscribers = Collections.emptyList();
	private static ModContainer dimDoorsMod;
	private static DimensionalDoorBlockRegistrar dimensionalDoorBlockRegistrar;
	private static DimensionalDoorItemRegistrar dimensionalDoorItemRegistrar;

	public DimensionalDoors() {
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(this::keyBindSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		RegistryHandler.init(FMLJavaModLoadingContext.get().getModEventBus());
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(CommonEvents.class);
		if(FMLEnvironment.dist == Dist.CLIENT) {
			MinecraftForge.EVENT_BUS.register(ClientEvents.class);
			FMLJavaModLoadingContext.get().getModEventBus().register(DimensionalDoorsClient.class);
		}
	}

	private void clientSetup(final FMLClientSetupEvent ev) {
	}

	public void commonSetup(FMLCommonSetupEvent ev) {

	}

	public void loadComplete(FMLLoadCompleteEvent ev) {

	}

	@SubscribeEvent
	public void registerCommands(RegisterCommandsEvent ev) {

	}

	public static DimensionalDoorBlockRegistrar getDimensionalDoorBlockRegistrar() {
		return dimensionalDoorBlockRegistrar;
	}

	public static DimensionalDoorItemRegistrar getDimensionalDoorItemRegistrar() {
		return dimensionalDoorItemRegistrar;
	}

	public static ModContainer getDimDoorsMod() {
		return dimDoorsMod;
	}
	
	public static MinecraftServer getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}
	
	public static ServerLevel getWorld(ResourceKey<Level> key) {
		return getServer().getLevel(key);
	}

	public static ResourceLocation resource(String id) {
		return new ResourceLocation("dimdoors", id);
	}

    private void registerListeners() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			((ExtendedServerPlayNetworkHandler) handler).getDimDoorsPacketHandler().init();
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			((ExtendedServerPlayNetworkHandler) handler).getDimDoorsPacketHandler().unregister();
			PocketCommand.logSetting.remove(handler.getPlayer().getUUID());
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
