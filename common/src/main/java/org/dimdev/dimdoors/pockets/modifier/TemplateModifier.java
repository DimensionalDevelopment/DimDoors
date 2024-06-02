package org.dimdev.dimdoors.pockets.modifier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.TemplateTarget;
import org.dimdev.dimdoors.forge.world.pocket.type.Pocket;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.dimdev.dimdoors.pockets.modifier.RiftDataModifier.stream;
import static org.dimdev.dimdoors.pockets.modifier.RiftDataModifier.toByteArray;

public class TemplateModifier extends AbstractModifier {
    public static final String KEY = "template";

    private ResourceLocation templateId;

    private List<Integer> ids;

    @Override
    public Modifier fromNbt(CompoundTag nbt, ResourceManager manager) {
        templateId = ResourceLocation.tryParse(nbt.getString("templateId"));
        ids = stream(nbt.getByteArray("ids")).boxed().collect(Collectors.toList());
        return this;
    }

    @Override
    public CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
        super.toNbtInternal(nbt, allowReference);

        nbt.putString("templateId", templateId.toString());
        nbt.putByteArray("ids", toByteArray(ids.stream().mapToInt(Integer::intValue).toArray()));
        return nbt;
    }


    @Override
    public ModifierType<? extends Modifier> getType() {
        return ModifierType.TEMPLATE_MODIFIER_TYPE.get();
    }

    @Override
    public String getKey() {
        return KEY;
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
