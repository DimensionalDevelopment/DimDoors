package org.dimdev.dimdoors.client.language

import net.minecraft.Util
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar
import java.util.Locale.getDefault
import java.util.function.BiConsumer

typealias TranslationMap = MutableMap<String, String>

object AutoGenDoorTranslations {
    private const val AUTOGEN_PREFIX = "autogen."
    private const val BLOCK_PREFIX_KEY = AUTOGEN_PREFIX + "block_prefix"
    private const val ITEM_PREFIX_KEY = AUTOGEN_PREFIX + "item_prefix"

    private const val DEFAULT_BLOCK_PREFIX = "Dimensional %"
    private const val DEFAULT_ITEM_PREFIX = "Dimensional "

    private val loadedTranslations: TranslationMap = mutableMapOf()
    private val generatedTranslations: TranslationMap = mutableMapOf()
    private val infoTranslations: MutableMap<String, TranslationMap> = mutableMapOf()

    @JvmStatic
    fun beginReload() {
        loadedTranslations.clear()
        generatedTranslations.clear()
        infoTranslations.clear()
    }

    @JvmStatic
    fun recordTranslation(key: String, value: String) {
        loadedTranslations[key] = value
        indexInfoTranslation(key, value)
    }

    @JvmOverloads
    fun apply(
        output: BiConsumer<String, String>,
        overwriteExisting: Boolean = false
    ) {
        applyMappings(
            output,
            DimensionalDoorBlockRegistrar.generatedBlockMappings,
            "block",
            BLOCK_PREFIX_KEY,
            DEFAULT_BLOCK_PREFIX,
            overwriteExisting,
            true
        )

        applyMappings(
            output,
            DimensionalDoorItemRegistrar.generatedItemMappings,
            "item",
            ITEM_PREFIX_KEY,
            DEFAULT_ITEM_PREFIX,
            overwriteExisting,
            false
        )
    }

    private fun applyMappings(
        output: BiConsumer<String, String>,
        mappings: MutableMap<ResourceLocation?, ResourceLocation?>,
        type: String,
        prefixKey: String?,
        fallbackPrefix: String?,
        overwriteExisting: Boolean,
        copyInfo: Boolean
    ) {
        val prefix = loadedTranslations[prefixKey].takeUnless(String?::isNullOrEmpty) ?: fallbackPrefix

        mappings.forEach { (generatedId: ResourceLocation?, originalId: ResourceLocation?) ->
            val generatedKey = Util.makeDescriptionId(type, generatedId)
            addGeneratedTranslation(
                output,
                generatedKey,
                Util.makeDescriptionId(type, originalId),
                prefix!!,
                originalId!!,
                overwriteExisting
            )
            if (copyInfo) {
                addGeneratedInfoTranslations(output, generatedKey, originalId)
            }
        }
    }

    private fun addGeneratedTranslation(
        output: BiConsumer<String, String>,
        generatedKey: String,
        originalKey: String,
        prefix: String,
        originalId: ResourceLocation,
        overwriteExisting: Boolean
    ) {
        val shorthand = loadedTranslations[originalId.autogenKey("name")]
        val existing = loadedTranslations[generatedKey]
        val previousGenerated = generatedTranslations[generatedKey]
        val value = shorthand
            ?: when {
                !overwriteExisting && existing != null && (existing != previousGenerated)
                    -> {
                    existing
                }

                else -> {
                    val originalName = loadedTranslations.getOrDefault(
                        originalKey,
                        fallbackName(originalId)
                    )

                    assemble(prefix, originalName)
                }
            }

        output.accept(generatedKey, value)
        loadedTranslations[generatedKey] = value
        generatedTranslations[generatedKey] = value
    }

    private fun addGeneratedInfoTranslations(
        output: BiConsumer<String, String>,
        generatedKey: String,
        originalId: ResourceLocation
    ) {
        val translations = infoTranslations[originalId.autogenKey("info")] ?: return

        translations.forEach { (suffix: String, value: String) ->
            val generatedInfoKey = "$generatedKey.info$suffix"
            output.accept(generatedInfoKey, value)
            loadedTranslations[generatedInfoKey] = value
            generatedTranslations[generatedInfoKey] = value
        }
    }

    private fun indexInfoTranslation(key: String, value: String) {
        when {
            !key.startsWith(AUTOGEN_PREFIX) -> {
                return
            }
            else -> {
                val infoIndex = key.lastIndexOf(".info")

                when {
                    infoIndex < 0 -> return

                    else -> {
                        var suffix = key.substring(infoIndex + ".info".length)
                        if (!suffix.isEmpty() && !suffix.all(Char::isDigit)) return

                        if (suffix == "0") suffix = ""

                        val prefix = key.substring(0, infoIndex + ".info".length)

                        infoTranslations.computeIfAbsent(prefix) { mutableMapOf() }[suffix] = value
                    }
                }
            }
        }
    }

    private fun assemble(prefix: String, originalName: String): String {
        return if (prefix.contains("%"))
            prefix.replace("%", originalName)
        else
            prefix + originalName
    }

    private fun ResourceLocation.autogenKey(suffix: String? = null): String = ("$AUTOGEN_PREFIX${this.namespace}.${this.path.replace('/', '.')}.$suffix")

    private fun fallbackName(id: ResourceLocation): String {
        val path = id.path
        val name = path.substring(path.lastIndexOf('/') + 1)
            .replace('_', ' ')
            .replace('-', ' ')
            .replace('.', ' ')

        return name.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .filter(String::isNotEmpty).joinToString(" ") {
                word: String -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }
            }
    }
}