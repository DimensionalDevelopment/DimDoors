package org.dimdev.dimcore;

import org.dimdev.dimcore.api.Platform;
import org.dimdev.dimcore.api.client.ClientPlatform;
import org.dimdev.dimcore.api.transfer.TransferBridge;

import java.util.ServiceLoader;

public final class DimCore {
    private static Platform platform;
    private static ClientPlatform clientPlatform;
    private static TransferBridge transfer;

    private DimCore() {
    }

    public static Platform platform() {
        if (platform == null) {
            platform = load(Platform.class);
        }

        return platform;
    }

    public static ClientPlatform clientPlatform() {
        if (clientPlatform == null) {
            clientPlatform = load(ClientPlatform.class);
        }

        return clientPlatform;
    }

    public static TransferBridge transfer() {
        if (transfer == null) {
            transfer = load(TransferBridge.class);
        }

        return transfer;
    }

    private static <T> T load(Class<T> type) {
        return ServiceLoader.load(type).findFirst().orElseThrow(() -> new IllegalStateException("No " + type.getSimpleName() + " implementation found"));
    }
}
