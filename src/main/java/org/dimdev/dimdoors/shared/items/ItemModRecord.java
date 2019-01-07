package org.dimdev.dimdoors.shared.items;

import net.minecraft.item.ItemRecord;
import net.minecraft.util.SoundEvent;
import org.dimdev.dimdoors.DimDoors;

public class ItemModRecord extends ItemRecord {
    protected ItemModRecord(String recordName, SoundEvent soundIn) {
        super(recordName, soundIn);
        setRegistryName(DimDoors.MODID, "record_" + recordName);
    }
}
