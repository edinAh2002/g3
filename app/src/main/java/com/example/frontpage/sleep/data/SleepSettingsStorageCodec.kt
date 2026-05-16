package com.example.frontpage.sleep.data

import android.net.Uri
import com.example.frontpage.sleep.model.SleepCustomTag
import com.example.frontpage.sleep.model.SleepPageSectionId

internal object SleepSettingsStorageCodec {
    private const val CUSTOM_TAG_SEPARATOR = "|"
    private const val PAGE_LAYOUT_SEPARATOR = ","

    fun encodeCustomTags(tags: List<SleepCustomTag>): String {
        return tags.joinToString("\n") { tag ->
            "${tag.id}$CUSTOM_TAG_SEPARATOR${Uri.encode(tag.label)}"
        }
    }

    fun decodeCustomTags(storedTags: String): List<SleepCustomTag> {
        if (storedTags.isBlank()) return emptyList()

        return storedTags.lines()
            .mapNotNull { line -> decodeCustomTag(line) }
    }

    fun encodePageSectionIds(sectionIds: List<SleepPageSectionId>): String {
        return sectionIds.joinToString(PAGE_LAYOUT_SEPARATOR) { sectionId ->
            sectionId.storageValue
        }
    }

    fun decodePageSectionIds(storedLayout: String): List<SleepPageSectionId> {
        if (storedLayout.isBlank()) return emptyList()

        return storedLayout
            .split(PAGE_LAYOUT_SEPARATOR)
            .mapNotNull { value ->
                SleepPageSectionId.fromStorageValue(value.trim())
            }
            .distinct()
    }

    private fun decodeCustomTag(line: String): SleepCustomTag? {
        val parts = line.split(CUSTOM_TAG_SEPARATOR, limit = 2)
        val id = parts.getOrNull(0)?.takeIf { value -> value.isNotBlank() }
            ?: return null

        val label = parts.getOrNull(1)
            ?.let { encodedLabel -> Uri.decode(encodedLabel) }
            ?.takeIf { decodedLabel -> decodedLabel.isNotBlank() }
            ?: return null

        return SleepCustomTag(
            id = id,
            label = label
        )
    }
}
