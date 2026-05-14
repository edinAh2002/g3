package com.example.frontpage.theme.data

import android.content.Context
import com.example.frontpage.theme.domain.PageThemeCatalog
import com.example.frontpage.theme.model.PageThemeCustomPresetDraft
import com.example.frontpage.theme.model.PageThemePreference
import com.example.frontpage.theme.model.PageThemePreferenceEntry
import com.example.frontpage.theme.model.PageThemePreset
import com.example.frontpage.theme.model.PageThemePresetDescriptor
import com.example.frontpage.theme.model.PageThemePresetId
import com.example.frontpage.theme.model.PageThemeStoredState
import com.example.frontpage.theme.model.PageThemeTargetKey
import com.example.frontpage.theme.model.toCustomPresetEntity
import com.example.frontpage.theme.model.toPreset
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

interface PageThemeDataSource {
    fun observeThemeState(userId: Long?): Flow<PageThemeStoredState>

    suspend fun updateThemePresetId(
        userId: Long?,
        target: PageThemeTargetKey,
        presetId: PageThemePresetId
    ): PageThemePresetId

    suspend fun createCustomPreset(
        userId: Long?,
        target: PageThemeTargetKey,
        draft: PageThemeCustomPresetDraft
    ): PageThemePreset?

    suspend fun updateCustomPreset(
        userId: Long?,
        target: PageThemeTargetKey,
        presetId: PageThemePresetId,
        draft: PageThemeCustomPresetDraft
    ): PageThemePreset?

    suspend fun deleteCustomPreset(
        userId: Long?,
        target: PageThemeTargetKey,
        presetId: PageThemePresetId
    ): PageThemePresetId
}

class PageThemeRepository(
    context: Context,
    private val themeDao: PageThemeDao,
    private val catalog: PageThemeCatalog = PageThemeCatalog.Default
) : PageThemeDataSource {
    private val appContext = context.applicationContext
    private val pageThemePreferences = appContext.getSharedPreferences(
        SharedPageThemeStorage.PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val legacySleepPreferences = appContext.getSharedPreferences(
        LegacySleepThemeStorage.PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    override fun observeThemeState(userId: Long?): Flow<PageThemeStoredState> {
        if (userId == null) {
            return flowOf(defaultStoredState())
        }

        return flow {
            migrateSharedPreferencesToRoom(userId)

            emitAll(
                combine(
                    themeDao.observePreferencesForUser(userId),
                    themeDao.observeCustomPresetsForUser(userId)
                ) { preferenceEntries, customThemeEntries ->
                    val customPresets = customThemeEntries
                        .map { entity -> entity.toPreset() }
                        .groupBy { preset -> preset.descriptor.target }

                    PageThemeStoredState(
                        preferences = storedPreferences(
                            preferenceEntries = preferenceEntries,
                            customPresets = customPresets
                        ),
                        customPresets = customPresets
                    )
                }
            )
        }
    }

    override suspend fun updateThemePresetId(
        userId: Long?,
        target: PageThemeTargetKey,
        presetId: PageThemePresetId
    ): PageThemePresetId {
        if (userId == null) return catalog.configFor(target).defaultPresetId

        themeDao.upsertPreference(
            PageThemePreferenceEntry(
                userId = userId,
                target = target.storageValue,
                presetId = presetId.storageValue,
                updatedAtMillis = System.currentTimeMillis()
            )
        )

        return presetId
    }

    override suspend fun createCustomPreset(
        userId: Long?,
        target: PageThemeTargetKey,
        draft: PageThemeCustomPresetDraft
    ): PageThemePreset? {
        if (userId == null) return null

        val now = System.currentTimeMillis()
        val preset = PageThemePreset(
            descriptor = PageThemePresetDescriptor(
                target = target,
                id = PageThemePresetId(customPresetId(target)),
                displayName = draft.displayName.trim().ifBlank {
                    "Custom ${catalog.configFor(target).displayName}"
                },
                description = draft.description.trim().ifBlank {
                    "Custom ${catalog.configFor(target).displayName} theme."
                },
                isCustom = true
            ),
            colors = draft.colors,
            layoutStyle = draft.layoutStyle
        )

        themeDao.upsertCustomPreset(
            preset.toCustomPresetEntity(
                userId = userId,
                createdAtMillis = now,
                updatedAtMillis = now
            )
        )
        themeDao.upsertPreference(
            PageThemePreferenceEntry(
                userId = userId,
                target = target.storageValue,
                presetId = preset.descriptor.id.storageValue,
                updatedAtMillis = now
            )
        )

        return preset
    }

    override suspend fun updateCustomPreset(
        userId: Long?,
        target: PageThemeTargetKey,
        presetId: PageThemePresetId,
        draft: PageThemeCustomPresetDraft
    ): PageThemePreset? {
        if (userId == null || catalog.supportsBuiltInPreset(target, presetId)) return null

        val existingEntry = themeDao.getCustomPreset(
            userId = userId,
            target = target.storageValue,
            id = presetId.storageValue
        ) ?: return null

        val now = System.currentTimeMillis()
        val existingPreset = existingEntry.toPreset()
        val preset = existingPreset.copy(
            descriptor = existingPreset.descriptor.copy(
                displayName = draft.displayName.trim().ifBlank {
                    existingPreset.descriptor.displayName
                },
                description = draft.description.trim().ifBlank {
                    existingPreset.descriptor.description
                }
            ),
            colors = draft.colors,
            layoutStyle = draft.layoutStyle
        )

        themeDao.upsertCustomPreset(
            preset.toCustomPresetEntity(
                userId = userId,
                createdAtMillis = existingEntry.createdAtMillis,
                updatedAtMillis = now
            )
        )

        return preset
    }

    override suspend fun deleteCustomPreset(
        userId: Long?,
        target: PageThemeTargetKey,
        presetId: PageThemePresetId
    ): PageThemePresetId {
        val defaultPresetId = catalog.configFor(target).defaultPresetId
        if (userId == null || catalog.supportsBuiltInPreset(target, presetId)) return defaultPresetId

        themeDao.deleteCustomPreset(
            userId = userId,
            target = target.storageValue,
            id = presetId.storageValue
        )

        val storedPresetId = themeDao.getPreferencePresetId(
            userId = userId,
            target = target.storageValue
        )

        if (storedPresetId == presetId.storageValue) {
            themeDao.upsertPreference(
                PageThemePreferenceEntry(
                    userId = userId,
                    target = target.storageValue,
                    presetId = defaultPresetId.storageValue,
                    updatedAtMillis = System.currentTimeMillis()
                )
            )
        }

        return defaultPresetId
    }

    private fun storedPreferences(
        preferenceEntries: List<PageThemePreferenceEntry>,
        customPresets: Map<PageThemeTargetKey, List<PageThemePreset>>
    ): Map<PageThemeTargetKey, PageThemePreference> {
        return catalog.targetConfigs.associate { config ->
            val storedPresetId = preferenceEntries
                .firstOrNull { entity -> entity.target == config.target.storageValue }
                ?.presetId
                ?.let { storedValue -> PageThemePresetId.fromStorageValue(storedValue) }

            val presetId = resolvePresetId(
                target = config.target,
                presetId = storedPresetId ?: config.defaultPresetId,
                customPresets = customPresets
            )

            config.target to PageThemePreference(
                target = config.target,
                presetId = presetId
            )
        }
    }

    private fun resolvePresetId(
        target: PageThemeTargetKey,
        presetId: PageThemePresetId,
        customPresets: Map<PageThemeTargetKey, List<PageThemePreset>>
    ): PageThemePresetId {
        if (catalog.supportsBuiltInPreset(target, presetId)) {
            return presetId
        }

        val customPresetExists = customPresets[target].orEmpty().any { preset ->
            preset.descriptor.id == presetId
        }

        return if (customPresetExists) {
            presetId
        } else {
            catalog.configFor(target).defaultPresetId
        }
    }

    private suspend fun migrateSharedPreferencesToRoom(userId: Long) {
        catalog.targetConfigs.forEach { config ->
            val existingPresetId = themeDao.getPreferencePresetId(
                userId = userId,
                target = config.target.storageValue
            )

            if (existingPresetId != null) return@forEach

            val sharedPresetId = pageThemePreferences.getString(
                SharedPageThemeStorage.themePreset(
                    userId = userId,
                    target = config.target
                ),
                null
            )
            val legacyPresetId = migratedLegacySleepPresetId(
                userId = userId,
                target = config.target
            )
            val migratedPresetId = PageThemePresetId.fromStorageValue(
                sharedPresetId ?: legacyPresetId?.storageValue
            )
            val presetId = if (catalog.supportsBuiltInPreset(config.target, migratedPresetId)) {
                migratedPresetId
            } else {
                config.defaultPresetId
            }

            if (sharedPresetId != null || legacyPresetId != null) {
                themeDao.upsertPreference(
                    PageThemePreferenceEntry(
                        userId = userId,
                        target = config.target.storageValue,
                        presetId = presetId.storageValue,
                        updatedAtMillis = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    private fun migratedLegacySleepPresetId(
        userId: Long,
        target: PageThemeTargetKey
    ): PageThemePresetId? {
        if (target != PageThemeTargetKey.Sleep) return null

        val storedPresetId = legacySleepPreferences.getString(
            LegacySleepThemeStorage.themePreset(userId),
            null
        ) ?: return null

        return PageThemePresetId.fromStorageValue(storedPresetId)
    }

    private fun defaultStoredState(): PageThemeStoredState {
        return PageThemeStoredState(
            preferences = catalog.targetConfigs.associate { config ->
                config.target to PageThemePreference(
                    target = config.target,
                    presetId = config.defaultPresetId
                )
            },
            customPresets = emptyMap()
        )
    }

    private fun customPresetId(target: PageThemeTargetKey): String {
        return "custom_${target.storageValue}_${UUID.randomUUID()}"
    }
}

private object SharedPageThemeStorage {
    const val PREFERENCES_NAME = "page_theme_settings"

    private const val THEME_PRESET_KEY_PREFIX = "theme_preset_user_"

    fun themePreset(
        userId: Long,
        target: PageThemeTargetKey
    ): String {
        return "$THEME_PRESET_KEY_PREFIX${userId}_${target.storageValue}"
    }
}

private object LegacySleepThemeStorage {
    const val PREFERENCES_NAME = "sleep_settings"

    private const val THEME_PRESET_KEY_PREFIX = "theme_preset_user_"

    fun themePreset(userId: Long): String {
        return "$THEME_PRESET_KEY_PREFIX${userId}_${PageThemeTargetKey.Sleep.storageValue}"
    }
}
