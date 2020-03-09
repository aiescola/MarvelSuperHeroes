package com.aescolar.marvelsuperheroes.domain.model

import com.aescolar.marvelsuperheroes.domain.model.Character

data class Page(
    val moreToLoad: Boolean,
    val data: List<Character>
)
