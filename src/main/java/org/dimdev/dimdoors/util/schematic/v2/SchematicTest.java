package org.dimdev.dimdoors.util.schematic.v2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.DataResult;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import net.fabricmc.loader.api.FabricLoader;

public class SchematicTest {
    public static void test() throws IOException {
        SchematicMetadata meta = new SchematicMetadata("Dimdoors Test Schematic", "Dimensional Development", System.currentTimeMillis(), ImmutableList.of("dimdoors"));
        BlockEntity be = new BlastFurnaceBlockEntity();
        be.setPos(new BlockPos(0, 0, 0));
        CompoundTag beTag = new CompoundTag();
        be.toTag(beTag);
        Entity e = new FireballEntity(EntityType.FIREBALL, DimensionalDoorsInitializer.getServer().getOverworld());
        e.setPos(1, 1, 1);
        CompoundTag eTag = new CompoundTag();
        e.saveSelfToTag(eTag);
        Schematic schematic = new Schematic(2, 2543, meta, (short) 5, (short) 5, (short) 5, Vec3i.ZERO, -1, ImmutableBiMap.of(Blocks.AIR.getDefaultState(), 0, Blocks.NETHERRACK.getDefaultState(), 1), ByteBuffer.wrap(new byte[]{1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0}), ImmutableList.of(beTag), ImmutableList.of(eTag));
        DataResult<Tag> result = Schematic.CODEC.encodeStart(NbtOps.INSTANCE, schematic);
        Path path = FabricLoader.getInstance().getConfigDir().resolve("schematic.schem");
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        NbtIo.writeCompressed((CompoundTag) result.getOrThrow(false, System.err::println), path.toFile());
    }
}
