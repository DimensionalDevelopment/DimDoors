package org.dimdev.dimdoors.pockets;

import java.util.List;
import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class PocketGroup {
    public static final Codec<PocketGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("group").forGetter(PocketGroup::getGroup),
            VirtualPocket.CODEC.listOf().fieldOf("pockets").forGetter(PocketGroup::getEntries)
    ).apply(instance, PocketGroup::new));
    private final String group;
    private final List<VirtualPocket> entries;

    public PocketGroup(String group, List<VirtualPocket> entries) {
        this.group = group;
        this.entries = entries;
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
