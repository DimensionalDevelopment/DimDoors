package org.dimdev.dimdoors.api.util;

import com.google.common.collect.Multimap;

public interface ReferenceSerializable {
	void processFlags(Multimap<String, String> flags);
}
