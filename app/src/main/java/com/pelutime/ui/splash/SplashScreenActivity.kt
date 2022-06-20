package com.pelutime.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.pelutime.R
import com.pelutime.databinding.ActivitySplashScreenBinding
import com.pelutime.ui.login.LoginActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class SplashScreenActivity : AppCompatActivity() {

    // VIEW BINDING
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showSplashAnimation()
    }

    private fun showSplashAnimation() {
        val intent = Intent(this, LoginActivity::class.java)
        val animation = AnimationUtils.loadAnimation(this, R.anim.animation_splash)
        binding.imageSplash.startAnimation(animation)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) { }
            override fun onAnimationEnd(animation: Animation?) {
                startActivity(intent)
                finish()
            }
            override fun onAnimationStart(animation: Animation?) { }
        })
    }
}