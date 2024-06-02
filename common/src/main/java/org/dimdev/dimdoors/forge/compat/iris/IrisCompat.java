package org.dimdev.dimdoors.forge.compat.iris;

import net.coderbot.iris.Iris;
import org.dimdev.dimdoors.forge.client.ShaderPackDetector;

public class IrisCompat implements ShaderPackDetector {
    @Override
    public boolean shaderPackOn() {
        return Iris.getCurrentPack().isPresent();
    }
}
