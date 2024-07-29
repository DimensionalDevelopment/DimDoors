package org.dimdev.dimdoors.world.structure.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import org.dimdev.dimdoors.world.structure.ModRuleBlockEntityModifiers;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record DestinationDataModifier(Map<Integer, CompoundTag> destinations) implements RuleBlockEntityModifier {
    public static final Codec<DestinationDataModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.INT, CompoundTag.CODEC).fieldOf("destinations").forGetter(DestinationDataModifier::destinations)).apply(instance, DestinationDataModifier::new));
    @Nullable
    @Override
    public CompoundTag apply(RandomSource random, @Nullable CompoundTag tag) {
        if(tag != null && tag.contains("data")) {
            var data = tag.getCompound("data");

            var id = getMarkerId(data);
            if(id >= 0) {
                if(destinations.containsKey(id)) {
                    var nbt = destinations.get(id);

                    data.put("destination", nbt);
                }
            }
        }

        return tag;
    }

    @Override
    public RuleBlockEntityModifierType<?> getType() {
        return ModRuleBlockEntityModifiers.DESTINATION_DATA.get();
    }

    private int getMarkerId(CompoundTag data) {
        var destination = data.getCompound("destination");
        if(destination.getString("type").equals("dimdoors:id_marker")) {
            return destination.getInt("id");
        }

        return -1;
    }
}
