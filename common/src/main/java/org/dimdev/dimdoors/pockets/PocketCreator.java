package org.dimdev.dimdoors.pockets;

import org.dimdev.dimdoors.world.pocket.type.Pocket;

public interface PocketCreator {
    Pocket prepareAndPlacePocket(PocketGenerationContext parameters);

    Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Boolean setupLoot);
}
