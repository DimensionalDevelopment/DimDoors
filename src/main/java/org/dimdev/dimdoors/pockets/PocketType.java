package org.dimdev.dimdoors.pockets;

import java.util.List;
import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class PocketType {
    public static final Codec<PocketType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("group").forGetter(PocketType::getGroup),
            PocketEntry.CODEC.listOf().fieldOf("pockets").forGetter(PocketType::getEntries)
    ).apply(instance, PocketType::new));
    private final String group;
    private final List<PocketEntry> entries;

    public PocketType(String group, List<PocketEntry> entries) {
        this.group = group;
        this.entries = entries;
    }

    public String getGroup() {
        return this.group;
    }

    public List<PocketEntry> getEntries() {
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
        PocketType that = (PocketType) o;
        return Objects.equals(this.group, that.group) &&
                Objects.equals(this.entries, that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.group, this.entries);
    }

    public static final class PocketEntry {
        public static final Codec<PocketEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("size").forGetter(PocketEntry::getSize),
                Codec.STRING.fieldOf("id").forGetter(PocketEntry::getName)
        ).apply(instance, PocketEntry::new));
        private final int size;
        private final String name;

        PocketEntry(int size, String name) {
            this.size = size;
            this.name = name;
        }

        public int getSize() {
            return this.size;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            PocketEntry that = (PocketEntry) o;
            return this.size == that.size &&
                    Objects.equals(this.name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.size, this.name);
        }

        @Override
        public String toString() {
            return "PocketEntry{" +
                    "size=" + this.size +
                    ", name=" + this.name +
                    '}';
        }
    }
}
