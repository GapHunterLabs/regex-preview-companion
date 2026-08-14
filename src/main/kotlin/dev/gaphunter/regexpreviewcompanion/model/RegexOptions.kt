package dev.gaphunter.regexpreviewcompanion.model

import java.util.regex.Pattern

data class RegexOptions(
    val caseInsensitive: Boolean = false,
    val multiline: Boolean = false,
    val dotAll: Boolean = false,
) {
    fun toJavaFlags(): Int {
        var flags = 0
        if (caseInsensitive) flags = flags or Pattern.CASE_INSENSITIVE
        if (multiline) flags = flags or Pattern.MULTILINE
        if (dotAll) flags = flags or Pattern.DOTALL
        return flags
    }
}

data class MatchRange(val start: Int, val end: Int)

sealed class RegexPreviewResult {
    data class Success(val matches: List<MatchRange>) : RegexPreviewResult()
    data class Error(val message: String) : RegexPreviewResult()
}
