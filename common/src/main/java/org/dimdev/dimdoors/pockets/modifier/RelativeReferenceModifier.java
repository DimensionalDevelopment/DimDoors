package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StringRepresentable;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

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
        return ModifierType.RELATIVE_REFERENCE_MODIFIER_TYPE;
    }

    @Override
    public void apply(PocketGenerationContext parameters, RiftManager manager) {
        Optional<Location> riftA = manager.get(point_a).map(rift -> Location.ofWorld((ServerLevel) rift.getLevel(), rift.getBlockPos()));
        Optional<Location> riftB = manager.get(point_b).map(rift -> Location.ofWorld((ServerLevel) rift.getLevel(), rift.getBlockPos()));

        if (riftA.isPresent() && riftB.isPresent()) {
            VirtualTarget link1 = riftB.get().asTarget();
            VirtualTarget link2 = riftA.get().asTarget();

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

    private boolean addLink(RiftBlockEntity rift, VirtualTarget<?> link) {
        rift.setDestination(link);
        return true;
    }

    public enum ConnectionType implements StringRepresentable {
        BOTH("both"),
        ONE_WAY("one_way");

        public static final Codec<ConnectionType> CODEC = StringRepresentable.fromValues(ConnectionType::values);

        private String id;

        ConnectionType(String id) {
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return id;
        }
    }
}