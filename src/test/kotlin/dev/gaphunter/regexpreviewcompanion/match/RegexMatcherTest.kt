package dev.gaphunter.regexpreviewcompanion.match

import dev.gaphunter.regexpreviewcompanion.model.MatchRange
import dev.gaphunter.regexpreviewcompanion.model.RegexOptions
import dev.gaphunter.regexpreviewcompanion.model.RegexPreviewResult
import junit.framework.TestCase

class RegexMatcherTest : TestCase() {

    fun testFindsASimpleLiteralMatch() {
        val result = RegexMatcher.find("cat", RegexOptions(), "the cat sat")
        assertEquals(RegexPreviewResult.Success(listOf(MatchRange(4, 7))), result)
    }

    fun testFindsMultipleMatches() {
        val result = RegexMatcher.find("\\d+", RegexOptions(), "a1 b22 c333") as RegexPreviewResult.Success
        assertEquals(3, result.matches.size)
    }

    fun testEmptyPatternMatchesNothingAndIsNotAnError() {
        val result = RegexMatcher.find("", RegexOptions(), "anything")
        assertEquals(RegexPreviewResult.Success(emptyList()), result)
    }

    fun testInvalidPatternReturnsTheRealSyntaxExceptionMessage() {
        val result = RegexMatcher.find("(unclosed", RegexOptions(), "text") as RegexPreviewResult.Error
        assertTrue(result.message.isNotBlank())
    }

    fun testCaseInsensitiveFlagIsRespected() {
        val insensitive = RegexMatcher.find("CAT", RegexOptions(caseInsensitive = true), "the cat sat") as RegexPreviewResult.Success
        assertEquals(1, insensitive.matches.size)

        val sensitive = RegexMatcher.find("CAT", RegexOptions(caseInsensitive = false), "the cat sat") as RegexPreviewResult.Success
        assertEquals(0, sensitive.matches.size)
    }

    fun testMultilineFlagChangesAnchorBehavior() {
        val text = "line1\nline2\nline3"
        val withoutMultiline = RegexMatcher.find("^line", RegexOptions(multiline = false), text) as RegexPreviewResult.Success
        val withMultiline = RegexMatcher.find("^line", RegexOptions(multiline = true), text) as RegexPreviewResult.Success

        assertEquals(1, withoutMultiline.matches.size)
        assertEquals(3, withMultiline.matches.size)
    }

    fun testDotAllFlagLetsDotMatchNewlines() {
        val text = "a\nb"
        val withoutDotAll = RegexMatcher.find("a.b", RegexOptions(dotAll = false), text) as RegexPreviewResult.Success
        val withDotAll = RegexMatcher.find("a.b", RegexOptions(dotAll = true), text) as RegexPreviewResult.Success

        assertEquals(0, withoutDotAll.matches.size)
        assertEquals(1, withDotAll.matches.size)
    }

    fun testNoMatchesInSampleTextIsAValidEmptySuccess() {
        val result = RegexMatcher.find("zzz", RegexOptions(), "abc")
        assertEquals(RegexPreviewResult.Success(emptyList()), result)
    }

    fun testZeroWidthMatchesDoNotHangOrCrash() {
        // A pattern that can match an empty string at every position --
        // Matcher.find() must terminate (it does, by contract, advancing
        // past a zero-width match); this pins that it really does.
        val result = RegexMatcher.find("a*", RegexOptions(), "bbb") as RegexPreviewResult.Success
        assertTrue(result.matches.isNotEmpty())
    }
}
