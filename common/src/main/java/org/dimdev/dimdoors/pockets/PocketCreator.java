package org.dimdev.dimdoors.pockets;

import java.util.UUID;

public interface PocketCreator {
    UUID prepareAndPlacePocket(PocketGenerationContext parameters);

    UUID prepareAndPlacePocket(PocketGenerationContext parameters, Boolean setupLoot);
}
