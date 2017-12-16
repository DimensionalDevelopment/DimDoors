package com.zixiken.dimdoors.shared.rifts;

import com.zixiken.dimdoors.shared.util.INBTStorable;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.util.NBTUtils;
import lombok.*;
import lombok.experimental.Wither;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter @ToString @EqualsAndHashCode @AllArgsConstructor(access = AccessLevel.PRIVATE)
public /*abstract*/ class RiftDestination implements INBTStorable { // TODO: fix lombok and make this abstract
    @Getter private EnumType type;
    @Getter protected RiftDestination oldDestination;

    public enum EnumType {
        RELATIVE, LOCAL, GLOBAL, NEW_PUBLIC, PRIVATE, LIMBO, AVAILABLE_LINK, POCKET_ENTRANCE, POCKET_EXIT, PRIVATE_POCKET_EXIT, ESCAPE;
    }

    private RiftDestination() {
        if (this instanceof RelativeDestination) {
            type = EnumType.RELATIVE;
        } else if (this instanceof LocalDestination) {
            type = EnumType.LOCAL;
        } else if (this instanceof GlobalDestination) {
            type = EnumType.GLOBAL;
        } else if (this instanceof NewPublicDestination) {
            type = EnumType.NEW_PUBLIC;
        } else if (this instanceof PrivateDestination) {
            type = EnumType.PRIVATE;
        } else if (this instanceof LimboDestination) {
            type = EnumType.LIMBO;
        } else if (this instanceof AvailableLinkDestination) {
            type = EnumType.AVAILABLE_LINK;
        } else if (this instanceof PocketEntranceDestination) {
            type = EnumType.POCKET_ENTRANCE;
        } else if (this instanceof PocketExitDestination) {
            type = EnumType.POCKET_EXIT;
        } else if (this instanceof PrivatePocketExitDestination) {
            type = EnumType.PRIVATE_POCKET_EXIT;
        } else if (this instanceof EscapeDestination) {
            type = EnumType.ESCAPE;
        }
    }

    public static RiftDestination readDestinationNBT(NBTTagCompound nbt) { // TODO: store old AVAILABLE_LINK
        RiftDestination destination = null;
        EnumType type = EnumType.valueOf(nbt.getString("type"));
        switch (type) {
            case RELATIVE:
                destination = new RelativeDestination();
                break;
            case LOCAL:
                destination = new LocalDestination();
                break;
            case GLOBAL:
                destination = new GlobalDestination();
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
            case AVAILABLE_LINK:
                destination = new AvailableLinkDestination();
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

    public RiftDestination withOldDestination(RiftDestination oldDestination) {
        RiftDestination dest = null;
        if (this instanceof RelativeDestination) { // TODO: use type switch
            dest = ((RelativeDestination) this).toBuilder().build();
            dest.oldDestination = oldDestination;
        } else if (this instanceof LocalDestination) {
            dest = ((LocalDestination) this).toBuilder().build();
            dest.oldDestination = oldDestination;
        } else if (this instanceof GlobalDestination) {
            dest = ((GlobalDestination) this).toBuilder().build();
            dest.oldDestination = oldDestination;
        } else if (this instanceof NewPublicDestination) {
            dest = ((NewPublicDestination) this).toBuilder().build();
            dest.oldDestination = oldDestination;
        } else if (this instanceof PrivateDestination) {
            dest = ((PrivateDestination) this).toBuilder().build();
            dest.oldDestination = oldDestination;
        } else if (this instanceof LimboDestination) {
            dest = ((LimboDestination) this).toBuilder().build();
            dest.oldDestination = oldDestination;
        } else if (this instanceof AvailableLinkDestination) {
            dest = ((AvailableLinkDestination) this).toBuilder().build();
            dest.oldDestination = oldDestination;
        } else if (this instanceof PocketEntranceDestination) {
            dest = ((PocketEntranceDestination) this).toBuilder().build();
            dest.oldDestination = oldDestination;
        } else if (this instanceof PocketExitDestination) {
            dest = ((PocketExitDestination) this).toBuilder().build();
            dest.oldDestination = oldDestination;
        } else if (this instanceof PrivatePocketExitDestination) {
            dest = ((PrivatePocketExitDestination) this).toBuilder().build();
            dest.oldDestination = oldDestination;
        } else if (this instanceof EscapeDestination) {
            dest = ((EscapeDestination) this).toBuilder().build();
            dest.oldDestination = oldDestination;
        }
        return dest;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("oldDestination")) oldDestination = readDestinationNBT(nbt.getCompoundTag("oldDestination"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (oldDestination != null) nbt.setTag("oldDestination", oldDestination.writeToNBT(new NBTTagCompound()));
        nbt.setString("type", type.name());
        return nbt;
    }


    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class RelativeDestination extends RiftDestination { // TODO: use Vec3i
        private Vec3i offset;

        private RelativeDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            offset = NBTUtils.readVec3i(nbt.getCompoundTag("offset"));
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setTag("offset", NBTUtils.writeVec3i(offset));
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class LocalDestination extends RiftDestination { // TODO: use BlockPos
        private BlockPos pos;

        private LocalDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            pos = new BlockPos(NBTUtils.readVec3i(nbt.getCompoundTag("pos")));
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setTag("pos", NBTUtils.writeVec3i(pos));
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class GlobalDestination extends RiftDestination { // TODO: location directly in nbt like minecraft?
        private Location loc;

        private GlobalDestination() {};

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            loc = Location.readFromNBT(nbt.getCompoundTag("loc"));
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setTag("loc", Location.writeToNBT(loc));
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class NewPublicDestination extends RiftDestination { // TODO: more config options such as non-default size, etc.
        //private NewPublicDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
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
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class AvailableLinkDestination extends RiftDestination { // TODO
        private float newDungeonRiftProbability;
        private float depthPenalization; // TODO: these make the equation assymetric
        private float distancePenalization;
        private float closenessPenalization;

        private boolean dungeonRiftsOnly;
        private boolean overworldRifts;
        private boolean unstable;
        private float entranceLinkWeight;
        private float floatingRiftWeight;

        private boolean noLinkBack;
        // private int maxLinks;

        @Builder.Default private UUID uuid = UUID.randomUUID();
        // TODO: add a "safe" option to link only to a rift destination that has a non-zero weight

        private AvailableLinkDestination() {}

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
            noLinkBack = nbt.getBoolean("noLinkBack");
            // maxLinks = nbt.getInteger("maxLinks");
            uuid = nbt.getUniqueId("uuid");
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt = super.writeToNBT(nbt);
            nbt.setFloat("newDungeonRiftProbability", newDungeonRiftProbability);
            nbt.setFloat("depthPenalization", depthPenalization);
            nbt.setFloat("distancePenalization", distancePenalization);
            nbt.setFloat("closenessPenalization", closenessPenalization);
            nbt.setBoolean("dungeonRiftsOnly", dungeonRiftsOnly);
            nbt.setBoolean("overworldRifts", overworldRifts);
            nbt.setBoolean("unstable", unstable);
            nbt.setBoolean("noLinkBack", noLinkBack);
            // nbt.setInteger("maxLinks", maxLinks);
            nbt.setUniqueId("uuid", uuid);
            return nbt;
        }
    }

    @Getter @AllArgsConstructor @lombok.Builder(toBuilder = true)
    public static class PocketEntranceDestination extends RiftDestination {
        private float weight;
        @Builder.Default private List<WeightedRiftDestination> ifDestinations = new LinkedList<>(); // TODO addIfDestination method in builder
        @Builder.Default private List<WeightedRiftDestination> otherwiseDestinations = new LinkedList<>(); // TODO addIfDestination method in builder

        private PocketEntranceDestination() {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            weight = nbt.getFloat("weight");

            ifDestinations = new LinkedList<>();
            NBTTagList ifDestinationsNBT = (NBTTagList) nbt.getTag("ifDestinations");
            for (NBTBase ifDestinationNBT : ifDestinationsNBT) {
                WeightedRiftDestination ifDestination = new WeightedRiftDestination();
                ifDestination.readFromNBT((NBTTagCompound) ifDestinationNBT);
                ifDestinations.add(ifDestination);
            }

            otherwiseDestinations = new LinkedList<>();
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
            return nbt;
        }
    }
}
