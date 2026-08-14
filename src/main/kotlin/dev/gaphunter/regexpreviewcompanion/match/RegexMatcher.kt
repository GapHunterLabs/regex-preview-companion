package dev.gaphunter.regexpreviewcompanion.match

import dev.gaphunter.regexpreviewcompanion.model.MatchRange
import dev.gaphunter.regexpreviewcompanion.model.RegexOptions
import dev.gaphunter.regexpreviewcompanion.model.RegexPreviewResult
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * Pure `java.util.regex` matching, no Swing, no PSI -- fully testable
 * in isolation. An invalid pattern returns the REAL
 * [PatternSyntaxException] message, never a generic "invalid" --
 * that's specifically the honest-error behavior this plugin exists to
 * provide (an empty pattern is not an error, just "no matches yet";
 * it returns an empty, successful result).
 */
object RegexMatcher {

    fun find(patternText: String, options: RegexOptions, sampleText: String): RegexPreviewResult {
        if (patternText.isEmpty()) return RegexPreviewResult.Success(emptyList())

        val pattern = try {
            Pattern.compile(patternText, options.toJavaFlags())
        } catch (e: PatternSyntaxException) {
            return RegexPreviewResult.Error(e.description ?: e.message ?: "Invalid regex pattern")
        }

        val matcher = pattern.matcher(sampleText)
        val matches = mutableListOf<MatchRange>()
        while (matcher.find()) {
            matches += MatchRange(matcher.start(), matcher.end())
        }
        return RegexPreviewResult.Success(matches)
    }
}
