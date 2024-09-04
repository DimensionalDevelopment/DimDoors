package org.dimdev.dimdoors.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
<<<<<<< HEAD:fabric/src/main/java/org/dimdev/dimdoors/fabric/DimensionalDoorsFabric.java
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.StreamUtils;

import static org.dimdev.dimdoors.forge.world.feature.ModFeatures.Placed.*;

=======
import org.dimdev.dimdoors.api.util.StreamUtils;

>>>>>>> merge-branch:fabric/src/main/java/org/dimdev/dimdoors/DimensionalDoorsFabric.java
public class DimensionalDoorsFabric implements ModInitializer {

	@Override
    public void onInitialize() {
		StreamUtils.setup(this);
        DimensionalDoors.init();

		PlayerBlockBreakEvents.AFTER.register(DimensionalDoors::afterBlockBreak);
    }
}
