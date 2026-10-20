package org.dimdev.dimdoors.item

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Equipable
import net.minecraft.world.item.Item

class MaskItem(properties: Properties) : Item(properties), Equipable {
    override fun getEquipmentSlot() = EquipmentSlot.HEAD
}
