package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.jetbrains.annotations.NotNull;

public record PocketEntranceModifier(int id) implements Modifier {
    public static final String KEY = "pocket_entrance";

    public static final MapCodec<PocketEntranceModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.INT.fieldOf("id").forGetter(PocketEntranceModifier::id)).apply(instance, PocketEntranceModifier::new));

    @Override
    public @NotNull String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .toString();
    }

    @Override
    public ModifierType<? extends Modifier> getType() {
    return ModifierType.PUBLIC_MODIFIER_TYPE;
    }

    @Override
    public void apply(PocketGenerationContext parameters, RiftManager manager) {
    manager.consume(id, rift -> {
        rift.setDestination(PocketEntranceMarker.builder().ifDestination(PocketExitMarker.INSTANCE).weight(1.0f).build());
        return true;
    });
    }

    @Override
    public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

    }
}