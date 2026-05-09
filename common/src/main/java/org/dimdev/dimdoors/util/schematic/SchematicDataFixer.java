package org.dimdev.dimdoors.util.schematic;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public final class SchematicDataFixer {
    public static final DSL.TypeReference SCHEMATIC = () -> "sponge_schematic";
    public static final int SCHEMATIC_VERSION = 1;
    public static final int DATA_VERSION = SharedConstants.getCurrentVersion().getDataVersion().getVersion();

    public static final DataFixer DATA_FIXER = Util.make(new DataFixerBuilder(DATA_VERSION), builder -> {
        builder.addSchema(SCHEMATIC_VERSION, SchematicSchema::new);
        Schema current = builder.addSchema(DATA_VERSION, SchematicSchema::new);
        builder.addFixer(new SpongeV1Fix(current));
        builder.addFixer(new VanillaPayloadFix(current));
    }).build().fixer();

    private SchematicDataFixer() {
    }

    public static CompoundTag update(CompoundTag tag) {
        return update(getDataVersion(tag), tag);
    }

    public static CompoundTag update(int oldVersion, CompoundTag tag) {
        return update(oldVersion, DATA_VERSION, tag);
    }

    public static CompoundTag update(int oldVersion, int newVersion, CompoundTag tag) {
        return (CompoundTag) DATA_FIXER.update(SCHEMATIC, new Dynamic<>(NbtOps.INSTANCE, tag), oldVersion, newVersion).getValue();
    }

    private static int getDataVersion(CompoundTag tag) {
        if (tag.contains("Data Version", Tag.TAG_ANY_NUMERIC)) {
            return tag.getInt("Data Version");
        }
        if (tag.contains("DataVersion", Tag.TAG_ANY_NUMERIC)) {
            return tag.getInt("DataVersion");
        }
        if (tag.contains("Version", Tag.TAG_ANY_NUMERIC)) {
            return tag.getInt("Version");
        }
        return SCHEMATIC_VERSION;
    }

    private static final class SchematicSchema extends Schema {
        private SchematicSchema(int versionKey, Schema parent) {
            super(versionKey, parent);
        }

        @Override
        public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
            schema.registerType(false, SCHEMATIC, DSL::remainder);
        }
    }

    private static final class SpongeV1Fix extends DataFix {
        private SpongeV1Fix(Schema outputSchema) {
            super(outputSchema, false);
        }

        @Override
        protected TypeRewriteRule makeRule() {
            Type<?> inputType = this.getInputSchema().getType(SCHEMATIC);
            Type<?> outputType = this.getOutputSchema().getType(SCHEMATIC);
            return this.writeFixAndRead("Sponge schematic v1 base shape", inputType, outputType, SpongeV1Fix::fix);
        }

        private static Dynamic<?> fix(Dynamic<?> dynamic) {
            CompoundTag tag = (CompoundTag) dynamic.convert(NbtOps.INSTANCE).getValue();
            CompoundTag fixed = tag.copy();

            if (!fixed.contains("Version", Tag.TAG_ANY_NUMERIC)) {
                fixed.putInt("Version", SCHEMATIC_VERSION);
            }
            if (!fixed.contains("Offset", Tag.TAG_INT_ARRAY)) {
                fixed.put("Offset", new IntArrayTag(new int[] {0, 0, 0}));
            }
            if (fixed.contains("TileEntities", Tag.TAG_LIST) && !fixed.contains("BlockEntities", Tag.TAG_LIST)) {
                fixed.put("BlockEntities", fixed.getList("TileEntities", Tag.TAG_COMPOUND).copy());
            }
            if (!fixed.contains("PaletteMax", Tag.TAG_ANY_NUMERIC)) {
                fixed.putInt("PaletteMax", paletteMax(fixed));
            }

            return convert(dynamic, fixed);
        }

        private static int paletteMax(CompoundTag schematic) {
            if (!schematic.contains("Palette", Tag.TAG_COMPOUND)) {
                return 0;
            }

            int max = -1;
            CompoundTag palette = schematic.getCompound("Palette");
            for (String key : palette.getAllKeys()) {
                if (palette.contains(key, Tag.TAG_ANY_NUMERIC)) {
                    max = Math.max(max, palette.getInt(key));
                }
            }
            return max + 1;
        }
    }

    private static final class VanillaPayloadFix extends DataFix {
        private VanillaPayloadFix(Schema outputSchema) {
            super(outputSchema, false);
        }

        @Override
        protected TypeRewriteRule makeRule() {
            Type<?> inputType = this.getInputSchema().getType(SCHEMATIC);
            Type<?> outputType = this.getOutputSchema().getType(SCHEMATIC);
            return this.writeFixAndRead("Sponge schematic vanilla payloads", inputType, outputType, this::fix);
        }

        private Dynamic<?> fix(Dynamic<?> dynamic) {
            CompoundTag tag = (CompoundTag) dynamic.convert(NbtOps.INSTANCE).getValue();
            CompoundTag fixed = tag.copy();
            int fromVersion = getDataVersion(fixed);
            int toVersion = this.getOutputSchema().getVersionKey();

            if (fromVersion >= toVersion) {
                return dynamic;
            }

            fixPalette(fixed, fromVersion, toVersion);
            fixBlockEntities(fixed, "BlockEntities", fromVersion, toVersion);
            fixBlockEntities(fixed, "TileEntities", fromVersion, toVersion);
            fixEntities(fixed, fromVersion, toVersion);
            touchDataVersion(fixed, toVersion);

            return convert(dynamic, fixed);
        }

        private static void fixPalette(CompoundTag schematic, int fromVersion, int toVersion) {
            if (!schematic.contains("Palette", Tag.TAG_COMPOUND)) {
                return;
            }

            CompoundTag palette = schematic.getCompound("Palette");
            CompoundTag fixedPalette = new CompoundTag();
            for (String blockState : palette.getAllKeys()) {
                Tag value = palette.get(blockState);
                if (value != null) {
                    fixedPalette.put(fixBlockState(blockState, fromVersion, toVersion), value.copy());
                }
            }
            schematic.put("Palette", fixedPalette);
        }

        private static String fixBlockState(String state, int fromVersion, int toVersion) {
            try {
                Tag fixed = DataFixers.getDataFixer().update(
                        References.BLOCK_STATE,
                        new Dynamic<>(NbtOps.INSTANCE, readBlockState(state)),
                        fromVersion,
                        toVersion
                ).getValue();

                if (fixed instanceof CompoundTag fixedState) {
                    return writeBlockState(fixedState, state);
                }
            } catch (RuntimeException ignored) {
            }

            return state;
        }

        private static CompoundTag readBlockState(String value) {
            CompoundTag state = new CompoundTag();
            int propertyStart = value.indexOf('[');
            state.putString("Name", qualifyMinecraftId(propertyStart == -1 ? value : value.substring(0, propertyStart)));

            if (propertyStart != -1 && value.endsWith("]")) {
                CompoundTag properties = new CompoundTag();
                String body = value.substring(propertyStart + 1, value.length() - 1);
                if (!body.isEmpty()) {
                    for (String property : body.split(",")) {
                        int split = property.indexOf('=');
                        if (split > 0 && split < property.length() - 1) {
                            properties.putString(property.substring(0, split), property.substring(split + 1));
                        }
                    }
                }
                if (!properties.isEmpty()) {
                    state.put("Properties", properties);
                }
            }

            return state;
        }

        private static String writeBlockState(CompoundTag state, String fallback) {
            if (!state.contains("Name", Tag.TAG_STRING)) {
                return fallback;
            }

            StringBuilder builder = new StringBuilder(state.getString("Name"));
            if (state.contains("Properties", Tag.TAG_COMPOUND)) {
                CompoundTag properties = state.getCompound("Properties");
                if (!properties.isEmpty()) {
                    ArrayList<String> keys = new ArrayList<>(properties.getAllKeys());
                    Collections.sort(keys);

                    builder.append('[');
                    for (int i = 0; i < keys.size(); i++) {
                        if (i > 0) {
                            builder.append(',');
                        }

                        String key = keys.get(i);
                        builder.append(key).append('=').append(properties.getString(key));
                    }
                    builder.append(']');
                }
            }

            return builder.toString();
        }

        private static void fixBlockEntities(CompoundTag schematic, String key, int fromVersion, int toVersion) {
            if (!schematic.contains(key, Tag.TAG_LIST)) {
                return;
            }

            ListTag fixedList = new ListTag();
            for (Tag value : schematic.getList(key, Tag.TAG_COMPOUND)) {
                if (value instanceof CompoundTag blockEntity) {
                    fixedList.add(fixBlockEntity(blockEntity, fromVersion, toVersion));
                }
            }
            schematic.put(key, fixedList);
        }

        private static CompoundTag fixBlockEntity(CompoundTag original, int fromVersion, int toVersion) {
            CompoundTag work = original.copy();
            String id = readId(work);
            if (!id.isEmpty()) {
                work.putString("id", id);
                work.remove("Id");
            }

            int[] pos = null;
            if (work.contains("Pos", Tag.TAG_INT_ARRAY)) {
                int[] readPos = work.getIntArray("Pos");
                if (readPos.length >= 3) {
                    pos = new int[] {readPos[0], readPos[1], readPos[2]};
                    work.putInt("x", pos[0]);
                    work.putInt("y", pos[1]);
                    work.putInt("z", pos[2]);
                    work.remove("Pos");
                }
            }

            CompoundTag fixed = fixVanilla(References.BLOCK_ENTITY, work, fromVersion, toVersion);
            if (pos != null) {
                fixed.putIntArray("Pos", new int[] {
                        fixed.contains("x", Tag.TAG_ANY_NUMERIC) ? fixed.getInt("x") : pos[0],
                        fixed.contains("y", Tag.TAG_ANY_NUMERIC) ? fixed.getInt("y") : pos[1],
                        fixed.contains("z", Tag.TAG_ANY_NUMERIC) ? fixed.getInt("z") : pos[2]
                });
                fixed.remove("x");
                fixed.remove("y");
                fixed.remove("z");
            }

            if (fixed.contains("id", Tag.TAG_STRING)) {
                fixed.putString("Id", fixed.getString("id"));
            }
            return fixed;
        }

        private static void fixEntities(CompoundTag schematic, int fromVersion, int toVersion) {
            if (!schematic.contains("Entities", Tag.TAG_LIST)) {
                return;
            }

            ListTag fixedList = new ListTag();
            for (Tag value : schematic.getList("Entities", Tag.TAG_COMPOUND)) {
                if (value instanceof CompoundTag entity) {
                    fixedList.add(fixEntity(entity, fromVersion, toVersion));
                }
            }
            schematic.put("Entities", fixedList);
        }

        private static CompoundTag fixEntity(CompoundTag original, int fromVersion, int toVersion) {
            CompoundTag work = original.copy();
            String id = readId(work);
            if (!id.isEmpty()) {
                work.putString("id", id);
                work.remove("Id");
            }

            CompoundTag fixed = fixVanilla(References.ENTITY, work, fromVersion, toVersion);
            if (fixed.contains("id", Tag.TAG_STRING)) {
                fixed.putString("Id", fixed.getString("id"));
            }
            return fixed;
        }

        private static CompoundTag fixVanilla(DSL.TypeReference type, CompoundTag original, int fromVersion, int toVersion) {
            Tag fixed = DataFixers.getDataFixer().update(type, new Dynamic<>(NbtOps.INSTANCE, original), fromVersion, toVersion).getValue();
            return fixed instanceof CompoundTag compound ? compound : original;
        }

        private static String readId(CompoundTag tag) {
            if (tag.contains("id", Tag.TAG_STRING)) {
                return tag.getString("id");
            }
            if (tag.contains("Id", Tag.TAG_STRING)) {
                return tag.getString("Id");
            }
            return "";
        }

        private static void touchDataVersion(CompoundTag tag, int version) {
            if (tag.contains("Data Version", Tag.TAG_ANY_NUMERIC)) {
                tag.putInt("Data Version", version);
            }
            if (tag.contains("DataVersion", Tag.TAG_ANY_NUMERIC)) {
                tag.putInt("DataVersion", version);
            }
        }

        private static String qualifyMinecraftId(String id) {
            return id.indexOf(':') == -1 ? "minecraft:" + id : id;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Dynamic<?> convert(Dynamic<?> dynamic, CompoundTag tag) {
        return new Dynamic(dynamic.getOps(), NbtOps.INSTANCE.convertTo(dynamic.getOps(), tag));
    }
}
