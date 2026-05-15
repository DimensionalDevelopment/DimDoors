package org.dimdev.dimdoors.util.schematic;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.datafix.fixes.References;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

public final class SpongeSchematicV2FixTypes {
    private static final String PALETTE_STATE = "State";
    private static final String PALETTE_ID = "Id";
    private static final String PALETTE_ORIGINAL = "Original";
    private static final HookFunction SPONGE_ID_TO_VANILLA_ID = renameField("Id", "id");
    private static final HookFunction VANILLA_ID_TO_SPONGE_ID = renameField("id", "Id");
    private static final HookFunction FLAT_PALETTE_TO_BLOCK_STATE_LIST = new HookFunction() {
        @Override
        public <T> T apply(DynamicOps<T> ops, T value) {
            return flatPaletteToBlockStateList(ops, value);
        }
    };
    private static final HookFunction BLOCK_STATE_LIST_TO_FLAT_PALETTE = new HookFunction() {
        @Override
        public <T> T apply(DynamicOps<T> ops, T value) {
            return blockStateListToFlatPalette(ops, value);
        }
    };

    private SpongeSchematicV2FixTypes() {
    }

    public static TypeTemplate schematic(Schema schema) {
        TypeTemplate spongePalette = DSL.hook(
                DSL.list(DSL.optionalFields(
                        Pair.of(PALETTE_STATE, References.BLOCK_STATE.in(schema)),
                        Pair.of(PALETTE_ID, DSL.constType(DSL.intType()))
                )),
                FLAT_PALETTE_TO_BLOCK_STATE_LIST,
                BLOCK_STATE_LIST_TO_FLAT_PALETTE
        );
        TypeTemplate spongeBlockEntity = DSL.or(
                DSL.hook(References.BLOCK_ENTITY.in(schema), SPONGE_ID_TO_VANILLA_ID, VANILLA_ID_TO_SPONGE_ID),
                DSL.remainder()
        );
        TypeTemplate spongeEntity = DSL.or(
                DSL.hook(References.ENTITY_TREE.in(schema), SPONGE_ID_TO_VANILLA_ID, VANILLA_ID_TO_SPONGE_ID),
                DSL.remainder()
        );

        return DSL.optionalFields(
                Pair.of("Palette", spongePalette),
                Pair.of("BlockEntities", DSL.list(spongeBlockEntity)),
                Pair.of("Entities", DSL.list(spongeEntity))
        );
    }

    private static <T> T flatPaletteToBlockStateList(DynamicOps<T> ops, T value) {
        Optional<Stream<Pair<Dynamic<T>, Dynamic<T>>>> entries = new Dynamic<>(ops, value).asMapOpt().result();
        if (entries.isEmpty()) {
            return value;
        }

        List<T> converted = new ArrayList<>();
        entries.get().forEach(entry -> entry.getFirst().asString().result().ifPresent(blockState -> {
            List<Pair<T, T>> fields = new ArrayList<>();
            readBlockState(ops, blockState).ifPresent(state -> fields.add(Pair.of(ops.createString(PALETTE_STATE), state)));
            fields.add(Pair.of(ops.createString(PALETTE_ID), entry.getSecond().getValue()));
            fields.add(Pair.of(ops.createString(PALETTE_ORIGINAL), ops.createString(blockState)));
            converted.add(ops.createMap(fields.stream()));
        }));
        return ops.createList(converted.stream());
    }

    public static <T> T blockStateListToFlatPalette(DynamicOps<T> ops, T value) {
        Optional<Stream<Dynamic<T>>> entries = new Dynamic<>(ops, value).asStreamOpt().result();
        if (entries.isEmpty()) {
            return value;
        }

        List<Pair<T, T>> converted = new ArrayList<>();
        entries.get().forEach(entry -> entry.get(PALETTE_ID).result().ifPresent(id -> {
            Optional<String> fixedState = entry.get(PALETTE_STATE).result().flatMap(SpongeSchematicV2FixTypes::writeBlockState);
            String blockState = fixedState.orElseGet(() -> entry.get(PALETTE_ORIGINAL).asString(""));
            if (!blockState.isEmpty()) {
                converted.add(Pair.of(ops.createString(blockState), id.getValue()));
            }
        }));
        return ops.createMap(converted.stream());
    }

    public static <T> Optional<T> readBlockState(DynamicOps<T> ops, String value) {
        if (value.isEmpty()) {
            return Optional.empty();
        }

        int propertyStart = value.indexOf('[');
        if (propertyStart == 0 || propertyStart != -1 && !value.endsWith("]")) {
            return Optional.empty();
        }

        String name = propertyStart == -1 ? value : value.substring(0, propertyStart);
        List<Pair<T, T>> state = new ArrayList<>();
        state.add(Pair.of(ops.createString("Name"), ops.createString(qualifyMinecraftId(name))));

        if (propertyStart != -1) {
            String body = value.substring(propertyStart + 1, value.length() - 1);
            if (!body.isEmpty()) {
                List<Pair<T, T>> properties = new ArrayList<>();
                for (String property : body.split(",", -1)) {
                    int split = property.indexOf('=');
                    if (split <= 0 || split >= property.length() - 1) {
                        return Optional.empty();
                    }

                    properties.add(Pair.of(
                            ops.createString(property.substring(0, split)),
                            ops.createString(property.substring(split + 1))
                    ));
                }
                state.add(Pair.of(ops.createString("Properties"), ops.createMap(properties.stream())));
            }
        }

        return Optional.of(ops.createMap(state.stream()));
    }

    private static <T> Optional<String> writeBlockState(Dynamic<T> state) {
        Optional<String> name = state.get("Name").asString().result();
        if (name.isEmpty()) {
            return Optional.empty();
        }

        StringBuilder builder = new StringBuilder(name.get());
        state.get("Properties").result().ifPresent(properties -> {
            TreeMap<String, String> sorted = new TreeMap<>();
            properties.asMapOpt().result().ifPresent(entries -> entries.forEach(entry -> {
                Optional<String> key = entry.getFirst().asString().result();
                Optional<String> value = entry.getSecond().asString().result();
                if (key.isPresent() && value.isPresent()) {
                    sorted.put(key.get(), value.get());
                }
            }));

            if (!sorted.isEmpty()) {
                builder.append('[');
                boolean first = true;
                for (Map.Entry<String, String> property : sorted.entrySet()) {
                    if (!first) {
                        builder.append(',');
                    }

                    builder.append(property.getKey()).append('=').append(property.getValue());
                    first = false;
                }
                builder.append(']');
            }
        });

        return Optional.of(builder.toString());
    }

    private static HookFunction renameField(String from, String to) {
        return new HookFunction() {
            @Override
            public <T> T apply(DynamicOps<T> ops, T value) {
                return SpongeSchematicV2FixTypes.renameField(new Dynamic<>(ops, value), from, to).getValue();
            }
        };
    }

    private static <T> Dynamic<T> renameField(Dynamic<T> dynamic, String from, String to) {
        Optional<Dynamic<T>> existing = dynamic.get(to).result();
        if (existing.isPresent()) {
            return dynamic;
        }

        Optional<Dynamic<T>> renamed = dynamic.get(from).result();
        return renamed.map(value -> dynamic.set(to, value).remove(from)).orElse(dynamic);
    }

    private static String qualifyMinecraftId(String id) {
        return id.indexOf(':') == -1 ? "minecraft:" + id : id;
    }
}
