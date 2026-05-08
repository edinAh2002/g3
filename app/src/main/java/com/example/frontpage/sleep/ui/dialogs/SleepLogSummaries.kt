package com.example.frontpage.sleep.ui.dialogs

import com.example.frontpage.sleep.model.SleepTagOption

internal fun sleepTagSummary(tags: List<SleepTagOption>): String {
    if (tags.isEmpty()) return "None"

    val visibleTags = tags.take(3).joinToString(", ") { tag ->
        tag.label
    }

    return if (tags.size > 3) {
        "$visibleTags +${tags.size - 3}"
    } else {
        visibleTags
    }
}

internal fun sleepTextSummary(text: String): String {
    return text.trim().ifBlank { "Not added" }
}
