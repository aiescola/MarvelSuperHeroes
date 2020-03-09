package com.aescolar.marvelsuperheroes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aescolar.marvelsuperheroes.characters.ui.CharactersListActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // This screens does nothing but the gif is so entertaining that I couldn't help but
        // to put a 5 second delay before starting the application
        Glide.with(this).load(resources.getString(R.string.loading_animation_path))
            .into(loading_animation_container)

        GlobalScope.launch {
            delay(5000L)
            startActivity(Intent(this@SplashActivity, CharactersListActivity::class.java))
            finish()
        }
    }
}
