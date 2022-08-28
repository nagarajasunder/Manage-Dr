package com.geekydroid.managedr.application

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.geekydroid.managedr.ui.add_doctor.model.SortPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    @ApplicationContext private val context:Context
) {

    private val Context.datastore:DataStore<Preferences> by preferencesDataStore("USER_PREFERENCES")

    val sortPreference:Flow<String> = context.datastore.data.catch { exception ->
        if (exception is IOException)
        {
            emptyPreferences()
        }
        else
        {
            throw(exception)
        }
    }.map { preferences ->
        preferences[PreferenceKeyManager.SORT_PREFERENCES]?:SortPreferences.NEWEST_FIRST.name
    }

    suspend fun updateSortPreferences(preference:SortPreferences)
    {
        context.datastore.edit { settings ->
            settings[PreferenceKeyManager.SORT_PREFERENCES] = preference.name
        }
    }
}