package org.dimdev.dimdoors.world.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;

import java.util.Optional;

public class PocketStructure extends Structure {
    public static final MapCodec<PocketStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(PocketGenerator.CODEC))

    private final Holder<PocketGenerator> value;
    private final Vec3i size;

    public PocketStructure(Holder<PocketGenerator> value, int depth) {
        super(new StructureSettings(HolderSet.empty()));
        this.value = value;

        this.size = value.value().getSize(null);

        value.value().) {

        }

    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext generationContext) {
        return Optional.of(new GenerationStub(BlockPos.ZERO, builder -> builder.addPiece(new PocketStructure.StructurePiece())));


    }

    @Override
    public StructureType<?> type() {
        return null;
    }

    private class StructurePiece extends net.minecraft.world.level.levelgen.structure.StructurePiece {
        protected StructurePiece(StructurePieceType structurePieceType, int i, BoundingBox boundingBox) {
            super(structurePieceType, i, boundingBox);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {

        }

        @Override
        public void postProcess(WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {

        }
    }
}
