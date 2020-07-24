package org.dimdev.dimdoors.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class RiftEntity extends Entity {
    public RiftEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public RiftEntity(World world) {
        super(null, world); //TODO: register entity type
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {

    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }
}
