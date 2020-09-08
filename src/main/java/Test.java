import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Test {
    public static String[] tags = new String[] {"color", "destination", "forcedColor", "alwaysDelete", "properties"};

    public static void main(String[] args) throws IOException {
        Files.walk(Paths.get("."))
                .filter(path -> path.toString().endsWith(".schem"))
                .forEach(path1 -> {
                    try {
                        create(path1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static void create(Path path) throws IOException {
        CompoundTag tag = NbtIo.readCompressed(Files.newInputStream(path));

        ListTag list = tag.getList("TileEntities", NbtType.COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag compoundTag = list.getCompound(i);

            System.out.println("Derp");

            if(Stream.of(tags).allMatch(compoundTag::contains)) {
                CompoundTag data = new CompoundTag();

                for(String name : tags) {
                    data.put(name, compoundTag.get(name));
                    compoundTag.remove(name);
                }

                compoundTag.put("data", data);
            }

            System.out.println(tag);
        }

        NbtIo.writeCompressed(tag, Files.newOutputStream(path));

        System.out.println(path);
    }
}
