package com.jp.tododesguace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ResultadosBusquedaActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductosAdapter
    private val productos = mutableListOf<Producto>()  // Lista mutable de productos



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultados_busqueda)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)


        initRecyclerView()

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cuenta -> {
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                }
                R.id.carrito -> {
                    val intent = Intent(this, CarritoActivity::class.java)
                    startActivity(intent)
                }
                R.id.selector -> {
                    val intent = Intent(this, SubidaActivity::class.java)
                    startActivity(intent)
                }
                R.id.buscar -> {
                    val intent = Intent(this, BusquedaActivity::class.java).apply {  }
                    startActivity(intent)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

    }

    fun initRecyclerView(){

        val db = FirebaseFirestore.getInstance()
        GlobalScope.launch(Dispatchers.IO) {
            db.collection("productos")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val producto = document.toObject<Producto>()
                        // Aquí puedes hacer algo con cada producto
                        productos.add(producto)
                        println("Producto: ${producto}")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error al obtener documentos: $exception")
                }
            runOnUiThread {
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCarrito)
                adapter.notifyDataSetChanged()  // Notificar cambios al adaptador
                recyclerView.layoutManager = GridLayoutManager(this@ResultadosBusquedaActivity, 2)  // Usar GridLayoutManager con 2 columnas
                recyclerView.adapter = ProductosAdapter(productos)

            }
        }

    }


}