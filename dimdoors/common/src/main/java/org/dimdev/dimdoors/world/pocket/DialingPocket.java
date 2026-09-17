package org.dimdev.dimdoors.world.pocket;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.dimdev.dimdoors.rift.registry.DialingAddress;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.DyeableAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.List;
import java.util.Objects;

public class DialingPocket extends Pocket<DialingPocket, DialingPocket.Builder> implements DyeableAddon.DyeablePocket {
    public static final MapCodec<DialingPocket> CODEC = RecordCodecBuilder.mapCodec(instance -> commonPocketFields(instance).and(DialingAddress.MAP_CODEC.forGetter(DialingPocket::getAddress)).apply(instance, DialingPocket::new));

    private DialingAddress address;

    public DialingPocket(int id, ResourceKey<Level> world, int range, BoundingBox box, VirtualLocation virtualLocation, List<PocketAddon> addons, DialingAddress address) {
        super(id, world, range, box, virtualLocation, addons);
        setAddress(address);
    }

    public DialingAddress getAddress() {
        return Objects.requireNonNull(address, "DialingPocket address has not been assigned.");
    }

    public void setAddress(DialingAddress address) {
        this.address = Objects.requireNonNull(address, "address");
    }

    public DialingPocket() {}

    public static Builder builderDialingPocket() {
        return new Builder();
    }

    public static class Builder extends PocketBuilder<DialingPocket, Builder> {
        public static final MapCodec<Builder> CODEC = RecordCodecBuilder.mapCodec(instance -> PocketBuilder.commonFields(instance).apply(instance, Builder::new));

        private DialingAddress address;

        protected Builder(List<PocketAddon.PocketBuilderAddon<?, ?>> addons) {
            super(addons);
        }

        protected Builder() {
            super();
        }

        @Override
        public Builder instance() {
            return builderDialingPocket();
        }

        public Builder address(DialingAddress address) {
            this.address = Objects.requireNonNull(address, "address");
            return this;
        }

        @Override
        public DialingPocket build() {
            var pocket = super.build();
            if (address != null) {
                pocket.setAddress(address);
            }
            return pocket;
        }

        @Override
        public AbstractPocketType<DialingPocket, Builder> type() {
            return AbstractPocketType.DIALING;
        }
    }

    @Override
    public AbstractPocketType<DialingPocket, Builder> getType() {
        return AbstractPocketType.DIALING;
    }
}
