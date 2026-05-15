package org.dimdev.dimdoors.pockets.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class RiftDataModifier implements Modifier {
    public static final MapCodec<RiftDataModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RiftData.HOLDER_CODEC.optionalFieldOf("rift_data").forGetter(a -> Optional.ofNullable(a.doorData)),
                    Codec.INT_STREAM.xmap(a -> a.boxed().toList(), integers -> integers.stream().mapToInt(Integer::intValue)).fieldOf("id").forGetter(a -> a.ids))
            .apply(instance, RiftDataModifier::new));


    public static final String KEY = "rift_data";

    private final Holder<RiftData> doorData;
    private final List<Integer> ids;

    public RiftDataModifier(Optional<Holder<RiftData>> doorData, List<Integer> ids) {
        this.doorData = doorData.orElse(null);
        this.ids = ids;
    }

    @Override
    public ModifierType<? extends Modifier> getType() {
        return ModifierType.RIFT_DATA_MODIFIER_TYPE;
    }

    @Override
    public void apply(PocketGenerationContext parameters, RiftManager manager) {
        Consumer<RiftBlockEntity> riftBlockEntityConsumer;

        if (doorData == null) {
            riftBlockEntityConsumer = rift -> rift.setDestination(VirtualTarget.NoneTarget.INSTANCE);
        } else {
            riftBlockEntityConsumer = rift -> rift.setData(doorData.value());
        }

        manager.foreachConsume((id, rift) -> {
            if (ids.contains(id)) {
                riftBlockEntityConsumer.accept(rift);
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