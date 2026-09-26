<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Regex Preview Companion Changelog

## [Unreleased]

## [0.1.3]

### Fixed

- Review/star CTA now links to this plugin's own Marketplace
  reviews page instead of the vendor's generic plugin list.

## [0.1.2]

### Added

- Review/star CTA: after 5 debounced sessions of use (a real pause
  after typing a pattern that actually matched something -- never a
  raw keystroke), a one-time notification asks whether to rate the
  plugin on Marketplace, with a permanent "Don't ask again" option.

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

[Unreleased]: https://github.com/GapHunterLabs/regex-preview-companion/compare/0.1.3...HEAD
[0.1.3]: https://github.com/GapHunterLabs/regex-preview-companion/compare/0.1.2...0.1.3
[0.1.2]: https://github.com/GapHunterLabs/regex-preview-companion/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/GapHunterLabs/regex-preview-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/GapHunterLabs/regex-preview-companion/commits/0.1.0
