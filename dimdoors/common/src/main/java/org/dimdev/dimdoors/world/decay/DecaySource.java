package org.dimdev.dimdoors.world.decay;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.HashMap;
import java.util.Map;

public enum DecaySource implements StringRepresentable {
    LIMBO("unravelled_fabric", false),
    REALITY_SPONGE("reality_sponge", false),
    RIFT("rift", true),
    CUSTOM("custom", false);

    public static final Codec<DecaySource> CODEC = StringRepresentable.fromValues(DecaySource::values);

    private static final Map<String, DecaySource> MAP = new HashMap<>(); //TODO: Remove once converted into codec.

    static {
        for (DecaySource source : values()) {
            MAP.put(source.getSerializedName(), source);
        }
    }

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
