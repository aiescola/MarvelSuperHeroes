package com.aescolar.marvelsuperheroes

import com.aescolar.marvelsuperheroes.domain.model.Character

object CharactersObjectMother {
    private val charactersSequence = generateSequence(1) { it + 1 }.map {
        Character(
            it.toString(),
            "hero $it",
            "description $it",
            "thumbnail",
            "ext",
            "Marvel",
            null
        )
    }

    private val seriesSequence = generateSequence(1) { it + 1 }.map {
        "Series-$it"
    }

    fun createCharacters(amount: Int): List<Character> = charactersSequence.take(amount).toList()

    fun createCharacter(hasSeries: Boolean, numberOfSeries: Int = 1): Character {
        val series = if (hasSeries) {
            seriesSequence.take(numberOfSeries).toList()
        } else {
            emptyList()
        }

        return Character(
            "1",
            "hero",
            "description",
            "thumbnail",
            "ext",
            "Marvel",
            series
        )

    }
}
