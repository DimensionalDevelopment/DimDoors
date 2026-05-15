package org.dimdev.dimdoors.item.door.data.condition;

import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.types.templates.Product;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.dimdev.dimdoors.api.util.Products;

public abstract class MultipleCondition implements Condition {
    public static final MapCodec<List<Condition>> LIST = Condition.CODEC.listOf().fieldOf("conditions");

    protected final List<Condition> conditions;

    protected List<Condition> conditions() {
        return conditions;
    }

    protected MultipleCondition(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
