package com.yandex.practicum.middle_homework_4.data.setting_repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yandex.practicum.middle_homework_4.ui.contract.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : SettingsRepository {
    private val REFRESH_PERIOD_KEY = longPreferencesKey("REFRESH_PERIOD")
    private val FIRST_LAUNCH_DELAY_KEY = longPreferencesKey("FIRST_LAUNCH_DELAY")
    private val _state = MutableStateFlow(SettingContainer.initial)
    override val state = _state.asStateFlow()

    init {
        CoroutineScope(Job() + dispatcher).launch {
            readSetting()
        }
    }

    override suspend fun saveSetting(periodic: Long, delayed: Long) {
        withContext(dispatcher) {
            // Реализуйте функционал записи в dataStore
            // Для periodic ключ - REFRESH_PERIOD_KEY
            // Для delayed ключ - FIRST_LAUNCH_DELAY_KEY
            // После записи данных обновите _state

            dataStore.edit { preferences: MutablePreferences ->
                preferences[REFRESH_PERIOD_KEY] = periodic
                preferences[FIRST_LAUNCH_DELAY_KEY] = delayed
                _state.update { it.copy(
                    periodic = periodic,
                    delayed = delayed
                )
                }
            }
        }
    }


    override suspend fun readSetting() {
        withContext(dispatcher){
            // Реализуйте функционал чтения данных  из dataStore.
            // Для periodic ключ - REFRESH_PERIOD_KEY, значение по умолчанию SettingContainer.DEFAULT_REFRESH_PERIOD
            // Для delayed ключ - FIRST_LAUNCH_DELAY_KEY, значение по умолчанию SettingContainer.FIST_LAUNCH_DELAY
            // После чтения данных обновите _state

            dataStore.data.collect { preferences: Preferences ->
                _state.update {
                    it.copy(
                        periodic = preferences[REFRESH_PERIOD_KEY]
                            ?: SettingContainer.DEFAULT_REFRESH_PERIOD,
                        delayed = preferences[FIRST_LAUNCH_DELAY_KEY]
                            ?: SettingContainer.FIST_LAUNCH_DELAY
                    )
                }
            }
        }
    }

}