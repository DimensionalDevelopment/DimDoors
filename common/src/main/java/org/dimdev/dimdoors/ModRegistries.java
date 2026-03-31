package org.dimdev.dimdoors;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

public class ModRegistries {
    public static final ResourceKey<Registry<VirtualPocket>> POCKET_GROUP = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("pockets/groups"));
    public static final ResourceKey<Registry<VirtualPocket>> VIRTUAL_POCKET = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("pockets/virtual"));
    public static final ResourceKey<Registry<PocketGenerator>> POCKET_GENERATOR = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("pockets/generators"));
    public static final ResourceKey<Registry<RiftData>> RIFT_DATA = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("pockets/rift_data"));
    public static final ResourceKey<Registry<Modifier>> MODIFIERS = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("pockets/modifiers"));
}
