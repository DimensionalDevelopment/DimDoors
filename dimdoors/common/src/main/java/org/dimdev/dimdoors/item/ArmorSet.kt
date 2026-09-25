package org.dimdev.dimdoors.item

import net.minecraft.core.Holder
import net.minecraft.world.item.Item

data class ArmorSet(val helmet: Holder<Item>, val chestplate: Holder<Item>, val leggings: Holder<Item>, val boots: Holder<Item>)
