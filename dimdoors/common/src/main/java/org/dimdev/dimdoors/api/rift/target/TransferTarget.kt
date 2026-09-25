package org.dimdev.dimdoors.api.rift.target

import org.dimdev.dimcore.api.transfer.Handle
import org.dimdev.dimcore.api.transfer.Unit

interface TransferTarget<U : Unit<U>> : Target, Handle<U>
