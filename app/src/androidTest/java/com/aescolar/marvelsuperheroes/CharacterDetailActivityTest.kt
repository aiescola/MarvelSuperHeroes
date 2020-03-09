package com.aescolar.marvelsuperheroes

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aescolar.marvelsuperheroes.characterdetail.ui.CharacterDetailActivity
import com.aescolar.marvelsuperheroes.domain.model.Character
import org.hamcrest.Matchers.allOf
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.module.Module

@RunWith(AndroidJUnit4::class)
class CharacterDetailActivityTest :
    AcceptanceTest<CharacterDetailActivity>(CharacterDetailActivity::class.java) {
    override val testModules: Module?
        get() = null

    override val overrideModules: List<Module>
        get() = emptyList()


    @Test
    fun displayCharacterInfo() {
        val response = givenACharacter()

        startActivity(response)

        onView(allOf(withId(R.id.character_name), withText(response.name))).check(
            matches(isDisplayed())
        )
        onView(allOf(withId(R.id.character_description), withText(response.description))).check(
            matches(isDisplayed())
        )
        onView(allOf(withId(R.id.copyright_label), withText(response.attributionText))).check(
            matches(isDisplayed())
        )

        onView(withId(R.id.character_image)).check(matches(isDisplayed()))
        onView(withId(R.id.appears_in)).check(matches(isDisplayed()))
    }

    private fun givenACharacter(hasSeries: Boolean = true) =
        CharactersObjectMother.createCharacter(hasSeries)

    private fun startActivity(character: Character): CharacterDetailActivity {
        val args = Bundle()
        args.putParcelable("character_key", character)

        return startActivity(args)
    }

}