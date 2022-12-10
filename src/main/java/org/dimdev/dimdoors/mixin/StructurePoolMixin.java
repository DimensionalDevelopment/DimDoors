package org.dimdev.dimdoors.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;

@Mixin(StructurePool.class)
public class StructurePoolMixin {
	@Unique
	private static final Identifier HOUSES_ID = new Identifier("bastion/treasure/extensions/houses");

	@ModifyVariable(method = "<init>(Lnet/minecraft/util/Identifier;Lnet/minecraft/util/Identifier;Ljava/util/List;Lnet/minecraft/structure/pool/StructurePool$Projection;)V", at = @At("HEAD"))
	private static List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> thing(List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> list, Identifier id, Identifier terminatorsId, List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> elementCounts, StructurePool.Projection projection) {
		if (id.equals(HOUSES_ID)) {
			List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> copy = new ArrayList<>(list);
			copy.add(Pair.of(StructurePoolElement.ofProcessedSingle("dimdoors:bastion/treasure/houses/bastion_gateway", StructureProcessorLists.TREASURE_ROOMS), 1));
			return copy;
		}
		return list;
	}
}
