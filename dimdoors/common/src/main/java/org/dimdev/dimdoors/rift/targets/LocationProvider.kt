package org.dimdev.dimdoors.rift.targets

import org.dimdev.dimdoors.api.util.Location

interface LocationProvider {
    val providedLocation: Location
}