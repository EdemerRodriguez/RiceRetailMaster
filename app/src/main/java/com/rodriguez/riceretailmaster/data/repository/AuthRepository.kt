package com.rodriguez.riceretailmaster.data.repository

import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.model.UserRole
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class AuthRepository {

    private val auth get() = SupabaseService.client.auth

    suspend fun signIn(email: String, password: String): UserRole {
        auth.signInWith(Email) {
            this.email = email.trim()
            this.password = password
        }
        return currentRole()
    }

    fun currentRole(): UserRole {
        val role = auth.currentUserOrNull()
            ?.userMetadata
            ?.get("role")
            ?.jsonPrimitive
            ?.contentOrNull
        return UserRole.from(role)
    }

    fun currentUserId(): String? = auth.currentUserOrNull()?.id

    suspend fun awaitInitialRole(): UserRole? {
        val status = auth.sessionStatus.first { it !is SessionStatus.Initializing }
        return if (status is SessionStatus.Authenticated) currentRole() else null
    }

    suspend fun sendPasswordReset(email: String) {
        auth.resetPasswordForEmail(email.trim(), redirectUrl = "riceretailmaster://reset-password")
    }

    suspend fun updatePassword(newPassword: String) {
        auth.updateUser { password = newPassword }
    }

    suspend fun signOut() {
        runCatching { auth.signOut() }
    }
}
