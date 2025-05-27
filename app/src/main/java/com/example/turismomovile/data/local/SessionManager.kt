package com.example.turismomovile.data.local


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.turismomovile.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SessionManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")

        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val ONBOARDING_KEY = booleanPreferencesKey("onboarding_completed")
    }

  suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_EMAIL] = user.email
            preferences[KEY_USER_NAME] = user.name
            preferences[KEY_ACCESS_TOKEN] = user.token
            preferences[KEY_IS_LOGGED_IN] = true
        }
    }

    suspend fun getUser(): User? {
        val prefs = dataStore.data.first()
        val token = prefs[KEY_ACCESS_TOKEN] ?: return null
        val email = prefs[KEY_USER_EMAIL] ?: "SinEmail"
        val name = prefs[KEY_USER_NAME] ?: "SinNombre"
        return User(
            id = "", // o puedes guardar un ID si deseas
            email = email,
            name = name,
            token = token
        )
    }

    suspend fun clearSession() {
        dataStore.edit {
            it.remove(KEY_USER_EMAIL)
            it.remove(KEY_USER_NAME)
            it.remove(KEY_ACCESS_TOKEN)
            it[KEY_IS_LOGGED_IN] = false
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.map { it[KEY_IS_LOGGED_IN] ?: false }.first()
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit {
            it[ONBOARDING_KEY] = completed
        }
    }

    suspend fun isOnboardingCompleted(): Boolean {
        return dataStore.data.map { it[ONBOARDING_KEY] ?: false }.first()
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit {
            it[KEY_ACCESS_TOKEN] = token
        }
    }

    suspend fun getAuthToken(): String? {
        val prefs = dataStore.data.first()
        return prefs[KEY_ACCESS_TOKEN]
    }
}
