package org.dimdev.dimcore.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimcore.util.DataValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IRegister {
	<T> void registerCallback(Registry<T> key, TriConsumer<Registry<T>, ResourceLocation, T> consumer);

	<T, V extends T> V register(ResourceKey<Registry<T>> key, String id, V obj);
	<T, V extends T> V register(ResourceKey<Registry<T>> key, ResourceLocation id, V obj);
	<T, V extends T> Holder<T> registerHolder(ResourceKey<Registry<T>> key, String id, V obj);
	<T, V extends T> Holder<T> registerHolder(ResourceKey<Registry<T>> key, ResourceLocation id, V obj);


	default <T extends Item> T registerItem(String id, T obj) {
        return register(Registries.ITEM, id, obj);
    }

    default <T extends Block> T registerBlock(String id, T obj) {
        return register(Registries.BLOCK, id, obj);
    }

    default <T extends BlockEntityType<?>> T registerBlockEntityType(String id, T obj) {
        return register(Registries.BLOCK_ENTITY_TYPE, id, obj);
    }

	default <T extends MapCodec<? extends ChunkGenerator>> T registerChunkGenerator(String id, T obj) {
		return register(Registries.CHUNK_GENERATOR, id, obj);
	}

    default <T extends EntityType<?>> T registerEntityType(String id, T obj) {
        return register(Registries.ENTITY_TYPE, id, obj);
    }

    default <T extends Fluid> T registerFluid(String id, T obj) {
        return register(Registries.FLUID, id, obj);
    }

    default <T extends SoundEvent> T registerSoundEvent(String id, T obj) {
        return register(Registries.SOUND_EVENT, id, obj);
    }

    default <T extends MenuType<?>> T registerMenu(String id, T obj) {
        return register(Registries.MENU, id, obj);
    }

    default <T extends RecipeSerializer<?>> T registerRecipeSerializer(String id, T obj) {
        return register(Registries.RECIPE_SERIALIZER, id, obj);
    }

    default <T extends RecipeType<?>> T registerRecipeType(String id, T obj) {
        return register(Registries.RECIPE_TYPE, id, obj);
    }


    default CreativeModeTab registerCreativeModeTab(String id, Function<CreativeModeTab.Builder, CreativeModeTab.Builder> consumer) {
        return register(Registries.CREATIVE_MODE_TAB, id, createTab(consumer));
    }

    CreativeModeTab createTab(Function<CreativeModeTab.Builder, CreativeModeTab.Builder> consumer);

    default <T extends ParticleType<?>> T registerParticleType(String id, T obj) {
        return register(Registries.PARTICLE_TYPE, id, obj);
    }

    default <T extends Potion> T registerPotion(String id, T obj) {
        return register(Registries.POTION, id, obj);
    }

    default <T extends Enchantment> T registerEnchantment(String id, T obj) {
        return register(Registries.ENCHANTMENT, id, obj);
    }

    default <T extends ArmorMaterial> T registerArmorMaterial(String id, T obj) {
        return register(Registries.ARMOR_MATERIAL, id, obj);
    }

    default <T extends DamageType> T registerDamageType(String id, T obj) {
        return register(Registries.DAMAGE_TYPE, id, obj);
    }

    default <T extends DataComponentType<?>> T registerDataComponentType(String id, T obj) {
        return register(Registries.DATA_COMPONENT_TYPE, id, obj);
    }

    default ResourceLocation registerCustomStat(String id, ResourceLocation obj) {
        return register(Registries.CUSTOM_STAT, id, obj);
    }

    default <T extends LootTable> T registerLootTable(String id, T obj) {
        return register(Registries.LOOT_TABLE, id, obj);
    }

    default <T extends CriterionTrigger<?>> T registerTriggerType(String id, T obj) {
        return register(Registries.TRIGGER_TYPE, id, obj);
    }

    default <T extends WorldCarver<?>> T registerCarver(String id, T obj) {
        return register(Registries.CARVER, id, obj);
    }

    default <T extends StructureProcessor> StructureProcessorType<T> registerStructureProcessor(String id, MapCodec<T> codec) {
        return register(Registries.STRUCTURE_PROCESSOR, id, new StructureProcessorType<>() {
            @Override
            public @NotNull MapCodec<T> codec() {
                return codec;
            }
        });
    }

    void registerRunnable(ResourceKey<? extends Registry<?>> key, Runnable runnable);

    <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key, ResourceLocation defaultId, boolean sync);

	default <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key, ResourceLocation defaultId) {
		return createRegistry(key, defaultId, false);
	}

	default <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key, boolean sync) {
		return createRegistry(key, null, sync);
	}

	default <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
		return createRegistry(key, null, false);
	}

    <T> DataValue<T> registerDataValue(String fray, Supplier<T> defaultValue, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec);

    void registerRunDataValue(Runnable runnable);

	void modifyCreativeTab(ResourceKey<CreativeModeTab> tab, Consumer<CreativeTabEntries> consumer);

	interface CreativeTabEntries extends CreativeModeTab.Output {
		void addAfter(ItemStack after, Collection<ItemStack> stacks, CreativeModeTab.TabVisibility visibility);

		default void addAfter(ItemStack after, Collection<ItemStack> stacks) {
			addAfter(after, stacks, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		}

		default void addAfter(ItemStack after, ItemStack... stacks) {
			addAfter(after, List.of(stacks));
		}

		default void addAfter(ItemLike after, Collection<ItemStack> stacks, CreativeModeTab.TabVisibility visibility) {
			addAfter(stack(after), stacks, visibility);
		}

		default void addAfter(ItemLike after, Collection<ItemStack> stacks) {
			addAfter(stack(after), stacks);
		}

		default void addAfter(ItemLike after, ItemStack... stacks) {
			addAfter(stack(after), List.of(stacks));
		}

		default void addAfter(ItemLike after, ItemLike... items) {
			addAfter(stack(after), stacks(items));
		}

		void addBefore(ItemStack before, Collection<ItemStack> stacks, CreativeModeTab.TabVisibility visibility);

		default void addBefore(ItemStack before, Collection<ItemStack> stacks) {
			addBefore(before, stacks, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		}

		default void addBefore(ItemStack before, ItemStack... stacks) {
			addBefore(before, List.of(stacks));
		}

		default void addBefore(ItemLike before, Collection<ItemStack> stacks, CreativeModeTab.TabVisibility visibility) {
			addBefore(stack(before), stacks, visibility);
		}

		default void addBefore(ItemLike before, Collection<ItemStack> stacks) {
			addBefore(stack(before), stacks);
		}

		default void addBefore(ItemLike before, ItemStack... stacks) {
			addBefore(stack(before), List.of(stacks));
		}

		default void addBefore(ItemLike before, ItemLike... items) {
			addBefore(stack(before), stacks(items));
		}

		private static ItemStack stack(ItemLike item) {
			return new ItemStack(item);
		}

		private static List<ItemStack> stacks(ItemLike... items) {
			List<ItemStack> stacks = new ArrayList<>(items.length);

			for (ItemLike item : items) {
				stacks.add(stack(item));
			}

			return stacks;
		}
	}
}
