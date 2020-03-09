package com.aescolar.marvelsuperheroes.domain.model

import android.os.Parcelable
import com.aescolar.apiclient.model.CharactersWrapperDto
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Character(
    val id: String,
    val name: String,
    val description: String,
    val thumbnail: String?,
    val thumbnailExtension: String?,
    val attributionText: String?,
    val series: List<String>?
) : Parcelable

fun CharactersWrapperDto.asDomainModel(): List<Character> {
    return this.data.results.map {
        val series = it.series?.items?.map { series -> series.name }
        Character(
            it.id,
            it.name,
            it.description,
            it.thumbnail?.path,
            it.thumbnail?.extension,
            this.attributionText,
            series
        )
    }
}
