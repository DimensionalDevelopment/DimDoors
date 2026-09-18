package org.dimdev.dimdoors.api.rift.target;

import org.dimdev.dimcore.api.transfer.Handle;
import org.dimdev.dimcore.api.transfer.Unit;

public interface TransferTarget<U extends Unit<U>> extends Target, Handle<U> { }
