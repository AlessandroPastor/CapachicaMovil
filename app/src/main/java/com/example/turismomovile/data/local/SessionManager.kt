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

    // Guardar el usuario en DataStore
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
            id = "",
            email = email,
            name = name,
            token = token
        )
    }


    // Limpiar la sesión
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            // Eliminamos todos los datos relevantes de la sesión
            preferences.remove(KEY_USER_EMAIL)
            preferences.remove(KEY_USER_NAME)
            preferences.remove(KEY_ACCESS_TOKEN)
            preferences[KEY_IS_LOGGED_IN] = false  // Esto marca al usuario como no autenticado
        }
        // Verificamos que la limpieza se haya realizado correctamente
        println("Session cleared: user_email, user_name, access_token, and is_logged_in")
    }


    // Verificar si el usuario está autenticado
    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.map { it[KEY_IS_LOGGED_IN] ?: false }.first()
    }

    // Guardar el estado de onboarding (si la introducción fue completada)
    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_KEY] = completed
        }
    }

    // Verificar si el onboarding fue completado
    suspend fun isOnboardingCompleted(): Boolean {
        return dataStore.data.map { it[ONBOARDING_KEY] ?: false }.first()
    }

    // Guardar el token de autenticación
    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = token
        }
    }

    // Obtener el token de autenticación
    suspend fun getAuthToken(): String? {
        val prefs = dataStore.data.first()
        return prefs[KEY_ACCESS_TOKEN]
    }
}
