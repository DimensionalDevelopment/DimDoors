package org.dimdev.dimdoors.registry;

import java.util.HashSet;

import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.DimensionalDoorsApi;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.block.door.data.condition.Condition;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.command.ModCommands;
import org.dimdev.dimdoors.criteria.ModCriteria;
import org.dimdev.dimdoors.enchantment.ModEnchants;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.DimensionalDoorItemRegistrar;
import org.dimdev.dimdoors.item.ModItems;
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
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.decay.DecayPredicate;
import org.dimdev.dimdoors.world.decay.DecayProcessor;
import org.dimdev.dimdoors.world.decay.LimboDecay;
import org.dimdev.dimdoors.world.feature.ModFeatures;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import static org.dimdev.dimdoors.Constants.CONFIG_MANAGER;
import static org.dimdev.dimdoors.DimensionalDoors.resource;

public class RegistryHandler {

	private static final HashSet<Item> CREATIVE_ITEM_LIST = new HashSet<>();

	public static void init(IEventBus bus) {
		CREATIVE_ITEM_LIST.clear();
		dimDoorsMod = FabricLoader.getInstance().getModContainer("dimdoors").orElseThrow(RuntimeException::new);

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

		ModRecipeTypes.init(bus);
		ModRecipeSerializers.init(bus);
		ModScreenHandlerTypes.init(bus);
		ModBlocks.init(bus);
		ModItems.init(bus);
		ModFeatures.init();
		ModBiomes.init();
		ModDimensions.init();
		ModEntityTypes.init(bus);
		ModStats.init();
		ModBlockEntityTypes.init(bus);
		ModCommands.init();
		ModFluids.init();
		ModSoundEvents.init(bus);
		ModParticleTypes.init(bus);
		ModCriteria.init();
		ModEnchants.init(bus);
		dimensionalDoorItemRegistrar = new DimensionalDoorItemRegistrar(BuiltInRegistries.ITEM);
		dimensionalDoorBlockRegistrar = new DimensionalDoorBlockRegistrar(BuiltInRegistries.BLOCK, dimensionalDoorItemRegistrar);

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(PocketLoader.getInstance());
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(LimboDecay.DecayLoader.getInstance());
		ResourceManagerHelper.registerBuiltinResourcePack(resource("default"), dimDoorsMod, CONFIG_MANAGER.get().getPocketsConfig().defaultPocketsResourcePackActivationType.asResourcePackActivationType());
		ResourceManagerHelper.registerBuiltinResourcePack(resource("classic"), dimDoorsMod, CONFIG_MANAGER.get().getPocketsConfig().classicPocketsResourcePackActivationType.asResourcePackActivationType());

		registerListeners();
		apiSubscribers.forEach(DimensionalDoorsApi::postInitialize);
	}

	public static void addCreativeItem(Item item) {
		CREATIVE_ITEM_LIST.add(item);
	}

	@SubscribeEvent
	public static void registerCreativeTabs(CreativeModeTabEvent.Register ev) {
		ev.registerCreativeModeTab(resource("itemGroup"), builder -> builder
				.icon(() -> new ItemStack(ModItems.RIFT_BLADE.get()))
				.title(Component.translatable("itemGroup"))
				.withSearchBar()
				.displayItems((features, output, permissions) -> {
					for(Item item : CREATIVE_ITEM_LIST) output.accept(item);
				}));
	}
}
