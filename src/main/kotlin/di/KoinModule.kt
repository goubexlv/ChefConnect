package cm.daccvo.di

import cm.daccvo.features.auth.repository.AuthRepository
import cm.daccvo.features.auth.repository.AuthRepositoryImpl
import org.koin.dsl.module

val KoinModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl()
    }
}