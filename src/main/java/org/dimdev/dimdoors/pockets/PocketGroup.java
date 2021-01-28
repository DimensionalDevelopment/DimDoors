package org.dimdev.dimdoors.pockets;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public final class PocketGroup {
	/*
    public static final Codec<PocketGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("group").forGetter(PocketGroup::getGroup),
            VirtualPocket.CODEC.listOf().fieldOf("pockets").forGetter(PocketGroup::getEntries)
    ).apply(instance, PocketGroup::new));
	 */

    private String group;
    private List<VirtualPocket> entries;

    public PocketGroup() {
	}

    public PocketGroup(String group, List<VirtualPocket> entries) {
        this.group = group;
        this.entries = entries;
    }

    public PocketGroup fromTag(CompoundTag tag) {
    	this.group = tag.getString("group");

		ListTag pockets = tag.getList("pockets", 10);
		this.entries = Lists.newArrayList();
		for (int i = 0; i < pockets.size(); i++) {
			CompoundTag pocket = pockets.getCompound(i);
			entries.add(VirtualPocket.deserialize(pocket));
		}
		return this;
	}

	public CompoundTag toTag(CompoundTag tag) {
    	tag.putString("group", this.group);

		ListTag pockets = new ListTag();
		entries.forEach(pocket -> pockets.add(pocket.toTag(new CompoundTag())));
		tag.put("pockets", pockets);
		return tag;
	}

    public String getGroup() {
        return this.group;
    }

    public List<VirtualPocket> getEntries() {
        return this.entries;
    }

    @Override
    public String toString() {
        return "PocketType{" +
                "group='" + this.group + '\'' +

                ", entries=" + this.entries +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        PocketGroup that = (PocketGroup) o;
        return Objects.equals(this.group, that.group) &&
                Objects.equals(this.entries, that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.group, this.entries);
    }
}
