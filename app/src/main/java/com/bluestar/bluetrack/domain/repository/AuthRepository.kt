package com.bluestar.bluetrack.domain.repository

import com.bluestar.bluetrack.domain.model.Session

interface AuthRepository {

    suspend fun login(username: String, password: String): Session?

    suspend fun getSession(): Session?

    suspend fun logout()
}
