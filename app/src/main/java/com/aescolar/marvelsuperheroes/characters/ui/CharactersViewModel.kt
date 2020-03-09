package com.aescolar.marvelsuperheroes.characters.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aescolar.apiclient.ApiClient
import com.aescolar.marvelsuperheroes.domain.usecases.GetCharacters
import com.aescolar.marvelsuperheroes.domain.model.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CharactersViewModel(private val getCharacters: GetCharacters) : ViewModel() {

    sealed class CharactersListScreenStatus {
        object ShowLoading : CharactersListScreenStatus()
        object ShowNoData : CharactersListScreenStatus()
        data class ShowNetworkError(val error: ApiClient.ApiError) : CharactersListScreenStatus()
        data class CharactersLoaded(val moreToLoad: Boolean, val characters: List<Character>) :
            CharactersListScreenStatus()
    }

    val charactersListScreenStatus: LiveData<CharactersListScreenStatus>
        get() = _charactersListScreenStatus

    private val _charactersListScreenStatus = MutableLiveData<CharactersListScreenStatus>()

    private var initialLoad = true

    fun fetchPage(filter: String? = null, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (initialLoad || forceRefresh) {
                initialLoad = false
                _charactersListScreenStatus.postValue(CharactersListScreenStatus.ShowLoading)
            }

            val characters = withContext(Dispatchers.IO) { getCharacters(filter) }
            characters.bimap({
                _charactersListScreenStatus.postValue(CharactersListScreenStatus.ShowNetworkError(it))
            }, {
                if (it.data.isEmpty()) {
                    _charactersListScreenStatus.postValue(CharactersListScreenStatus.ShowNoData)
                } else {
                    _charactersListScreenStatus.postValue(
                        CharactersListScreenStatus.CharactersLoaded(it.moreToLoad, it.data)
                    )
                }
            })
        }
    }
}
