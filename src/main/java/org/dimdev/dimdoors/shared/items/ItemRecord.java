package org.dimdev.dimdoors.shared.items;

import net.minecraft.util.SoundEvent;
import org.dimdev.dimdoors.DimDoors;

public class ItemRecord extends net.minecraft.item.ItemRecord {
    protected ItemRecord(String recordName, SoundEvent soundIn) {
        super(recordName, soundIn);
        setRegistryName(DimDoors.MODID, "record_" + recordName);
        setUnlocalizedName("record");
    }
}
