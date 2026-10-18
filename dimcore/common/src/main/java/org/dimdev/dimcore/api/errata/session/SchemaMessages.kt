package org.dimdev.dimcore.api.errata.session

import net.minecraft.network.chat.Component

object SchemaMessages {
    @JvmField val CLOSED_INVALID: Component = Component.translatableWithFallback("schema.dimcore.closed.invalid", "The edited object is no longer available.")
    @JvmField val CLOSED_PERMISSION: Component = Component.translatableWithFallback("schema.dimcore.closed.permission", "You no longer have permission to edit this.")
    @JvmField val NO_PERMISSION: Component = Component.translatableWithFallback("schema.dimcore.error.permission", "No permission to change this field.")
    @JvmField val CONFLICT: Component = Component.translatableWithFallback("schema.dimcore.error.conflict", "Changed on the server since you opened it.")
    @JvmField val INVALID_REFERENCE: Component = Component.translatableWithFallback("schema.dimcore.error.reference", "Invalid reference.")
    @JvmField val UNRESOLVED: Component = Component.translatableWithFallback("schema.dimcore.error.unresolved", "Nothing valid there.")
    @JvmField val CLIENT_MISSING: Component = Component.translatableWithFallback("schema.dimcore.client_missing", "Your client needs DimCore to use this editor.")
    @JvmField val NO_RENDERER: Component = Component.translatableWithFallback("schema.dimcore.no_renderer", "The editor needs imguimc installed.")
}
