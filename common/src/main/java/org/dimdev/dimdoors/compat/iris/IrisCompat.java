package org.dimdev.dimdoors.compat.iris;

import net.irisshaders.iris.Iris;
import org.dimdev.dimdoors.client.ShaderPackDetector;

public class IrisCompat implements ShaderPackDetector {
    @Override
    public boolean shaderPackOn() {
        return Iris.getCurrentPack().isPresent();
    }
}
