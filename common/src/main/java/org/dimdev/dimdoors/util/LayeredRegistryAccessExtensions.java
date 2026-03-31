package org.dimdev.dimdoors.util;

import net.minecraft.core.RegistryAccess;

import java.util.List;

public interface LayeredRegistryAccessExtensions<T> {
    RegistryAccess.Frozen getComposite();

    void setValues(List<RegistryAccess.Frozen> list);
}
