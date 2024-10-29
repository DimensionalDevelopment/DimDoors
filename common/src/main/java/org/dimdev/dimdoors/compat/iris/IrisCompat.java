package org.dimdev.dimdoors.compat.iris;

import net.irisshaders.iris.api.v0.IrisApi;
import org.dimdev.dimdoors.client.ShaderPackDetector;

public class IrisCompat implements ShaderPackDetector {
    @Override
    public boolean shaderPackOn() {
        return IrisApi.getInstance().isShaderPackInUse();
    }
}
