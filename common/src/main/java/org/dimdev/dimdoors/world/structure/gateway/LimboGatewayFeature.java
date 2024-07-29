//package org.dimdev.dimdoors.world.structure.gateway;
//
//import net.minecraft.world.level.levelgen.feature.Feature;
//import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
//import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
//import net.minecraft.world.level.levelgen.structure.Structure;
//import net.minecraft.world.level.levelgen.structure.StructureType;
//
//import java.util.Optional;
//
//public class LimboGatewayFeature extends Structure<NoneFeatureConfiguration> {
//    public LimboGatewayFeature() {
//        super(NoneFeatureConfiguration.CODEC);
//    }
//
//	@Override
//	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
//		org.dimdev.dimdoors.world.feature.gateway.LimboGateway.INSTANCE.generate(context.level(), context.origin());
//		return true;
//
//	}
//
//	@Override
//	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
//		return Optional.empty();
//	}
//
//	@Override
//	public StructureType<?> type() {
//		return null;
//	}
//}
