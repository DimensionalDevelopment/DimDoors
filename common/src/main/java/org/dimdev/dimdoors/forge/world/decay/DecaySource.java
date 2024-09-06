package org.dimdev.dimdoors.forge.world.decay;

import net.minecraft.util.StringRepresentable;

import java.util.HashMap;
import java.util.Map;

public enum DecaySource implements StringRepresentable {
    LIMBO("unravelled_fabric", false),
    REAlITY_SPONGE("reality_sponge", false),
    RIFT("rift", true),
    CUSTOM("custom", false);

    private static final Map<String, DecaySource> MAP = new HashMap<>(); //TODO: Remove once converted into codec.

    private final String name;
    private final boolean decayIntoWorldThread;

    DecaySource(String name, boolean decayIntoWorldThread) {
        this.name = name;
        this.decayIntoWorldThread = decayIntoWorldThread;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static DecaySource fromName(String name) {
        return MAP.getOrDefault(name.toLowerCase(), CUSTOM);
    }

    public boolean decayIntoWorldThread() {
        return decayIntoWorldThread;
    }
}
