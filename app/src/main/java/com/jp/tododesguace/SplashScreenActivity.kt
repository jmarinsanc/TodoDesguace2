package com.jp.tododesguace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var imageRotativa: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        imageRotativa = findViewById(R.id.imageView)
        girar(imageRotativa)


        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreenActivity, BusquedaActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }


    /**
     * Método que hace que gire la imagen que es icono de la apliacación
     * @param view
     */
    private fun girar(view: View) {
        val animation = RotateAnimation(0f, 360f, RotateAnimation.RELATIVE_TO_SELF,
            0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 1400
        animation.repeatCount = Animation.INFINITE
        view.startAnimation(animation)
    }
}
