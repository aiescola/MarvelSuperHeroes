package com.aescolar.marvelsuperheroes.data

import org.koin.dsl.module

val dataModule = module {
    single { CharactersRepository(get()) }
}
