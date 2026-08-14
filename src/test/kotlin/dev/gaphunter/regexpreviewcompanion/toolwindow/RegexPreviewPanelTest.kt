package dev.gaphunter.regexpreviewcompanion.toolwindow

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Deliberately light -- the real matching logic is exhaustively
 * covered by `RegexMatcherTest` without any Swing involved. This just
 * confirms the panel wires that logic to the UI correctly: a valid
 * pattern produces the right highlight count and status text, an
 * invalid one shows the real error instead of crashing the panel.
 */
class RegexPreviewPanelTest : BasePlatformTestCase() {

    fun testValidPatternHighlightsEachMatch() {
        val panel = RegexPreviewPanel()
        panel.setSampleTextForTest("a1 b22 c333")
        panel.setPatternTextForTest("\\d+")
        panel.updateHighlights()

        assertEquals(3, panel.highlightCountForTest())
        assertEquals("3 match(es)", panel.statusTextForTest())
    }

    fun testInvalidPatternShowsTheErrorInsteadOfCrashing() {
        val panel = RegexPreviewPanel()
        panel.setSampleTextForTest("anything")
        panel.setPatternTextForTest("(unclosed")
        panel.updateHighlights()

        assertEquals(0, panel.highlightCountForTest())
        assertTrue(panel.statusTextForTest().startsWith("Invalid pattern:"))
    }

    fun testEmptyPatternHighlightsNothingWithoutError() {
        val panel = RegexPreviewPanel()
        panel.setSampleTextForTest("anything")
        panel.setPatternTextForTest("")
        panel.updateHighlights()

        assertEquals(0, panel.highlightCountForTest())
        assertFalse(panel.statusTextForTest().startsWith("Invalid pattern:"))
    }

    fun testChangingTheSampleTextRecomputesHighlights() {
        val panel = RegexPreviewPanel()
        panel.setPatternTextForTest("cat")
        panel.setSampleTextForTest("no match here")
        panel.updateHighlights()
        assertEquals(0, panel.highlightCountForTest())

        panel.setSampleTextForTest("a cat and another cat")
        panel.updateHighlights()
        assertEquals(2, panel.highlightCountForTest())
    }
}
