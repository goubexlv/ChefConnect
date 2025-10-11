package cm.daccvo.di

import cm.daccvo.features.auth.repository.AuthRepository
import cm.daccvo.features.auth.repository.AuthRepositoryImpl
import cm.daccvo.features.recette.repository.RecetteRepository
import cm.daccvo.features.recette.repository.RecetteRepositoryImpl
import org.koin.dsl.module

val KoinModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl()
    }

    single<RecetteRepository> {
        RecetteRepositoryImpl()
    }
}