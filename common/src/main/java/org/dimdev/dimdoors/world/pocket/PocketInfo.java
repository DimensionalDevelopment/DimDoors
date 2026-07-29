package org.dimdev.dimdoors.world.pocket;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.util.CodecUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public record PocketInfo(ResourceKey<Level> world, int id) {
    public static final Codec<PocketInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(PocketInfo::world),
            Codec.INT.fieldOf("id").forGetter(PocketInfo::id)
    ).apply(instance, PocketInfo::new));

    public static final Codec<PocketInfo> STRING_CODEC = Codec.STRING.comapFlatMap(PocketInfo::fromString, PocketInfo::toString);

    public static DataResult<PocketInfo> fromString(String value) {
        var strings = value.split("#");

        if(strings.length != 2) {
            return DataResult.error(() -> "Value doesn't have # seperator.");
        } else {
            return ResourceLocation.read(strings[0])
                    .map(loc -> ResourceKey.create(Registries.DIMENSION, loc))
                    .flatMap(world -> CodecUtils.parseIntString(strings[1])
                            .map(id -> new PocketInfo(world, id))
                    );
        }
    }

    @Override
    public @NotNull String toString() {
        return world.location() + "#" + id;
    }
}
