package com.aescolar.marvelsuperheroes.characters.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aescolar.marvelsuperheroes.R
import com.aescolar.marvelsuperheroes.domain.model.Character
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.character_view_holder.view.*

class CharactersListAdapter(private val clickListener: (Character) -> Unit) : ListAdapter<Character, CharactersListAdapter.CharacterViewHolder>(
    DIFF_UTIL
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        return CharacterViewHolder(parent.inflate(R.layout.character_view_holder))
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position)
        holder.bind(character)
    }

    private fun ViewGroup.inflate(resId: Int): View {
        return LayoutInflater.from(this.context)
            .inflate(resId, this, false)
    }

    inner class CharacterViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(character: Character) {
            with(view) {
                name.text = character.name
                val thumbnailPath = "${character.thumbnail}/$THUMBNAIL_FORMAT.${character.thumbnailExtension}"
                Glide.with(this).load(thumbnailPath)
                    .placeholder(R.drawable.placeholder_image)
                    .into(thumbnail)

                setOnClickListener {
                    clickListener(character)
                }
            }
        }
    }

    companion object {
        private const val THUMBNAIL_FORMAT = "landscape_xlarge"

        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Character>() {
            override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
                return oldItem == newItem
            }
        }
    }
}
