package com.aescolar.marvelsuperheroes

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import arrow.core.Either
import arrow.core.orNull
import com.aescolar.apiclient.ApiClient
import com.aescolar.marvelsuperheroes.characters.charactersModule
import com.aescolar.marvelsuperheroes.domain.usecases.GetCharacters
import com.aescolar.marvelsuperheroes.characters.ui.CharactersListActivity
import com.aescolar.marvelsuperheroes.characters.ui.CharactersViewModel
import com.aescolar.marvelsuperheroes.domain.model.Page
import com.aescolar.marvelsuperheroes.recyclerview.RecyclerViewItemCountAssertion.Companion.withItemCount
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class CharactersListActivityTest :
    AcceptanceTest<CharactersListActivity>(CharactersListActivity::class.java) {

    @MockK
    lateinit var getCharactersMock: GetCharacters

    override val testModules: Module = module {
        viewModel { CharactersViewModel(getCharactersMock) }
    }

    override val overrideModules: List<Module> = listOf(charactersModule)

    @Test
    fun listIsDisplayedWhenSomeCharactersAreReturned() {
        val amount = 2
        givenNCharactersReturned(amount)

        startActivity()

        onView(withId(R.id.characters_recycler_view)).check(matches(isDisplayed()))
        onView(withId(R.id.search_filter)).check(matches(isDisplayed()))

        onView(withId(R.id.no_characters)).check(matches(not(isDisplayed())))
        onView(withId(R.id.loading_progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.error_refresh)).check(matches(not(isDisplayed())))
        onView(withId(R.id.error_text)).check(matches(not(isDisplayed())))
    }

    @Test
    fun emptyListIsDisplayedWhenNoCharactersAreReturned() {
        val amount = 0
        givenNCharactersReturned(amount)

        startActivity()

        onView(allOf(withId(R.id.no_characters), withText(R.string.no_superheroes_text))).check(
            matches(isDisplayed())
        )

        onView(withId(R.id.search_filter)).check(matches(not(isDisplayed())))
        onView(withId(R.id.characters_recycler_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.loading_progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.error_refresh)).check(matches(not(isDisplayed())))
        onView(withId(R.id.error_text)).check(matches(not(isDisplayed())))
    }

    @Test
    fun listOfCharactersIsFiltered() {
        val amount = 5
        val filteredItems = 1
        val response = givenNCharactersReturned(amount)

        val allCharacters = response.orNull()!!.data
        val firstCharacter = allCharacters.take(filteredItems).first()
        val filter = firstCharacter.name
        every { getCharactersMock.invoke(filter) } returns Either.right(
            Page(
                false,
                listOf(firstCharacter)
            )
        )

        startActivity()

        onView(withId(R.id.search_filter)).check(matches(isDisplayed()))
        onView(withId(R.id.search_filter)).perform(typeText(filter), pressImeActionButton())

        // This is for SIMPLICITY and should NOT be done. Check
        // https://www.javacodegeeks.com/2019/08/espresso-idling-resource-recyclerview-data-changes.html
        // for a proper solution
        Thread.sleep(100L)

        onView(withId(R.id.characters_recycler_view)).check(withItemCount(filteredItems))
    }

    @Test
    fun notFoundErrorIsDisplayedWhenNotFoundErrorIsReturned() {
        givenNetworkError(ApiClient.ApiError.NotFoundError)

        startActivity()
        verifyErrorLayoutVisibility()

        onView(withText(R.string.network_error_not_found)).check(matches(isDisplayed()))
    }

    @Test
    fun unknownErrorIsDisplayedWhenUnknownErrorIsReturned() {
        val code = 400
        givenNetworkError(ApiClient.ApiError.UnknownError(code))

        startActivity()
        verifyErrorLayoutVisibility()

        val expectedTest = testRule.activity.applicationContext.resources.getString(
            R.string.network_error_unknown,
            code
        )

        onView(withText(expectedTest)).check(matches(isDisplayed()))
    }

    @Test
    fun networkErrorIsDisplayedWhenNetworkErrorIsReturned() {
        givenNetworkError(ApiClient.ApiError.NetworkError)

        startActivity()
        verifyErrorLayoutVisibility()

        onView(withText(R.string.network_error_generic)).check(matches(isDisplayed()))
    }

    private fun verifyErrorLayoutVisibility() {
        onView(withId(R.id.error_refresh)).check(matches(isDisplayed()))
        onView(withId(R.id.error_text)).check(matches(isDisplayed()))

        onView(withId(R.id.characters_recycler_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.search_filter)).check(matches(not(isDisplayed())))
        onView(withId(R.id.loading_progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.no_characters)).check(matches(not(isDisplayed())))
    }

    private fun givenNCharactersReturned(amount: Int): Either<ApiClient.ApiError, Page> {
        val response = Either.right(
            Page(
                false,
                CharactersObjectMother.createCharacters(amount)
            )
        )
        every { getCharactersMock.invoke() } returns response
        return response
    }

    private fun givenNetworkError(error: ApiClient.ApiError) {
        val response = Either.left(error)
        every { getCharactersMock.invoke() } returns response
    }
}
