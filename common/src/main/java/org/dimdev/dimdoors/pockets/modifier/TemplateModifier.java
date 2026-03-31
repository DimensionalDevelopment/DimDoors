package org.dimdev.dimdoors.pockets.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.rift.targets.TemplateTarget;

import java.util.List;

import static org.dimdev.dimdoors.pockets.modifier.RiftDataModifier.toByteArray;

public record TemplateModifier(ResourceKey<VirtualPocket> templateId, List<Integer> ids) implements Modifier {
    public static final MapCodec<TemplateModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(ModRegistries.VIRTUAL_POCKET).fieldOf("template_id").forGetter(TemplateModifier::templateId),
            Codec.INT_STREAM.xmap(a -> a.boxed().toList(), a -> a.stream().mapToInt(Integer::intValue)).fieldOf("ids").forGetter(TemplateModifier::ids)
    ).apply(instance, TemplateModifier::new));

    public static final String KEY = "template";

    @Override
    public CompoundTag toNbtInternal(CompoundTag nbt, HolderLookup.Provider provider, boolean allowReference) {
        super.toNbtInternal(nbt, provider, allowReference);

        nbt.putString("templateId", templateId.toString());
        nbt.putByteArray("ids", toByteArray(ids.stream().mapToInt(Integer::intValue).toArray()));
        return nbt;
    }


    @Override
    public ModifierType<? extends Modifier> getType() {
        return ModifierType.TEMPLATE_MODIFIER_TYPE.get();
    }

    @Override
    public void apply(PocketGenerationContext parameters, RiftManager manager) {
        var template = new TemplateTarget(templateId);

        manager.foreachConsume((id, rift) -> {
            if (ids.contains(id)) {
                rift.setDestination(template.copy());
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

    }
}