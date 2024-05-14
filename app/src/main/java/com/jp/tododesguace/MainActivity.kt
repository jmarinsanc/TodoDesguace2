package com.jp.tododesguace

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.jp.tododesguace.databinding.ActivityMainBinding
import java.util.Objects

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    private lateinit var adaptador: ProductoBusquedaAdaptador
    private lateinit var bottomNavigationView: BottomNavigationView
    private val llmanager = LinearLayoutManager(this)

    var listProducto = arrayListOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TodoDesguace) // Forzar el tema aquí

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.recyclerViewBusqueda)

        setupBottomMenu()
        initRecyclerView()

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filtrar(s.toString())
            }
        })
    }

    private fun setupBottomMenu() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.buscar

        bottomNavigationView.setOnItemSelectedListener { item -> onItemSelectedListener(item) }
    }

    private fun onItemSelectedListener(item: MenuItem): Boolean {
        val intent = when (item.itemId) {
            R.id.cuenta -> Intent(this, AuthActivity::class.java)
            R.id.carrito -> Intent(this, CarritoActivity::class.java)
            R.id.selector -> Intent(this, SubidaActivity::class.java)
            R.id.buscar -> null
            else -> null
        }

        intent?.let {
            startActivityWithAnimation(it)
        }

        return true
    }

    private fun startActivityWithAnimation(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun filtrar(texto: String) {
        val listaFiltrada = mutableListOf<Producto>()

        listProducto.forEach {
            if (it.nombre.toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(it)
            }
        }

        adaptador.filtrar(listaFiltrada)
    }

    fun initRecyclerView() {
        val productos = mutableListOf<Producto>()  // Lista mutable de productos
        val db = FirebaseFirestore.getInstance()

        db.collection("productos")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val producto = document.toObject<Producto>()
                    productos.add(producto)  // Añadir cada producto a la lista mutable
                }

                runOnUiThread {
                    adaptador = ProductoBusquedaAdaptador(productos)
                    recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 1)
                    recyclerView.adapter = adaptador
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener documentos: $exception")
            }
    }
}
