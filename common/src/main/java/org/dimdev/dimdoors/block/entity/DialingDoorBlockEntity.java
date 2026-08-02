package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.rift.registry.DialingAddress;

public class DialingDoorBlockEntity extends EntranceRiftBlockEntity<DialingDoorBlockEntity> {
    private static final CodecRecord<DialingDoorBlockEntity, DialingAddress> DIALING_ADDRESS_BUILDER = new CodecRecord<>("address", DialingAddress.CODEC, DialingAddress.DEFAULT, a -> a.address);

    private DialingAddress address = DialingAddress.DEFAULT;

    protected DialingDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DIALING_DOOR, pos, state);
    }

    @Override
    public void serialize(Serialize<Tag, DialingDoorBlockEntity> serialize) {
        super.serialize(serialize);
        serialize.put(DIALING_ADDRESS_BUILDER);
    }

    @Override
    public void deserialize(Deserialize<Tag> nbt) {
        super.deserialize(nbt);
        this.address = nbt.get(DIALING_ADDRESS_BUILDER);
    }

    public void turnDial(DialingAddress.DialType type) {
        setAddress(address.turnDial(type));
    }

    public DialingAddress getAddress() {
        return address;
    }

    public void setAddress(DialingAddress address) {
        this.address = address;
        sync();
    }
}
