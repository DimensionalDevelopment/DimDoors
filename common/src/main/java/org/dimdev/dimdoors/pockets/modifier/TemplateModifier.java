package org.dimdev.dimdoors.pockets.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.rift.targets.TemplateTarget;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record TemplateModifier(Holder<VirtualPocket> templateId, List<Integer> ids) implements Modifier {
    public static MapCodec<TemplateModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            VirtualPocket.HOLDER_CODEC.fieldOf("templateId").forGetter(TemplateModifier::templateId),
            Codec.INT_STREAM.xmap(IntStream::boxed, integerStream -> integerStream.mapToInt(Integer::intValue))
                    .xmap(Stream::toList, Collection::stream).fieldOf("ids")
                    .forGetter(TemplateModifier::ids)
    ).apply(instance, TemplateModifier::new));

    public static final String KEY = "template";



    @Override
    public ModifierType<? extends Modifier> getType() {
    return ModifierType.TEMPLATE_MODIFIER_TYPE;
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
