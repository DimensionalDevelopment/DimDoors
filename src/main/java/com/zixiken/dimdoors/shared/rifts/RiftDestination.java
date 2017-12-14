package com.zixiken.dimdoors.shared.rifts;

import com.zixiken.dimdoors.shared.util.INBTStorable;
import lombok.*;
import lombok.experimental.Wither;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.LinkedList;
import java.util.List;

@Getter @ToString @EqualsAndHashCode @AllArgsConstructor(access = AccessLevel.PRIVATE)
public /*abstract*/ class RiftDestination implements INBTStorable { // TODO: fix lombok and make this abstract
    @Getter private EnumType type;
    @Wither @Getter private RiftDestination oldDestination;

    public enum EnumType {
        RELATIVE, LOCAL, GLOBAL, NEW_PUBLIC, PRIVATE, LIMBO, RANDOM_RIFT_LINK, POCKET_ENTRANCE, POCKET_EXIT, PRIVATE_POCKET_EXIT, ESCAPE;
    }

    private RiftDestination() {}


    public static RiftDestination readDestinationNBT(NBTTagCompound nbt) { // TODO: store old RANDOM_RIFT_LINK
        RiftDestination destination = null;
        EnumType type = EnumType.valueOf(nbt.getString("type"));
        switch (type) {
            case RELATIVE:
                destination = new RelativeDestination();
                break;
            case LOCAL:
                destination = new LocalDestination();
                break;
            case NEW_PUBLIC:
                destination = new NewPublicDestination();
                break;
            case PRIVATE:
                destination = new PrivateDestination();
                break;
            case LIMBO:
                destination = new LimboDestination();
                break;
            case RANDOM_RIFT_LINK:
                destination = new RandomRiftLinkDestination();
                break;
            case POCKET_ENTRANCE:
                destination = new PocketEntranceDestination();
                break;
            case POCKET_EXIT:
                destination = new PocketExitDestination();
                break;
            case PRIVATE_POCKET_EXIT:
                destination = new PrivatePocketExitDestination();
                break;
            case ESCAPE:
                destination = new EscapeDestination();
                break;
        }
        destination.type = type;
        destination.readFromNBT(nbt);
        return destination;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("oldDestination")) oldDestination = readDestinationNBT(nbt.getCompoundTag("oldDestination"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (oldDestination != null) nbt.setTag("oldDestination", oldDestination.writeToNBT(new NBTTagCompound()));
        return nbt;
    }


    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class RelativeDestination extends RiftDestination { // TODO: use Vec3i
        private int xOffset;
        private int yOffset;
        private int zOffset;

        private RelativeDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            xOffset = nbt.getInteger("xOffset");
            yOffset = nbt.getInteger("yOffset");
            zOffset = nbt.getInteger("zOffset");
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setString("type", EnumType.RELATIVE.name());
            nbt.setInteger("xOffset", xOffset);
            nbt.setInteger("yOffset", yOffset);
            nbt.setInteger("yOffset", zOffset);
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class LocalDestination extends RiftDestination { // TODO: use BlockPos
        private int x;
        private int y;
        private int z;

        private LocalDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            x = nbt.getInteger("x");
            y = nbt.getInteger("y");
            z = nbt.getInteger("z");
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setString("type", EnumType.LOCAL.name());
            nbt.setInteger("x", x);
            nbt.setInteger("y", y);
            nbt.setInteger("y", z);
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class GlobalDestination extends RiftDestination { // TODO: use Location
        private int dim;
        private int x;
        private int y;
        private int z;

        private GlobalDestination() {};

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            dim = nbt.getInteger("dim");
            x = nbt.getInteger("x");
            y = nbt.getInteger("y");
            z = nbt.getInteger("z");
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setString("type", EnumType.GLOBAL.name());
            nbt.setInteger("dim", dim);
            nbt.setInteger("x", x);
            nbt.setInteger("y", y);
            nbt.setInteger("y", z);
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class NewPublicDestination extends RiftDestination {

        //private NewPublicDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setString("type", EnumType.NEW_PUBLIC.name());
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class PrivateDestination extends RiftDestination {

        //private PrivateDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setString("type", EnumType.PRIVATE.name());
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class LimboDestination extends RiftDestination {

        //private LimboDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setString("type", EnumType.PRIVATE.name());
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class RandomRiftLinkDestination extends RiftDestination { // TODO
        private float newDungeonRiftProbability;
        private float depthPenalization; // TODO: these make the equation assymetric
        private float distancePenalization;
        private float closenessPenalization;

        private boolean dungeonRiftsOnly;
        private boolean overworldRifts;
        private boolean unstable;
        private float entranceLinkWeight;
        private float floatingRiftWeight;
        // TODO: add a "safe" option to link only to a rift destination that has a non-zero weight

        private RandomRiftLinkDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            newDungeonRiftProbability = nbt.getFloat("newDungeonRiftProbability");
            depthPenalization = nbt.getFloat("depthPenalization");
            distancePenalization = nbt.getFloat("distancePenalization");
            closenessPenalization = nbt.getFloat("closenessPenalization");
            dungeonRiftsOnly = nbt.getBoolean("dungeonRiftsOnly");
            overworldRifts = nbt.getBoolean("overworldRifts");
            unstable = nbt.getBoolean("unstable");
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setString("type", EnumType.RANDOM_RIFT_LINK.name());
            nbt.setFloat("newDungeonRiftProbability", newDungeonRiftProbability);
            nbt.setFloat("depthPenalization", depthPenalization);
            nbt.setFloat("distancePenalization", distancePenalization);
            nbt.setFloat("closenessPenalization", closenessPenalization);
            nbt.setBoolean("dungeonRiftsOnly", dungeonRiftsOnly);
            nbt.setBoolean("overworldRifts", overworldRifts);
            nbt.setBoolean("unstable", unstable);
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class PocketEntranceDestination extends RiftDestination {
        private float weight;
        private List<WeightedRiftDestination> ifDestinations = new LinkedList<>();
        private List<WeightedRiftDestination> otherwiseDestinations = new LinkedList<>();

        private PocketEntranceDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            weight = nbt.getFloat("weight");

            NBTTagList ifDestinationsNBT = (NBTTagList) nbt.getTag("ifDestinations");
            for (NBTBase ifDestinationNBT : ifDestinationsNBT) {
                WeightedRiftDestination ifDestination = new WeightedRiftDestination();
                ifDestination.readFromNBT((NBTTagCompound) ifDestinationNBT);
                otherwiseDestinations.add(ifDestination);
            }

            NBTTagList otherwiseDestinationsNBT = (NBTTagList) nbt.getTag("otherwiseDestinations");
            for (NBTBase otherwiseDestinationNBT : otherwiseDestinationsNBT) {
                WeightedRiftDestination otherwiseDestination = new WeightedRiftDestination();
                otherwiseDestination.readFromNBT((NBTTagCompound) otherwiseDestinationNBT);
                otherwiseDestinations.add(otherwiseDestination);
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setString("type", EnumType.POCKET_ENTRANCE.name());
            nbt.setFloat("weight", weight);

            NBTTagList ifDestinationsNBT = new NBTTagList();
            for (WeightedRiftDestination ifDestination : ifDestinations) {
                ifDestinationsNBT.appendTag(ifDestination.writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("ifDestinations", ifDestinationsNBT);

            NBTTagList otherwiseDestinationsNBT = new NBTTagList();
            for (WeightedRiftDestination otherwiseDestination : otherwiseDestinations) {
                otherwiseDestinationsNBT.appendTag(otherwiseDestination.writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("otherwiseDestinations", otherwiseDestinationsNBT);

            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class PocketExitDestination extends RiftDestination {

        //private PocketExitDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setString("type", EnumType.POCKET_EXIT.name());
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class PrivatePocketExitDestination extends RiftDestination { // TODO: merge into PocketExit or Escape?

        //private PrivatePocketExitDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setString("type", EnumType.PRIVATE_POCKET_EXIT.name());
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class EscapeDestination extends RiftDestination {

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setString("type", EnumType.ESCAPE.name());
            return nbt;
        }
    }
}
