package org.dimdev.dimdoors.pockets.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.TemplateTarget;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record TemplateModifier(ResourceLocation templateId, Set<Integer> ids) implements Modifier {
    public static final String KEY = "template";

    public static final MapCodec<TemplateModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("templateId").forGetter(TemplateModifier::templateId),
            Codec.INT_STREAM.xmap(IntStream::boxed, ints -> ints.mapToInt(Integer::intValue)).xmap(integerStream -> integerStream.collect(Collectors.toSet()), Collection::stream).fieldOf("ids").forGetter(a -> a.ids())
    ).apply(instance, TemplateModifier::new));

    @Override
    public Modifier.ModifierType<? extends Modifier> getType() {
        return Modifier.ModifierType.TEMPLATE_MODIFIER_TYPE.get();
    }

    @Override
    public void apply(PocketGenerationContext parameters, RiftManager manager) {
        var template = new TemplateTarget(templateId);

        manager.foreachConsume((id, rift) -> {
            if(ids.contains(id)) {
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
