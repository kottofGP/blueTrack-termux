package com.bluestar.bluetrack.data.repository

import com.bluestar.bluetrack.domain.model.Session
import com.bluestar.bluetrack.domain.model.User
import com.bluestar.bluetrack.domain.repository.AuthRepository

class MockAuthRepository : AuthRepository {

    private var currentSession: Session? = null

    override suspend fun login(
        username: String,
        password: String
    ): Session? {
        if (username.isBlank() || password.isBlank()) {
            return null
        }

        val user = User(
            id = "mock-user-001",
            username = username,
            displayName = "Usuario BlueStar"
        )

        return Session(
            user = user,
            isAuthenticated = true
        ).also {
            currentSession = it
        }
    }

    override suspend fun getSession(): Session? {
        return currentSession
    }

    override suspend fun logout() {
        currentSession = null
    }
}
