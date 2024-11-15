package org.dimdev.dimdoors;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class ModAttachmentTypes {
    public static final AttachmentType<Boolean> HAS_BEEN_LAZY_GENNED = AttachmentRegistry.<Boolean>builder().initializer(() -> false).persistent(Codec.BOOL).buildAndRegister(DimensionalDoors.id("has_been_lazy_genned"));

    public static void register() {
    }
}
