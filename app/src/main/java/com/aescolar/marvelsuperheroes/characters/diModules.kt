package com.aescolar.marvelsuperheroes.characters

import com.aescolar.marvelsuperheroes.domain.usecases.GetCharacters
import com.aescolar.marvelsuperheroes.characters.ui.CharactersViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val charactersModule = module {
    factory { GetCharacters(get()) }
    viewModel {
        CharactersViewModel(get())
    }
}
