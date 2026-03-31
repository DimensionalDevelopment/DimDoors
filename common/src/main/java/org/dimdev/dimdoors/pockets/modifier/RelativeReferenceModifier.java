package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.LocalReference;
import org.dimdev.dimdoors.rift.targets.RiftReference;

import java.util.Optional;

public record RelativeReferenceModifier(int point_a, int point_b, ConnectionType connection) implements Modifier {
    public static final MapCodec<RelativeReferenceModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("point_a").forGetter(RelativeReferenceModifier::point_a),
            Codec.INT.fieldOf("point_b").forGetter(RelativeReferenceModifier::point_b),
            ConnectionType.CODEC.optionalFieldOf("connection", ConnectionType.BOTH).forGetter(RelativeReferenceModifier::connection)
    ).apply(instance, RelativeReferenceModifier::new));

    public static final String KEY = "relative";


    @Override
    public ModifierType<? extends Modifier> getType() {
        return ModifierType.RELATIVE_REFERENCE_MODIFIER_TYPE.get();
    }

    @Override
    public void apply(PocketGenerationContext parameters, RiftManager manager) {
        Optional<Location> riftA = manager.get(point_a).map(rift -> new Location((ServerLevel) rift.getLevel(), rift.getBlockPos()));
        Optional<Location> riftB = manager.get(point_b).map(rift -> new Location((ServerLevel) rift.getLevel(), rift.getBlockPos()));

        if (riftA.isPresent() && riftB.isPresent()) {
            RiftReference link1 = LocalReference.tryMakeRelative(riftA.get(), riftB.get());
            RiftReference link2 = LocalReference.tryMakeRelative(riftB.get(), riftA.get());

            manager.consume(point_a, rift -> addLink(rift, link1));

            if (connection == ConnectionType.BOTH) manager.consume(point_b, rift -> addLink(rift, link2));
        }
    }

    @Override
    public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("point_a", point_a)
                .add("point_b", point_b)
                .add("connection", connection.getSerializedName())
                .toString();
    }

    private boolean addLink(RiftBlockEntity rift, RiftReference link) {
        rift.setDestination(link);
        return true;
    }

    public enum ConnectionType implements StringRepresentable {
        BOTH("both"),
        ONE_WAY("one_way");

        public static final Codec<ConnectionType> CODEC = StringRepresentable.fromEnum(ConnectionType::values);

        private String id;

        ConnectionType(String id) {
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return id;
        }

        public static ConnectionType fromString(String name) {
            return "one_way".equalsIgnoreCase(name) ? ONE_WAY : BOTH;
        }

    }
}