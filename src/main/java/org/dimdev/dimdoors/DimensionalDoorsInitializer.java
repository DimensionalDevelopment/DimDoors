package org.dimdev.dimdoors;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;

import net.minecraft.resource.ResourceType;
import net.minecraft.util.math.Vec3i;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.command.ModCommands;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.listener.AttackBlockCallbackListener;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.particle.ModParticleTypes;
import org.dimdev.dimdoors.pockets.SchematicHandler;
import org.dimdev.dimdoors.pockets.SchematicV2Handler;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualSingularPocket;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.feature.ModFeatures;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.NotNull;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

public class DimensionalDoorsInitializer implements ModInitializer {
    public static final Identifier MONOLITH_PARTICLE_PACKET = new Identifier("dimdoors", "monolith_particle_packet");
	public static final ConfigHolder<ModConfig> CONFIG_MANAGER = AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
	private static final Map<UUID, ServerPacketHandler> UUID_SERVER_PACKET_HANDLER_MAP = new HashMap<>();
	private static MinecraftServer server;

    @NotNull
    public static MinecraftServer getServer() {
        if (server != null) {
            return server;
        }
        throw new UnsupportedOperationException("Accessed server too early!");
    }

    public static ServerWorld getWorld(RegistryKey<World> key) {
        return getServer().getWorld(key);
    }

	public static ModConfig getConfig() {
		return CONFIG_MANAGER.get();
	}

	@Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register((minecraftServer) -> {
            server = minecraftServer;
        });

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

        Targets.registerDefaultTargets();
		VirtualTarget.VirtualTargetType.register();
		VirtualSingularPocket.VirtualSingularPocketType.register();
		Modifier.ModifierType.register();
		PocketGenerator.PocketGeneratorType.register();
		AbstractPocket.AbstractPocketType.register();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SchematicV2Handler.getInstance());
//        SchematicV2Handler.getInstance().load();
        SchematicHandler.INSTANCE.loadSchematics();

		registerListeners();

		//newPocketTest();
    }

    private void registerListeners() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			UUID_SERVER_PACKET_HANDLER_MAP.put(handler.player.getUuid(), new ServerPacketHandler(handler, server));
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			UUID_SERVER_PACKET_HANDLER_MAP.remove(handler.player.getUuid()).unregister();
		});

		AttackBlockCallback.EVENT.register(new AttackBlockCallbackListener());
	}

	/*
	void newPocketTest() {
		PocketDirectory directory = new PocketDirectory(ModDimensions.DUNGEON, 512);

		Pocket.PocketBuilder<?, ?> builder = Pocket.builder().expand(new Vec3i(1, 1, 1));


		System.out.println(0 + ", " + directory.newPocket(builder).getId());
		System.out.println(1 + ", " + directory.newPocket(builder).getId());
		System.out.println(2 + ", " + directory.newPocket(builder).getId());
		System.out.println(3 + ", " + directory.newPocket(builder).getId());
		System.out.println(4 + ", " + directory.newPocket(builder).getId());
		System.out.println(5 + ", " + directory.newPocket(builder).getId());
		System.out.println(6 + ", " + directory.newPocket(builder).getId());



		builder = Pocket.builder().expand(new Vec3i(directory.getGridSize() + 1, directory.getGridSize() + 1, directory.getGridSize() + 1));
		System.out.println(9 + ", " + directory.newPocket(builder).getId());
		System.out.println(18 + ", " + directory.newPocket(builder).getId());

		builder = Pocket.builder().expand(new Vec3i(3 * directory.getGridSize() + 1, 3 * directory.getGridSize() + 1, 3 * directory.getGridSize() + 1));
		System.out.println(81 + ", " + directory.newPocket(builder).getId());



		builder = Pocket.builder().expand(new Vec3i(directory.getGridSize() + 1, directory.getGridSize() + 1, directory.getGridSize() + 1));
		System.out.println(27 + ", " + directory.newPocket(builder).getId());

		builder = Pocket.builder().expand(new Vec3i(1, 1, 1));
		System.out.println(7 + ", " + directory.newPocket(builder).getId());
		System.out.println(8 + ", " + directory.newPocket(builder).getId());
		System.out.println(36 + ", " + directory.newPocket(builder).getId());
	}
	*/
}
