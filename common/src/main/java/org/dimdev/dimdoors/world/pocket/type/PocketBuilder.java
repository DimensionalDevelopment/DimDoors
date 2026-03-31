package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.List;
import java.util.UUID;

public record PocketBuilder(List<PocketAddon> addons) {
    public static Codec<PocketBuilder> CODEC = RecordCodecBuilder.create(instance -> instance.group(PocketAddon.LIST_CODEC.fieldOf("addons").forGetter(PocketBuilder::addons)).apply(instance, PocketBuilder::new));
}
