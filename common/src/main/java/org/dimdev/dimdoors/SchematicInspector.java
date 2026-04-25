package org.dimdev.dimdoors;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import org.dimdev.dimdoors.util.schematic.SchematicBlockPalette;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class SchematicInspector {
    private static final Path SCHEMATIC_ROOT = Path.of(
            "common",
            "src",
            "main",
            "resources",
            "resourcepacks",
            "default",
            "data",
            "dimdoors",
            "pockets",
            "schematic"
    );
    private static final List<Path> DEFAULT_TARGETS = List.of(
            SCHEMATIC_ROOT.resolve(Path.of("lab", "lab_experimentation.schem")),
            SCHEMATIC_ROOT.resolve(Path.of("lab", "labfinal.schem"))
    );

    private SchematicInspector() {
    }

    public static void main(String[] args) throws IOException {
        SharedConstants.setVersion(DetectedVersion.tryDetectVersion());

        List<Path> targets = args.length == 0
                ? DEFAULT_TARGETS
                : Arrays.stream(args).map(Path::of).toList();

        for (Path target : targets) {
            inspect(target.toAbsolutePath().normalize());
        }
    }

    private static void inspect(Path path) throws IOException {
        CompoundTag root = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());

        var data = root.getByteArray("BlockData");

        var pallete = new HashMap<Integer, String>();

        var nbt = root.getCompound("Palette");

        for(var name : nbt.getAllKeys()) {
            pallete.put(nbt.getInt(name), name);
        }

        var width = root.getInt("Width");
        var height = root.getInt("Height");
        var length = root.getInt("Length");

        var posMap = new HashMap<BlockPos, Integer>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    int i = x + z * width + y * width * length;

                    posMap.put(new BlockPos(x,y,z), (int) data[i]);
                }
            }

        }

        var map = root.getList("BlockEntities", Tag.TAG_COMPOUND).stream().map(a -> (CompoundTag) a).map(a -> a.get("Pos")).map(a -> BlockPos.CODEC.parse(NbtOps.INSTANCE, a).result().get()).toArray();



        System.out.println();
    }

    private static void printSection(CompoundTag root, String key) {
        if (!root.contains(key)) {
            System.out.println(key + "=<missing>");
            return;
        }

        Tag value = root.get(key);
        System.out.println(key + "=" + toStableJson(value));
    }

    private static String toStableJson(Tag tag) {
        return GsonHelper.toStableString(new Dynamic<>(NbtOps.INSTANCE, tag).convert(JsonOps.INSTANCE).getValue());
    }
}
