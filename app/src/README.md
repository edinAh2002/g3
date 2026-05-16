# Auth Package Documentation

The auth package owns login, sign up, guest access, the current-user session, and password security. It is the gatekeeper for deciding which user the rest of the app should load data for.

### Auth Entry Points

| Class | What it does | How to use it |
| --- | --- | --- |
| `AuthViewModel` | Holds auth form state and runs login, sign up, guest login, logout, and current-user loading. | Use it from auth screens when you need to update fields, submit credentials, or react to authentication events. |
| `AuthUiState` | Simple state model for auth screens. | Read it from `AuthViewModel.uiState` to render form values, loading state, errors, and the current user. |
| `AuthEvent` | One-time event type for auth navigation. | Collect it from `AuthViewModel.authEvents` when the UI needs to move past auth after success. |
| `Authenticated` | The success event emitted after login, sign up, or guest access works. | Handle it by navigating to the main app area. |

### Auth Data And Security

| Class | What it does | How to use it |
| --- | --- | --- |
| `AuthRepository` | Coordinates user lookup, sign up validation, login checks, guest account creation, and saved session updates. | Use it from `AuthViewModel`; keep auth rules here instead of spreading them through UI code. |
| `UserDao` | Room DAO for user records. | Use it through `AuthRepository` for user lookup and insert operations. |
| `User` | Room entity for one app user. | Store usernames, guest status, password hashes, password salts, and creation time with it. |
| `SecureAuthPreferences` | Encrypted preferences wrapper for the currently signed-in user id. | Use it through `AuthRepository` to save, read, or clear the active session. |
| `PasswordHasher` | PBKDF2 helper for creating salts, hashing passwords, and checking passwords safely. | Use it in auth code when creating or verifying a real user password. |

### Main Auth Compose Pieces

`AuthStartScreen` is the first auth screen and lets the user choose login, sign up, or guest access. `LoginScreen` renders the login form. `SignUpScreen` renders the account creation form. These screens should stay UI-focused and call `AuthViewModel` or callbacks for the real work.

# Shared Data Package Documentation

The shared data package owns the encrypted Room database used by the whole app. Feature packages still define their own DAOs and entities, but this package connects them into one database.

| Class | What it does | How to use it |
| --- | --- | --- |
| `AppDatabase` | The app-wide encrypted Room database for mood entries, sleep entries, and users. | Call `AppDatabase.getDatabase(context)` to get DAOs such as `moodDao()`, `sleepDao()`, and `userDao()`. |
| `DatabasePassphraseManager` | Creates and stores the SQLCipher database passphrase in encrypted preferences. | Let `AppDatabase` use it when opening the encrypted database; other code should not need it directly. |

# Sleep Package Documentation

This folder owns the sleep feature: manual sleep logging, Health Connect imports, sleep settings, sleep analytics, and the Compose UI. The notes below explain each class in plain language and how it is meant to be used.

### Feature Entry Points

| Class | What it does | How to use it |
| --- | --- | --- |
| `SleepViewModel` | Holds the sleep screen state and coordinates logs, settings, current user data, and Health Connect actions. | Use it from Compose screens when you need sleep logs, settings, import state, or actions such as add, update, delete, and import. |
| `SleepFeature` | Public facade for connecting the sleep feature to the rest of the app. | Use `MainRoute`, `HomeSummaryCard`, and `DialogHost` instead of wiring the sleep screen manually from other features. |
| `SleepFeatureController` | Small UI controller for opening and closing the sleep log dialog. | Create it with `SleepFeature.rememberController()` and pass it to `SleepFeature.MainRoute` and `SleepFeature.DialogHost`. |
| `SleepPage` | Internal enum for the sleep tabs: overview, history, insights, and settings. | Use it inside the sleep UI when adding or changing page navigation. |

### Data Layer

| Class | What it does | How to use it |
| --- | --- | --- |
| `SleepDao` | Room DAO for reading and writing `SleepEntry` rows. | Keep it behind `SleepRepository`; UI and ViewModels should not call it directly. |
| `SleepLogDataSource` | Interface for sleep log storage operations. | Depend on this in business logic when you want storage to be replaceable in tests or future data sources. |
| `SleepRepository` | Room-backed implementation of `SleepLogDataSource`. | Create it with `SleepDao`; it safely scopes reads and writes to the current user id passed in. |
| `SleepHealthDataSource` | Interface for Health Connect sleep permissions and imports. | Depend on this when code needs Health Connect behavior without caring about the concrete Android client. |
| `SleepHealthConnectManager` | Android Health Connect implementation for checking availability, permissions, and importing sleep sessions. | Use it through `SleepHealthDataSource`; it turns Health Connect sleep records into `SleepEntry` objects. |
| `SleepSettingsDataSource` | Interface for sleep goals, weekday schedules, and custom tags. | Use it from the ViewModel so settings storage can be swapped without changing UI code. |
| `SharedPreferencesSleepSettingsDataSource` | Adapter that implements `SleepSettingsDataSource` using `SleepSettingsRepository`. | Use this as the production settings data source when the app stores sleep preferences locally. |
| `SleepSettingsRepository` | SharedPreferences-based storage for sleep goals, weekday targets, date snapshots, and custom tags. | Use it through `SharedPreferencesSleepSettingsDataSource`; call it directly only for low-level settings work. |

### Domain Layer

| Class | What it does | How to use it |
| --- | --- | --- |
| `SleepCalculator` | Utility object for sleep duration, goal progress, clock formatting, consistency, and quality scoring. | Use it anywhere sleep math or display formatting is needed. |
| `SleepDateUtils` | Utility object for sleep-related date formatting and date comparisons. | Use it when filtering logs by day, week, month, or formatting dates for history. |
| `SleepDashboardState` | Complete calculated state for the main sleep dashboard. | Build it once and pass it into UI pages instead of recalculating values in Compose. |
| `SleepDashboardStateBuilder` | Converts raw sleep logs, mood logs, and goals into `SleepDashboardState`. | Use it before rendering dashboard pages or tests that need the same calculated sleep summary. |
| `SleepScoreSummary` | Small model for the sleep score card. | Use it to show a score with a title and explanation. |
| `SleepGoalBalance` | Small model for whether recent sleep is above or below the goal. | Use it in insight cards that explain goal progress over several days. |
| `SleepStreakSummary` | Small model for logged-day and near-goal streaks. | Use it when showing sleep streaks or recommendations. |
| `SleepMoodInsight` | Small model for the relationship between sleep duration and mood entries. | Use it in the insights page when enough matched mood and sleep days exist. |
| `SleepTagInsight` | Small model for simple insights based on tagged sleep logs. | Use it when showing patterns from tags such as stress, caffeine, or routine changes. |

### Models

| Class | What it does | How to use it |
| --- | --- | --- |
| `SleepEntry` | Room entity for one sleep log. | Use it whenever a saved or imported sleep record is needed. |
| `SleepLogDraft` | Temporary save payload from the sleep log dialog. | Use `toNewEntry()` for new logs or `applyTo()` when editing an existing log. |
| `SleepDefaults` | Shared default values for sleep goal, bedtime, and wake time. | Use it instead of repeating default minute values in settings or UI code. |
| `SleepHealthConnectState` | UI-friendly state for Health Connect availability, permission, import progress, and messages. | Read `canRequestPermission` and `canImport` before showing permission or import actions. |
| `HealthConnectAvailability` | Enum describing whether Health Connect is available, unavailable, or needs an update. | Use it to decide what Health Connect message or action the UI should show. |
| `SleepHistoryFilter` | Enum for history filters: all, today, this week, and this month. | Use it in the history page to filter visible sleep logs. |
| `SleepQuality` | Enum for user-rated sleep quality. | Store it on `SleepEntry` and use it for scoring, labels, and quality selection. |
| `SleepWeekday` | Enum for Monday through Sunday with labels and `Calendar` mapping. | Use it for weekday-specific goals and bedtime or wake targets. |
| `WeekdaySleepSettings` | Settings for one weekday: goal, bedtime target, and wake target. | Use it when rendering or saving per-day sleep settings. |
| `SleepCustomTag` | User-created sleep tag. | Use it when the user adds their own tags beyond the built-in list. |
| `SleepTagOption` | Display/storage model for either a built-in tag or a custom tag. | Use it in tag pickers so built-in and custom tags can share the same UI. |
| `SleepTag` | Built-in sleep tag enum plus helpers for converting tags to and from storage strings. | Use its helper methods when saving or reading tag selections. |
| `SleepSource` | Enum for where a sleep entry came from: manual, Health Connect, or wearable. | Store it on `SleepEntry` so the UI can explain the origin of a log. |
| `SnoringLevel` | Enum for snoring details on a sleep log. | Use it in the sleep details dialog and store it on `SleepEntry`. |
| `WeeklySleepChartItem` | One bar/item in the weekly sleep chart. | Build it with analytics helpers and pass it into chart components. |

### UI State Helpers

| Class | What it does | How to use it |
| --- | --- | --- |
| `SleepCalendarMonthState` | Internal model for the visible month in the sleep calendar. | Use it inside calendar components to move between months and build month labels. |
| `CalendarDayProgress` | Internal model for one day in the calendar, including duration, goal, and progress. | Use it to render a calendar day cell with the correct progress color. |
| `TimePickerTarget` | Internal enum for whether the sleep log dialog is editing bedtime or wake time. | Use it inside the log dialog when switching the inline clock picker. |
| `SleepDetailDialog` | Internal enum for which sleep detail dialog is open. | Use it inside the log dialog to show quality, snoring, tags, dream journal, or notes. |
| `ScheduleTargetPicker` | Private enum for whether the schedule target dialog is editing bedtime or wake time. | Keep it inside `EditScheduleTargetsDialog`; it is not meant to be used elsewhere. |
| `SleepSettingsDialog` | Private enum for which settings dialog is open. | Keep it inside `SleepSettingsPage`; it controls local dialog state only. |

### Main Compose Pieces

Most UI files in this package are composable functions, not classes. The main entry is `SleepScreen`, which collects ViewModel state, builds `SleepDashboardState`, and shows the sleep pages. The page composables are `SleepOverviewPage`, `SleepHistoryPage`, `SleepInsightsPage`, and `SleepSettingsPage`.

For logging sleep, start with `SleepLogDialog`. Its helper files split the dialog into smaller parts: inline time pickers, detail rows, summary text, option buttons, and focused detail dialogs for quality, snoring, tags, notes, and dream journal.

For reusable UI, use the components under `ui/components`: metric cards, insight cards, the progress calendar, page navigation, settings rows, history sections, trends, consistency, and the weekly chart. For the home screen summary, use `SleepHomeSummaryCard` through `SleepFeature.HomeSummaryCard`.

# Mood Package Documentation

This folder owns the mood feature: manual mood logging, mood scale presets, mood history, mood analytics, and the Compose UI. The notes below explain each class in plain language and how it is meant to be used.

### Feature Entry Points

| Class | What it does | How to use it |
| --- | --- | --- |
| `MoodViewModel` | Holds mood screen state and coordinates mood logs, filters, current user data, bulk deletes, and default scale settings. | Use it from Compose screens when you need mood entries, filtered history, default scale presets, or actions such as add, update, delete, and clear. |
| `MoodFeature` | Public facade for connecting the mood feature to the rest of the app. | Use `MainRoute`, `HomeSummaryCard`, and `DialogHost` instead of wiring the mood screen manually from other features. |
| `MoodFeatureController` | Small UI controller for opening and closing the mood log dialog. | Create it with `MoodFeature.rememberController()` and pass it to `MoodFeature.MainRoute` and `MoodFeature.DialogHost`. |
| `MoodPage` | Internal enum for the mood tabs: overview, history, insights, and settings. | Use it inside the mood UI when adding or changing page navigation. |

### Data Layer

| Class | What it does | How to use it |
| --- | --- | --- |
| `MoodDao` | Room DAO for reading, writing, updating, and deleting `MoodEntry` rows. | Keep it behind `MoodRepository`; UI and ViewModels should not call it directly. |
| `MoodRepository` | Room-backed mood log storage wrapper. | Create it with `MoodDao`; it safely scopes reads, writes, deletes, and bulk deletes to the current user id passed in. |

### Domain Layer

| Class | What it does | How to use it |
| --- | --- | --- |
| `MoodDateUtils` | Utility object for mood date formatting, parsing, current date/time, and date ranges. | Use it when logging a mood date, building calendar state, or formatting dates for history. |
| `MoodLabelUtils` | Utility object for turning numeric mood values into labels, descriptions, averages, and change text. | Use it whenever a mood score needs to be displayed with the active scale preset. |
| `MoodStatsCalculator` | Utility object for mood averages, best and lowest moods, daily averages, counts, and common mood values. | Use it anywhere mood math or simple mood summaries are needed. |
| `MoodDashboardState` | Complete calculated state for the main mood dashboard. | Build it once and pass it into UI pages instead of recalculating values in Compose. |
| `MoodDashboardStateBuilder` | Converts raw mood logs and the active scale preset into `MoodDashboardState`. | Use it before rendering dashboard pages or tests that need the same calculated mood summary. |
| `MoodScoreSummary` | Small model for the mood score card. | Use it to show a score with a title and explanation. |
| `MoodMomentumSummary` | Small model for whether recent mood is up, down, stable, or needs more logs. | Use it in insight cards that compare the last 7 days with the previous 7 days. |
| `MoodStreakSummary` | Small model for logged-day and positive-mood streaks. | Use it when showing mood streaks or recommendations. |
| `MoodPatternInsight` | Small model for simple insights based on repeated mood values. | Use it when showing the most common saved mood. |
| `MoodNoteInsight` | Small model for note coverage across mood logs. | Use it when showing how often the user adds context to mood logs. |

### Models

| Class | What it does | How to use it |
| --- | --- | --- |
| `MoodEntry` | Room entity for one mood log. | Use it whenever a saved mood record is needed. |
| `MoodLogDraft` | Temporary save payload from the mood log dialog. | Use `toNewEntry()` for new logs or `applyTo()` when editing an existing log. |
| `MoodFeelingFilter` | Enum for mood history filters such as all moods, sad, calm, content, or joyful. | Use it to filter history without changing the selected calendar date. |
| `MoodLogFilterState` | UI state model for the active mood history filter. | Read it from `MoodViewModel.filterState` when rendering the history filter button. |
| `MoodScalePreset` | Enum for the available 1-to-5 mood label presets, including feelings, classic, and emoji styles. | Use it when rendering mood options, saving the default scale, or displaying mood labels. |
| `MoodScaleOption` | Display model for one value inside a mood scale preset. | Use it when building option buttons in the mood log dialog or previewing a scale in settings. |
| `WeeklyMoodChartItem` | One bar/item in the weekly mood chart. | Build it with analytics helpers and pass it into chart components. |

### UI State Helpers

| Class | What it does | How to use it |
| --- | --- | --- |
| `MoodCalendarMonthState` | Internal model for the visible month in the mood calendar. | Use it inside calendar components to move between months and build month labels. |
| `CalendarDayMood` | Internal model for one day in the calendar, including average mood, entry count, and progress. | Use it to render a calendar day cell with the correct mood progress color. |
| `MoodSettingsDialog` | Private enum for which settings dialog is open. | Keep it inside `MoodSettingsPage`; it controls local dialog state only. |

### Main Compose Pieces

Most UI files in this package are composable functions, not classes. The main entry is `MoodScreen`, which collects ViewModel state, builds `MoodDashboardState`, and shows the mood pages. The page composables are `MoodOverviewPage`, `MoodHistoryPage`, `MoodInsightsPage`, and `MoodSettingsPage`.

For logging mood, start with `MoodLogDialog`. It lets the user choose a date, choose the scale preset for the current log, select the mood value, and add an optional note. `MoodOptionButton` renders each preset option, while `MoodScalePresetDialog`, `MoodScaleDialog`, and the history delete dialogs handle focused popup flows.

For reusable UI, use the components under `ui/components`: metric cards, insight cards, the progress calendar, page navigation, settings rows, history cards, section headers, and the weekly chart. For the home screen summary, use `MoodHomeSummaryCard` through `MoodFeature.HomeSummaryCard`.
