package org.dimdev.dimdoors.pockets;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.util.schematic.SchematicPlacer;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public interface PocketTemplate {
    void place(Pocket<?, ?> pocket);
    Vec3i getSize();

    record SchematicTemplate(Schematic schematic) implements PocketTemplate {
        public static SchematicTemplate create(CompoundTag tag) {
            return new SchematicTemplate(Schematic.fromNbt(tag));
        }

        public void place(Pocket<?, ?> pocket) {
            pocket.setSize(schematic.getWidth(), schematic.getHeight(), schematic.getLength());
            ServerLevel world = DimensionalDoors.getWorld(pocket.getWorld());
            BlockPos origin = pocket.getOrigin();
            SchematicPlacer.place(this.schematic, world, origin);
        }

        @Override
        public Vec3i getSize() {
            return new Vec3i(schematic.getWidth(), schematic.getHeight(), schematic.getLength());
        }
    }

    record NbtTemplate(StructureTemplate template) implements PocketTemplate {
        private static final StructurePlaceSettings SETTING = new StructurePlaceSettings();

        public static NbtTemplate create(CompoundTag tag) {
            var template = new StructureTemplate();
            template.load(BuiltInRegistries.BLOCK.asLookup(), tag);
            return new NbtTemplate(template);
        }

        @Override
        public void place(Pocket<?, ?> pocket) {
            pocket.setSize(template.getSize());
            ServerLevel world = DimensionalDoors.getWorld(pocket.getWorld());
            BlockPos origin = pocket.getOrigin();

            template.placeInWorld(world, BlockPos.ZERO, origin, SETTING, world.getRandom(), 0);
        }

        @Override
        public Vec3i getSize() {
            return template.getSize();
        }
    }
}