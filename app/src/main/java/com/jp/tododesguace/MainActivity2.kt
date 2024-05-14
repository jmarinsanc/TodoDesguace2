package com.jp.tododesguace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity2 : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        setupBottomMenu()
    }

    private fun setupBottomMenu() {

        bottomNavigationView = findViewById(R.id.bottom_navigation)
//        bottomNavigationView.selectedItemId = R.id.selector

        bottomNavigationView.setOnItemSelectedListener { item -> onItemSelectedListener(item) }


    }
    private fun onItemSelectedListener(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (item.itemId) {
            R.id.cuenta -> {
                val intent = Intent(this, AuthActivity::class.java).apply {  }
                startActivity(intent)
            }
            R.id.carrito -> {
                val intent = Intent(this, CarritoActivity::class.java).apply {  }
                startActivity(intent)
            }
            R.id.selector -> {
                val intent = Intent(this, SubidaActivity::class.java).apply {  }
                startActivity(intent)
            }
            R.id.buscar -> {
                val intent = Intent(this, BusquedaActivity::class.java).apply {  }
                startActivity(intent)
            }

        }
        return true
    }
}