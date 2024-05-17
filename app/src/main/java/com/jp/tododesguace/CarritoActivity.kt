package com.jp.tododesguace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class CarritoActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TodoDesguace) // Forzar el tema aquí
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        recyclerView = findViewById(R.id.recyclerViewCarrito)
        setupBottomMenu()
        initRecyclerView()
    }

    private fun setupBottomMenu() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.carrito

        bottomNavigationView.setOnItemSelectedListener { item -> onItemSelectedListener(item) }
    }

    private fun onItemSelectedListener(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cuenta -> {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.carrito -> {
                // No hay necesidad de reiniciar la actividad CarritoActivity si ya estamos aquí.
            }
            R.id.selector -> {
                val intent = Intent(this, SubidaActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.buscar -> {
                val intent = Intent(this, BusquedaActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }
        return true
    }

    fun initRecyclerView() {
        val productos = mutableListOf<Producto>()  // Lista mutable de productos
        val db = FirebaseFirestore.getInstance()

        db.collection("carrito")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val producto = document.toObject<Producto>()
                    productos.add(producto)  // Añadir cada producto a la lista mutable
                }
                runOnUiThread {
                    recyclerView.layoutManager = GridLayoutManager(this@CarritoActivity, 1)  // Usar GridLayoutManager con 2 columnas
                    recyclerView.adapter = ProductosAdapter(productos)
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener documentos: $exception")
            }
    }
}
