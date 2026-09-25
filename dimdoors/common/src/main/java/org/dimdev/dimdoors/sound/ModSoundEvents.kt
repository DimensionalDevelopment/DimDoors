package org.dimdev.dimdoors.sound

import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvent
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided

object ModSoundEvents {
    @JvmField
    val CRACK: SoundEvent = register("crack")
    @JvmField
    val CREEPY: SoundEvent = register("creepy")
    val DOOR_LOCKED: SoundEvent = register("door_locked")
    val DOOR_LOCK_REMOVED: SoundEvent = register("door_lock_removed")
    @JvmField
    val KEY_LOCK: SoundEvent = register("key_lock")
    @JvmField
    val KEY_UNLOCKED: SoundEvent = register("key_unlock")
    @JvmField
    val ARMOR_EQUIP_THREAD: SoundEvent = register("equip_thread")
    @JvmField
    val MONK: SoundEvent = register("monk")
    val RIFT: SoundEvent = register("rift")
    @JvmField
    val RIFT_CLOSE: SoundEvent = register("rift_close")
    val RIFT_DOOR: SoundEvent = register("rift_door")
    @JvmField
    val RIFT_END: SoundEvent = register("rift_end")
    @JvmField
    val RIFT_START: SoundEvent = register("rift_start")
    @JvmField
    val TEARING: SoundEvent = register("tearing")
    @JvmField
    val WHITE_VOID: SoundEvent = register("white_void")
    @JvmField
    val BLOOP: SoundEvent = register("bloop")
    @JvmField
    val TESSELATING_WEAVE: SoundEvent = register("tesselating_weave")
    @JvmField
    val THEY_STARE_BACK: SoundEvent = register("they_stare_back")

    private fun register(id: String): SoundEvent {
        return getSided().register<SoundEvent, SoundEvent>(
            Registries.SOUND_EVENT,
            id,
            SoundEvent.createVariableRangeEvent(DimensionalDoors.id(id))
        )
    }

    fun init() {}
}
