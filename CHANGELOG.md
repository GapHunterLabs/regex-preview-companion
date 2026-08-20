<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Regex Preview Companion Changelog

## [Unreleased]

## [0.1.1]

### Fixed

- Tool window no longer shows the generic platform icon in the sidebar —
  the real Gap Hunter Labs mark is now declared via `icon=` on
  `<toolWindow>`.

## [0.1.0]

### Added

- **Regex Preview tool window**: pattern field + sample-text area,
  live match highlighting on every keystroke, real match count.
- Case insensitive, multiline, and dot-all flags as real checkboxes.
- Invalid patterns show the real `PatternSyntaxException` message
  instead of a silent blank.

[Unreleased]: https://github.com/GapHunterLabs/regex-preview-companion/compare/0.1.0...HEAD
[0.1.0]: https://github.com/GapHunterLabs/regex-preview-companion/commits/0.1.0
