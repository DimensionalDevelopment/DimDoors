package org.dimdev.dimdoors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
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
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimdoors.world.fray.DataValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public interface IRegister {
    <T, V extends T> V register(ResourceKey<Registry<T>> key, ResourceLocation id, V obj);

    <T> void registerCallback(Registry<T> key, TriConsumer<Registry<T>, ResourceLocation, T> consumer);

    default <T, V extends T> V register(ResourceKey<Registry<T>> key, String id, V obj) {
        return register(key, DimensionalDoors.id(id), obj);
    }

    default <T extends Item> T registerItem(String id, T obj) {
        return register(Registries.ITEM, id, obj);
    }

    default <T extends Block> T registerBlock(String id, T obj) {
        return register(Registries.BLOCK, id, obj);
    }

    default <T extends BlockEntityType<?>> T registerBlockEntityType(String id, T obj) {
        return register(Registries.BLOCK_ENTITY_TYPE, id, obj);
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

    <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key);

    <T> DataValue<T> registerDataValue(String fray, Supplier<T> defaultValue, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec);

    void registerRunDataValue(Runnable runnable);
}
