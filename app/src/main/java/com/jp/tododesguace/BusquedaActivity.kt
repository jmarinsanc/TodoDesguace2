package com.jp.tododesguace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.jp.tododesguace.databinding.ActivityAuthBinding
import com.jp.tododesguace.databinding.ActivityBusquedaBinding
import com.jp.tododesguace.databinding.ActivityMainBinding
import java.util.Objects

class BusquedaActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding : ActivityBusquedaBinding
    private lateinit var adaptador: ProductoBusquedaAdaptador
    private lateinit var bottomNavigationView: BottomNavigationView
    private val llmanager = LinearLayoutManager(this)
    val productos = arrayListOf<Producto>()  // Lista mutable de productos

    var listProducto = arrayListOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TodoDesguace) // Forzar el tema aquí
        super.onCreate(savedInstanceState)
        binding = ActivityBusquedaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupBottomMenu()
        initRecyclerView()

        binding.searchView.addTextChangedListener(object: TextWatcher {
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
        when (item.itemId) {
            R.id.cuenta -> {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.carrito -> {
                val intent = Intent(this, CarritoActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.selector -> {
                val intent = Intent(this, SubidaActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            R.id.buscar -> {}
        }
        return true
    }

    private fun filtrar(texto: String) {
        var listaFiltardo = arrayListOf<Producto>()
        productos.forEach {
            if (it.nombre.toLowerCase().contains(texto.toLowerCase())) {
                listaFiltardo.add(it)
            }
        }
        adaptador.filtrar(listaFiltardo)
    }

    fun initRecyclerView() {
        val db = FirebaseFirestore.getInstance()

        db.collection("productos")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val producto = document.toObject<Producto>()
                    productos.add(producto)  // Añadir cada producto a la lista mutable
                }
                // Inicializar el adaptador aquí asegura que no sea null cuando se use
                adaptador = ProductoBusquedaAdaptador(productos)
                recyclerView = findViewById(R.id.recyclerViewBusqueda)
                recyclerView.layoutManager = GridLayoutManager(this@BusquedaActivity, 1)
                recyclerView.adapter = adaptador
            }
            .addOnFailureListener { exception ->
                println("Error al obtener documentos: $exception")
            }
    }
}
