package org.dimdev.dimcore.api;

public interface ModCommon<T extends ISided<?>> {
    void init(T sided);
	String getModId();
}
