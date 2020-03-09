package com.aescolar.marvelsuperheroes

import com.aescolar.apiclient.model.CharacterDto
import com.aescolar.apiclient.model.CharactersWrapperDto
import com.aescolar.apiclient.model.DataDto

object CharactersWrapperObjectMother {

    val singlePageCharactersWrapperDto = CharactersWrapperDto(
        "1", "200", "© 2020 MARVEL", "Marvel", "", DataDto(
            "0", "20", "1", "3",
            listOf(
                CharacterDto("1", "aa", "", "", "", null, null, null, null, null, null),
                CharacterDto("2", "bb", "", "", "", null, null, null, null, null, null),
                CharacterDto("3", "cc", "", "", "", null, null, null, null, null, null)
            )
        ), ""
    )

    val multiPageCharactersWrapperDto = listOf(
        CharactersWrapperDto(
            "3", "200", "© 2020 MARVEL", "Marvel", "", DataDto(
                "0", "20", "5", "3",
                listOf(
                    CharacterDto("1", "aa", "", "", "", null, null, null, null, null, null),
                    CharacterDto("2", "bb", "", "", "", null, null, null, null, null, null),
                    CharacterDto("3", "cc", "", "", "", null, null, null, null, null, null)
                )
            ), ""
        ),
        CharactersWrapperDto(
            "1", "200", "© 2020 MARVEL", "Marvel", "", DataDto(
                "3", "20", "5", "2",
                listOf(
                    CharacterDto("4", "dd", "", "", "", null, null, null, null, null, null),
                    CharacterDto("5", "ee", "", "", "", null, null, null, null, null, null)
                )
            ), ""
        )
    )
}
