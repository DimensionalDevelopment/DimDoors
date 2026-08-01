package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;

public class DialingDoorBlockEntity extends EntranceRiftBlockEntity {
    private byte[] combo = new byte[] { 0, 0, 0 };

    protected DialingDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DIALING_DOOR, pos, state);
    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        super.serialize(nbt);
        nbt.putByteArray("combo", combo);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        this.combo = nbt.getByteArray("combo");
        if(combo.length != 3) {
            DimensionalDoors.LOGGER.error("Malformed combo in dailing door at {}. Defaulting to (0, 0, 0)", getBlockPos());

            combo = new byte[] { 0, 0, 0 };
        }
    }

    public byte[] getCombo() {
        return combo;
    }

    public void setCombo(byte[] combo) {
        if(combo == null || combo.length != 3) return;

        this.combo = combo;
    }
}
