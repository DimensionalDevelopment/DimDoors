package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.rift.registry.DialingAddress;

public class DialingTargetImpl extends VirtualTarget<DialingTargetImpl> implements DialingTarget {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final MapCodec<DialingTargetImpl> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DialingAddress.MAP_CODEC.forGetter(DialingTargetImpl::getAddress)
    ).apply(instance, DialingTargetImpl::new));

    private DialingAddress address;

    public DialingTargetImpl(DialingAddress address) {
        this.address = address;
    }

    @Override
    public DialingAddress getAddress() {
        return address;
    }

    @Override
    public VirtualTarget.VirtualTargetType<DialingTargetImpl> getType() {
        return VirtualTarget.VirtualTargetType.DIALING;
    }

    @Override
    public DialingTargetImpl copy() {
        return new DialingTargetImpl(address);
    }
}
