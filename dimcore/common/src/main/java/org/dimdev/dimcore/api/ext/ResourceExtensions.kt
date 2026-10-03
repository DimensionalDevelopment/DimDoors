package org.dimdev.dimcore.api.ext

import net.minecraft.resources.ResourceLocation

infix fun String.id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(this, path)
