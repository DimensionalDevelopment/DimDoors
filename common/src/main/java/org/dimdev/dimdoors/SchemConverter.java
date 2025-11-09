package org.dimdev.dimdoors;//package org.dimdev.dimdoors;
//
//import com.google.common.collect.ImmutableMap;
//import com.mojang.datafixers.DSL;
//import com.mojang.datafixers.DataFixerBuilder;
//import com.mojang.datafixers.Typed;
//import com.mojang.datafixers.schemas.Schema;
//import com.mojang.serialization.Dynamic;
//import com.mojang.serialization.JsonOps;
//import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
//import net.minecraft.SharedConstants;
//import net.minecraft.Util;
//import net.minecraft.client.Minecraft;
//import net.minecraft.nbt.NbtAccounter;
//import net.minecraft.nbt.NbtIo;
//import net.minecraft.nbt.NbtOps;
//import net.minecraft.nbt.Tag;
//import net.minecraft.util.GsonHelper;
//import net.minecraft.util.datafix.DataFixers;
//import net.minecraft.util.datafix.FixWolfHealth;
//import net.minecraft.util.datafix.fixes.*;
//import net.minecraft.util.datafix.schemas.*;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Set;
//import java.util.function.Function;
//import java.util.function.UnaryOperator;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchemConverter {
    private static int toVersion = 0;
    private static int fromVersion = 0;

    public static void main(String[] args) throws IOException {
        SharedConstants.setVersion(DetectedVersion.tryDetectVersion());

        var path = Path.of("D:\\Git Repos\\DimDoors\\common\\src\\main\\resources\\resourcepacks\\classic\\data\\dimdoors\\pockets\\schematic\\");

        Files.walk(path).filter(a -> a.toString().endsWith(".schem")).forEach(SchemConverter::convert);




//        var dynamic = new Dynamic<>(NbtOps.INSTANCE, nbt);
//
//        var json = dynamic.convert(JsonOps.INSTANCE).getValue();
//
//        System.out.println(GsonHelper.toStableString(json));


    }

    private static void convert(Path path) {
        try {

            var nbt = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());

            toVersion = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
            fromVersion = NbtUtils.getDataVersion(nbt, toVersion);


            if (nbt.contains("BlockEntities")) {
                var value = nbt.getList("BlockEntities", Tag.TAG_COMPOUND).stream().map(CompoundTag.class::cast).map(SchemConverter::updateBlockEntity).collect(Collectors.toCollection(ListTag::new));
                nbt.put("BlockEntities", value);
            }

            if (nbt.contains("Entities")) {
                var value = nbt.getList("Entities", Tag.TAG_COMPOUND).stream().map(CompoundTag.class::cast).map(SchemConverter::updateEntity).collect(Collectors.toCollection(ListTag::new));
                nbt.put("Entities", value);
            }


            nbt.putInt("DataVersion", toVersion);

            NbtIo.writeCompressed(nbt, path);
        } catch (IOException e) {
            System.out.println("Failed to convert path: " + path);
        } finally {
            System.out.println("Succeeded to convert path: " + path);
        }
    }

    public static CompoundTag updateEntity(CompoundTag original) {
        // Create working copy
        CompoundTag temp = original.copy();

        // Step 1: Convert "Id" → "id"
        boolean hadUpperId = false;
        if (temp.contains("Id", Tag.TAG_STRING)) {
            temp.putString("id", temp.getString("Id"));
            temp.remove("Id");
            hadUpperId = true;
        }

        // Step 2: Convert "Pos" → x,y,z
        boolean hadPos = false;
        int[] pos = null;
        if (temp.contains("Pos", Tag.TAG_INT_ARRAY)) {
            pos = ((IntArrayTag) temp.get("Pos")).getAsIntArray();
            if (pos.length >= 3) {
                temp.putInt("x", pos[0]);
                temp.putInt("y", pos[1]);
                temp.putInt("z", pos[2]);
                temp.remove("Pos");
                hadPos = true;
            }
        }

        // Step 3: Apply DataFixer update
        var dynamic = new Dynamic<>(NbtOps.INSTANCE, temp);
        var fixed = DataFixers.getDataFixer().update(References.ENTITY, dynamic, fromVersion, toVersion);
        CompoundTag result = (CompoundTag) fixed.getValue();

        // Step 4: Revert "id" → "Id"
        if (hadUpperId && result.contains("id", Tag.TAG_STRING)) {
            result.putString("Id", result.getString("id"));
            result.remove("id");
        }

        // Step 5: Revert x,y,z → "Pos"
        if (hadPos) {
            int[] posArray = new int[] {
                    result.getInt("x"),
                    result.getInt("y"),
                    result.getInt("z")
            };
            result.put("Pos", new IntArrayTag(posArray));
            result.remove("x");
            result.remove("y");
            result.remove("z");
        }

        return result;
    }

    public static CompoundTag updateBlockEntity(CompoundTag original) {
        // Create working copy
        CompoundTag temp = original.copy();

        // Step 1: Convert "Id" → "id"
        boolean hadUpperId = false;
        if (temp.contains("Id", Tag.TAG_STRING)) {
            temp.putString("id", temp.getString("Id"));
            temp.remove("Id");
            hadUpperId = true;
        }

        // Step 2: Convert "Pos" → x,y,z
        boolean hadPos = false;
        int[] pos = null;
        if (temp.contains("Pos", Tag.TAG_INT_ARRAY)) {
            pos = ((IntArrayTag) temp.get("Pos")).getAsIntArray();
            if (pos.length >= 3) {
                temp.putInt("x", pos[0]);
                temp.putInt("y", pos[1]);
                temp.putInt("z", pos[2]);
                temp.remove("Pos");
                hadPos = true;
            }
        }

        // Step 3: Apply DataFixer update
        var dynamic = new Dynamic<>(NbtOps.INSTANCE, temp);
        var fixed = DataFixers.getDataFixer().update(References.BLOCK_ENTITY, dynamic, fromVersion, toVersion);
        CompoundTag result = (CompoundTag) fixed.getValue();

        // Step 4: Revert "id" → "Id"
        if (hadUpperId && result.contains("id", Tag.TAG_STRING)) {
            result.putString("Id", result.getString("id"));
            result.remove("id");
        }

        // Step 5: Revert x,y,z → "Pos"
        if (hadPos) {
            int[] posArray = new int[] {
                    result.getInt("x"),
                    result.getInt("y"),
                    result.getInt("z")
            };
            result.put("Pos", new IntArrayTag(posArray));
            result.remove("x");
            result.remove("y");
            result.remove("z");
        }

        return result;
    }
}