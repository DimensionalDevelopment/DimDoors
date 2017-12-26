package com.zixiken.dimdoors.shared.rifts;

import com.zixiken.dimdoors.shared.pockets.Pocket;
import com.zixiken.dimdoors.shared.pockets.PocketGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

@Getter @AllArgsConstructor @Builder(toBuilder = true)
public class NewPublicDestination extends RiftDestination { // TODO: more config options such as non-default size, etc.
    //public NewPublicDestination() {}

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public boolean teleport(TileEntityRift rift, Entity entity) {
        Pocket pocket = PocketGenerator.generatePublicPocket(rift.virtualLocation != null ? rift.virtualLocation.toBuilder().depth(-1).build() : null); // TODO: random transform
        pocket.setup();
        pocket.linkPocketTo(new GlobalDestination(rift.getLocation()));
        rift.makeDestinationPermanent(weightedDestination, pocket.getEntrance());
        ((TileEntityRift) pocket.getEntrance().getTileEntity()).teleportTo(entity);
        return true;
    }
}
