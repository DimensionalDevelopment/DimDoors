package org.dimdev.dimdoors.api.client

interface UniformExt {
    fun `dimensionalDoors$set`(valueArray: IntArray?)

    companion object {
        const val UT_VEC2: Int = 11
        const val UT_VEC3: Int = 12
        const val UT_VEC4: Int = 13
        const val UT_IVEC2: Int = 14
        const val UT_IVEC3: Int = 15
        const val UT_IVEC4: Int = 16
    }
}
