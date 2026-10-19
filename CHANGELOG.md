# Changelog

All notable changes to the Reminder app are documented below.

## [2.0.0] - One-Time Reminder Architecture

### Added
- Dynamic home screen header displaying overdue counts (`X overdue reminders`) and idle state (`No reminders due now`).
- Relative-time capsule on future reminder cards (`in 2 mins`, `in 3 days`, etc.).
- Explicit one-time lifecycle: reminders fire once and transition cleanly to `isCompleted = true`.
- Split Date and Time pickers replacing combined controls.
- Keyboard auto-capitalization (sentence style) for title and description fields.
- Device-protected JSON storage schema compatibility with one-time reminder attributes.
- Unit tests covering relative-time formatting, overdue calculations, and sorting.

### Changed
- App renamed from "reReminder" to "Reminder" with modern bell/check adaptive icon.
- Cards refactored: left icon indicates pending vs. completed state; right toggle controls alarm enablement.
- Ringing screen visual hierarchy updated: prominent title, secondary description, subdued time, and bottom-anchored Snooze/Dismiss actions.
- Completed reminders grouped at the bottom of the list and sorted by latest update timestamp.

### Removed
- Recurring scheduling, repeat interval selectors, and "Runs at any time" badges.
- Obsolete sound/vibrate toggle card from the home screen.
- "Insert value" dropdown in the reminder editor.
