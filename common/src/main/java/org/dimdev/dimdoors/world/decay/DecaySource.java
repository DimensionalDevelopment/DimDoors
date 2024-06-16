package org.dimdev.dimdoors.world.decay;

import net.minecraft.util.StringRepresentable;

import java.util.HashMap;
import java.util.Map;

public enum DecaySource implements StringRepresentable {
    LIMBO("unravelled_fabric"),
    REAlITY_SPONGE("reality_sponge"),
    CUSTOM("custom");

    private static final Map<String, DecaySource> MAP = new HashMap<>(); //TODO: Remove once converted into codec.

    private final String name;

    DecaySource(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static DecaySource fromName(String name) {
        return MAP.getOrDefault(name.toLowerCase(), CUSTOM);
    }
}
