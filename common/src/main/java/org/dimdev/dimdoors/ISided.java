package org.dimdev.dimdoors;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.function.Consumer;

public interface ISided extends IRegister, ICreativeTabHandler {
    void onServerStarted(Consumer<MinecraftServer> consumer);
}
