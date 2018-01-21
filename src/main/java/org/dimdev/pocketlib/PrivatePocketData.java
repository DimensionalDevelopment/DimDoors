package org.dimdev.pocketlib;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.WorldUtils;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.dimdoors.DimDoors;

import java.util.UUID;

@NBTSerializable public class PrivatePocketData extends WorldSavedData {

    @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode @ToString
    @NBTSerializable protected static class PocketInfo implements INBTStorable {
        @Saved protected int dim;
        @Saved protected int id;

        @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
        @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }
    }

    private static final String DATA_NAME = DimDoors.MODID + "_private_pockets";
    @Saved protected BiMap<String, PocketInfo> privatePocketMap = HashBiMap.create(); // Player UUID -> Pocket Info TODO: fix AnnotatedNBT and use UUID rather than String

    public PrivatePocketData(String name) {
        super(name);
    }

    public PrivatePocketData() {
        super(DATA_NAME);
    }

    public static PrivatePocketData instance() {
        MapStorage storage = WorldUtils.getWorld(0).getMapStorage();
        PrivatePocketData instance = (PrivatePocketData) storage.getOrLoadData(PrivatePocketData.class, DATA_NAME);

        if (instance == null) {
            instance = new PrivatePocketData();
            storage.setData(DATA_NAME, instance);
        }

        return instance;
    }

    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }

    public Pocket getPrivatePocket(UUID playerUUID) {
        PocketInfo pocket = privatePocketMap.get(playerUUID.toString());
        if (pocket == null) return null;
        return PocketRegistry.instance(pocket.dim).getPocket(pocket.id);
    }

    public void setPrivatePocketID(UUID playerUUID, Pocket pocket) {
        privatePocketMap.put(playerUUID.toString(), new PocketInfo(pocket.getDim(), pocket.getId()));
        markDirty();
    }

    public UUID getPrivatePocketOwner(Pocket pocket) {
        return UUID.fromString(privatePocketMap.inverse().get(new PocketInfo(pocket.getDim(), pocket.getId())));
    }
}
