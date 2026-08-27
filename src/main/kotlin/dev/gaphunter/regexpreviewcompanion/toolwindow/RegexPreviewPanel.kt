package dev.gaphunter.regexpreviewcompanion.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.Alarm
import dev.gaphunter.regexpreviewcompanion.match.RegexMatcher
import dev.gaphunter.regexpreviewcompanion.model.RegexOptions
import dev.gaphunter.regexpreviewcompanion.model.RegexPreviewResult
import dev.gaphunter.regexpreviewcompanion.review.ReviewPrompt
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.BadLocationException
import javax.swing.text.DefaultHighlighter

/**
 * The whole plugin's UI, self-contained -- never hooks into the IDE's
 * own highlighting/inspection pipeline, unlike an inline-editor-hint
 * approach would. Every match is recomputed and re-highlighted on
 * every keystroke; this is deliberately cheap enough that no
 * debouncing is needed (a single `Pattern.compile` + `Matcher.find()`
 * loop against typically-short sample text, not a bundler invocation
 * or anything else genuinely expensive).
 */
class RegexPreviewPanel(private val project: Project? = null) : JPanel(BorderLayout()) {

    private val patternField = JBTextField()
    private val caseInsensitiveCheckbox = JBCheckBox("Case insensitive")
    private val multilineCheckbox = JBCheckBox("Multiline (^\$ match line boundaries)")
    private val dotAllCheckbox = JBCheckBox("Dot matches newline")
    private val sampleTextArea = JBTextArea(12, 60).apply { lineWrap = true; wrapStyleWord = true }
    private val statusLabel = JBLabel(" ")
    private val highlightPainter = DefaultHighlighter.DefaultHighlightPainter(JBColor(Color(255, 235, 59, 120), Color(255, 235, 59, 90)))

    // Live-per-keystroke recompute (see the class doc above) is exactly the
    // "never count a keystroke" case the CTA design warns about -- a
    // separate debounce, only for the CTA signal, turns that continuous
    // stream into one discrete "the user paused after typing something
    // that produced a real match" event, ~800ms after the last edit.
    // No parent Disposable is passed (this panel isn't one).
    private val reviewAlarm = Alarm(Alarm.ThreadToUse.SWING_THREAD, null)

    init {
        val topPanel = JPanel(BorderLayout())
        topPanel.add(JBLabel("Pattern: "), BorderLayout.WEST)
        topPanel.add(patternField, BorderLayout.CENTER)

        val flagsPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        flagsPanel.add(caseInsensitiveCheckbox)
        flagsPanel.add(multilineCheckbox)
        flagsPanel.add(dotAllCheckbox)

        val headerPanel = JPanel(BorderLayout())
        headerPanel.add(topPanel, BorderLayout.NORTH)
        headerPanel.add(flagsPanel, BorderLayout.SOUTH)
        headerPanel.border = BorderFactory.createEmptyBorder(8, 8, 4, 8)

        sampleTextArea.border = BorderFactory.createEmptyBorder(4, 4, 4, 4)
        statusLabel.border = BorderFactory.createEmptyBorder(4, 8, 8, 8)

        add(headerPanel, BorderLayout.NORTH)
        add(JBScrollPane(sampleTextArea), BorderLayout.CENTER)
        add(statusLabel, BorderLayout.SOUTH)

        val listener = object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = updateHighlights()
            override fun removeUpdate(e: DocumentEvent) = updateHighlights()
            override fun changedUpdate(e: DocumentEvent) = updateHighlights()
        }
        patternField.document.addDocumentListener(listener)
        sampleTextArea.document.addDocumentListener(listener)
        caseInsensitiveCheckbox.addActionListener { updateHighlights() }
        multilineCheckbox.addActionListener { updateHighlights() }
        dotAllCheckbox.addActionListener { updateHighlights() }

        updateHighlights()
    }

    /** Exposed for tests -- runs the exact same path the UI listeners trigger, without needing a real keystroke event. */
    fun updateHighlights() {
        sampleTextArea.highlighter.removeAllHighlights()
        val options = RegexOptions(caseInsensitiveCheckbox.isSelected, multilineCheckbox.isSelected, dotAllCheckbox.isSelected)

        when (val result = RegexMatcher.find(patternField.text, options, sampleTextArea.text)) {
            is RegexPreviewResult.Success -> {
                for (match in result.matches) {
                    if (match.start >= match.end) continue // a zero-width match has nothing to visually highlight
                    try {
                        sampleTextArea.highlighter.addHighlight(match.start, match.end, highlightPainter)
                    } catch (_: BadLocationException) {
                        // Offsets came from matching this exact text moments ago; a
                        // concurrent edit could theoretically race this, in which case
                        // skipping this one highlight is harmless -- the next keystroke
                        // recomputes everything from the current text anyway.
                    }
                }
                statusLabel.text = "${result.matches.size} match(es)"
                statusLabel.foreground = JBColor.foreground()
                if (result.matches.isNotEmpty()) scheduleReviewHit()
            }
            is RegexPreviewResult.Error -> {
                statusLabel.text = "Invalid pattern: ${result.message}"
                statusLabel.foreground = JBColor.RED
            }
        }
    }

    /**
     * Debounced ~800ms after the last edit -- reset on every call, so a
     * user who keeps typing (still producing matches on every keystroke)
     * never fires this repeatedly; only a real pause after a successful
     * pattern counts as one real "session of use".
     */
    private fun scheduleReviewHit() {
        reviewAlarm.cancelAllRequests()
        reviewAlarm.addRequest({ ReviewPrompt.recordHit(project) }, 800)
    }

    // Exposed for tests only.
    fun setPatternTextForTest(text: String) { patternField.text = text }
    fun setSampleTextForTest(text: String) { sampleTextArea.text = text }
    fun statusTextForTest(): String = statusLabel.text
    fun highlightCountForTest(): Int = sampleTextArea.highlighter.highlights.size
}
