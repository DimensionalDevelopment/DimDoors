package org.dimdev.dimdoors;

import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "attachment_types");

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> HAS_BEEN_LAZY_GENNED = ATTACHMENT_TYPES.register("has_been_lazy_genned", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }


}
