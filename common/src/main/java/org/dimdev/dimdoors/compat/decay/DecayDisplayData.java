package org.dimdev.dimdoors.compat.decay;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import org.dimdev.dimdoors.world.decay.DecayPatternHolder;
import org.dimdev.dimdoors.world.decay.pattern.CompoundDecayPattern;
import org.dimdev.dimdoors.world.decay.results.DecayResult;

import java.util.List;
import java.util.stream.Stream;

public record DecayDisplayData(Identifier id, Object input, List<DecayResult.Result> outputs) {
    public static Stream<DecayDisplayData> list(DecayPatternHolder patternHolder, RegistryAccess registryAccess) {
        List<DecayResult.Result> outputs = patternHolder.value() instanceof CompoundDecayPattern compoundPattern
                ? compoundPattern.result().produces()
                : List.of();

        if (outputs.isEmpty()) {
            return Stream.empty();
        }

        return patternHolder.value()
                .constructApplicable(registryAccess)
                .distinct()
                .map(input -> new DecayDisplayData(patternHolder.id(), input, outputs));
    }
}
