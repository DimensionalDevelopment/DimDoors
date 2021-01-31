package org.dimdev.dimdoors.pockets;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocketList;

public final class PocketGroup {
	/*
    public static final Codec<PocketGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("group").forGetter(PocketGroup::getGroup),
            VirtualPocket.CODEC.listOf().fieldOf("pockets").forGetter(PocketGroup::getEntries)
    ).apply(instance, PocketGroup::new));
	 */

    private String group;
    private VirtualPocketList pocketList;

    public PocketGroup() {
	}

    public PocketGroup(String group, VirtualPocketList pocketList) {
        this.group = group;
        this.pocketList = pocketList;
    }

    public PocketGroup fromTag(CompoundTag tag) {
    	this.group = tag.getString("group");
		this.pocketList = VirtualPocketList.deserialize(tag.getList("pockets", 10));
		return this;
	}

	public CompoundTag toTag(CompoundTag tag) {
    	tag.putString("group", this.group);
		tag.put("pockets", pocketList.toTag(new ListTag()));
		return tag;
	}

    public String getGroup() {
        return this.group;
    }

    public VirtualPocketList getPocketList() {
        return this.pocketList;
    }

    @Override
    public String toString() {
        return "PocketType{" +
                "group='" + this.group + '\'' +

                ", entries=" + this.pocketList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        PocketGroup that = (PocketGroup) o;
        return Objects.equals(this.group, that.group) &&
                Objects.equals(this.pocketList, that.pocketList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.group, this.pocketList);
    }

    public void init() {
    	pocketList.init(this);
	}
}
