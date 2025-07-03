package com.example.turismomovile.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.turismomovile.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SessionManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        // 🔹 Definición completa y organizada de todas las claves necesarias
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_LAST_NAME = stringPreferencesKey("user_last_name")
        private val KEY_USER_FULL_NAME = stringPreferencesKey("user_full_name")
        private val KEY_USER_USERNAME = stringPreferencesKey("user_username")
        private val KEY_USER_CODE = stringPreferencesKey("user_code")
        private val KEY_USER_IMAGEN_URL = stringPreferencesKey("user_imagen_url")
        private val KEY_USER_ROLES = stringPreferencesKey("user_roles")
        private val KEY_USER_PERMISSIONS = stringPreferencesKey("user_permissions")
        private val KEY_USER_CREATED_AT = stringPreferencesKey("user_created_at")
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val ONBOARDING_KEY = booleanPreferencesKey("onboarding_completed")
    }

    // ✅ Guardar toda la información relevante del usuario
    suspend fun saveUser(user: User) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = user.id
            prefs[KEY_USER_EMAIL] = user.email
            prefs[KEY_USER_NAME] = user.name ?: ""
            prefs[KEY_USER_LAST_NAME] = user.last_name
            prefs[KEY_USER_FULL_NAME] = user.fullName ?: ""
            prefs[KEY_USER_USERNAME] = user.username
            prefs[KEY_USER_CODE] = user.code ?: ""
            prefs[KEY_USER_IMAGEN_URL] = user.imagenUrl ?: ""
            prefs[KEY_USER_ROLES] = user.roles.joinToString(",")
            prefs[KEY_USER_PERMISSIONS] = user.permissions.joinToString(",")
            prefs[KEY_USER_CREATED_AT] = user.created_at ?: ""
            prefs[KEY_ACCESS_TOKEN] = user.token       // token almacenado
            prefs[KEY_IS_LOGGED_IN] = true
        }
    }

    // ✅ Obtener un objeto User completo y validado
    suspend fun getUser(): User? {
        val prefs = dataStore.data.first()

        val token = prefs[KEY_ACCESS_TOKEN] ?: return null
        val id = prefs[KEY_USER_ID] ?: return null
        val email = prefs[KEY_USER_EMAIL] ?: "SinEmail"
        val name = prefs[KEY_USER_NAME]
        val lastName = prefs[KEY_USER_LAST_NAME] ?: ""
        val fullName = prefs[KEY_USER_FULL_NAME]
        val username = prefs[KEY_USER_USERNAME] ?: ""
        val code = prefs[KEY_USER_CODE]
        val imagenUrl = prefs[KEY_USER_IMAGEN_URL]
        val roles = prefs[KEY_USER_ROLES]?.split(",") ?: emptyList()
        val permissions = prefs[KEY_USER_PERMISSIONS]?.split(",") ?: emptyList()
        val created_at = prefs[KEY_USER_CREATED_AT]

        return name?.let {
            User(
                id = id,
                email = email,
                name = it,
                last_name = lastName,
                fullName = fullName,
                username = username,
                code = code,
                imagenUrl = imagenUrl,
                roles = roles,
                permissions = permissions,
                created_at = created_at,
                token = token
            )
        }
    }

    // ✅ Limpieza robusta y completa de sesión
    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_USER_EMAIL)
            prefs.remove(KEY_USER_NAME)
            prefs.remove(KEY_USER_LAST_NAME)
            prefs.remove(KEY_USER_FULL_NAME)
            prefs.remove(KEY_USER_USERNAME)
            prefs.remove(KEY_USER_CODE)
            prefs.remove(KEY_USER_IMAGEN_URL)
            prefs.remove(KEY_USER_ROLES)
            prefs.remove(KEY_USER_PERMISSIONS)
            prefs.remove(KEY_USER_CREATED_AT)
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs[KEY_IS_LOGGED_IN] = false
        }
        println("✅ Sesión eliminada correctamente.")
    }

    // ✅ Comprobar si el usuario ha iniciado sesión
    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.map { prefs ->
            prefs[KEY_IS_LOGGED_IN] ?: false
        }.first()
    }

    // ✅ Establecer si se completó el onboarding
    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[ONBOARDING_KEY] = completed
        }
    }

    // ✅ Comprobar si se ha completado el onboarding
    suspend fun isOnboardingCompleted(): Boolean {
        return dataStore.data.map { prefs ->
            prefs[ONBOARDING_KEY] ?: false
        }.first()
    }

    // ✅ Guardar solo el token (útil en ciertas situaciones)
    suspend fun saveAuthToken(token: String) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = token
        }
    }

    // ✅ Obtener solo el token de autenticación
    suspend fun getAuthToken(): String? {
        val prefs = dataStore.data.first()
        return prefs[KEY_ACCESS_TOKEN]
    }
}
