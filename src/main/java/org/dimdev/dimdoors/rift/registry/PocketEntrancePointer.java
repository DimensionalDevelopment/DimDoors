package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

public class PocketEntrancePointer extends RegistryVertex { // TODO: PocketRiftPointer superclass?
    public static final Codec<PocketEntrancePointer> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                DynamicSerializableUuid.field_25122.fieldOf("id").forGetter(a -> a.id),
                World.CODEC.fieldOf("pocketDim").forGetter(a -> a.pocketDim),
                Codec.INT.fieldOf("pocketId").forGetter(a -> a.pocketId)
        ).apply(instance, (id, pocketDim, pocketId) -> {
            PocketEntrancePointer pointer = new PocketEntrancePointer(pocketDim, pocketId);
            pointer.id = id;
            return pointer;
        });
    });

    @Saved
    public RegistryKey<World> pocketDim;
    @Saved
    public int pocketId;

    public PocketEntrancePointer(RegistryKey<World> pocketDim, int pocketId) {
        this.pocketDim = pocketDim;
        this.pocketId = pocketId;
    }

    public PocketEntrancePointer() {
    }

    @Override
    public RegistryVertexType<? extends RegistryVertex> getType() {
        return RegistryVertexType.ENTRANCE;
    }

    public String toString() {
        return "PocketEntrancePointer(pocketDim=" + this.pocketDim + ", pocketId=" + this.pocketId + ")";
    }
}
