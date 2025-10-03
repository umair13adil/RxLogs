# Changelog

All notable changes to this project will be documented in this file.

## [1.0.10]
### Changed
- Log deletion now respects retention window: only logs older than `(today - logsRetentionPeriodInDays)` are removed when `autoClearLogs = true`.
- `Triggers.shouldClearLogs()` updated to use selective deletion and to respect `autoClearLogs`.

### Added
- `PLog.clearLogsOlderThan(retentionDays: Int)` helper to prune old logs without affecting recent ones.

## [1.0.9]
### Changed
- Parameter name in `LogsConfig` changed from `enabled` to `enableLogsWriteToFile`.


