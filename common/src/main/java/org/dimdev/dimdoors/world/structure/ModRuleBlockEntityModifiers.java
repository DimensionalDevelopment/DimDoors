package org.dimdev.dimdoors.world.structure;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.structure.processors.DestinationDataModifier;

public class ModRuleBlockEntityModifiers {
    public static DeferredRegister<RuleBlockEntityModifierType<?>> RULE_BLOCK_ENTITY_MODIFIERS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.RULE_BLOCK_ENTITY_MODIFIER);

    public static RegistrySupplier<RuleBlockEntityModifierType<DestinationDataModifier>> DESTINATION_DATA = register("destination_data", DestinationDataModifier.CODEC);

    public static <T extends RuleBlockEntityModifier> RegistrySupplier<RuleBlockEntityModifierType<T>> register(String name, Codec<T> codec) {
        return RULE_BLOCK_ENTITY_MODIFIERS.register(name, () -> () -> codec);
    }

    public static void init() {
        RULE_BLOCK_ENTITY_MODIFIERS.register();
    }
}
