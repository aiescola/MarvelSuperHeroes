package com.aescolar.marvelsuperheroes.base

import android.app.Application
import com.aescolar.marvelsuperheroes.characters.charactersModule
import com.aescolar.marvelsuperheroes.data.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MarvelSuperHeroesApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val appModules = listOf(
            apiModule,
            dataModule,
            charactersModule
        )
        startKoin {
            androidContext(this@MarvelSuperHeroesApplication)
            modules(appModules)
        }
    }
}
