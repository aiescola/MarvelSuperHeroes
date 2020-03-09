package com.aescolar.marvelsuperheroes.characterdetail.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.aescolar.marvelsuperheroes.R
import com.aescolar.marvelsuperheroes.domain.model.Character
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_character_detail.*

class CharacterDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val character = intent?.extras?.getParcelable<Character>(CHARACTER_DETAIL_KEY)
        character?.let {
            title = it.name
            setupView(character)
        }
    }

    private fun setupView(character: Character) {
        with(character) {
            character_name.text = name
            character_description.text = description
            val thumbnailPath = "$thumbnail/$THUMBNAIL_FORMAT.$thumbnailExtension"
            Glide.with(this@CharacterDetailActivity).load(thumbnailPath).placeholder(R.drawable.placeholder_image)
                .into(character_image)
            copyright_label.text = attributionText

            // setup listView:
            if (!series.isNullOrEmpty()) {
                appears_in.visibility = View.VISIBLE

                val arrayAdapter = ArrayAdapter<String>(
                    this@CharacterDetailActivity,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    series.toTypedArray()
                )
                series_list.adapter = arrayAdapter
            }
        }
    }

    companion object {
        private const val THUMBNAIL_FORMAT = "landscape_incredible"
        private const val CHARACTER_DETAIL_KEY = "character_key"

        fun open(activity: Activity, character: Character) {
            val intent = Intent(activity, CharacterDetailActivity::class.java)
            intent.putExtra(CHARACTER_DETAIL_KEY, character)
            activity.startActivity(intent)
        }
    }
}
