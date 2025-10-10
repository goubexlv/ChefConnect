package cm.daccvo.features.auth.repository

import cm.daccvo.config.JwtManager
import cm.daccvo.config.MongoDbManager
import cm.daccvo.config.MongoDbManager.users
import cm.daccvo.config.PasswordManager
import cm.daccvo.domain.dto.ChefConnectResponse
import cm.daccvo.domain.dto.auth.AuthResponse
import cm.daccvo.domain.dto.auth.LoginRequest
import cm.daccvo.domain.dto.auth.RegisterRequest
import cm.daccvo.domain.users.Token
import cm.daccvo.domain.users.User
import com.mongodb.client.model.Filters

class AuthRepositoryImpl : AuthRepository {

    private val usersDocument = MongoDbManager.users

    override suspend fun register(request: RegisterRequest): ChefConnectResponse {
        return try {
            val filter = request.email?.let { Filters.eq("email", it) }
            val existingUser = usersDocument.find(filter).firstOrNull()?.let { User.fromDocument(it) }

            if (existingUser == null) {
                val salt = PasswordManager.generateSalt()
                val hashedPassword = PasswordManager.hashPassword(
                    password = request.password,
                    salt = salt
                )

                val newUser = User(
                    email = request.email,
                    password = hashedPassword,
                    salt = salt
                ).toDocument()

                if(usersDocument.insertOne(newUser).wasAcknowledged()){
                    ChefConnectResponse(true,"Le compte a etez cree avec success")
                } else {
                    ChefConnectResponse(false,"Une erreur es survenu lors de la creation du compte")
                }

            } else {
                ChefConnectResponse(false,"Le compte existe deja ")
            }

        } catch (e: Exception) {
            ChefConnectResponse(false,"Échec de l'enregistrement: ${e.message}")
        }
    }

    override suspend fun login(request: LoginRequest): AuthResponse {
        return try {
            val userDoc = usersDocument.find(Filters.eq("email", request.email)).firstOrNull()
                ?: return AuthResponse(false,"Le compte n'existe pas")

            val user = User.fromDocument(userDoc)

            val hashedPassword = PasswordManager.hashPassword(password = request.password, salt =  user.salt)
            if (hashedPassword != user.password) {
                return AuthResponse(false,"Mot de passe ou email invalide")
            }
            val access_token = JwtManager.generateTokenEmail(user.uuid, user.email)
            val refresh_token = JwtManager.generateTokenEmail(user.uuid, user.email,true)
            AuthResponse(true,"Connection reussi", token = Token(access_token,refresh_token))
        } catch (e: Exception) {
            AuthResponse(false,"Échec de l'enregistrement: ${e.message}")
        }
    }
}