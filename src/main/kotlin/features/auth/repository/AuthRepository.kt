package cm.daccvo.features.auth.repository

import cm.daccvo.domain.dto.ChefConnectResponse
import cm.daccvo.domain.dto.auth.AuthResponse
import cm.daccvo.domain.dto.auth.LoginRequest
import cm.daccvo.domain.dto.auth.RegisterRequest

interface AuthRepository {

    suspend fun register(request: RegisterRequest) : ChefConnectResponse

    suspend fun login(request: LoginRequest) : AuthResponse
}