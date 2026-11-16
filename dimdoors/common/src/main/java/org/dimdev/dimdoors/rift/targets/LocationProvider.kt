package org.dimdev.dimdoors.rift.targets

import org.dimdev.dimdoors.util.Location

interface LocationProvider {
    val providedLocation: Location?
}