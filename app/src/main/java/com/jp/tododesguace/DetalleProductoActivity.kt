package com.jp.tododesguace

import android.content.Intent
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetalleProductoActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_producto)

        val textViewNombre: TextView = findViewById(R.id.textViewNombreProducto)
        val textViewDescripcion: TextView = findViewById(R.id.textViewDescripcionProducto)
        val textViewPrecio: TextView = findViewById(R.id.textViewPrecioProducto)
        val textViewVendedor: TextView = findViewById(R.id.textViewVendedorProducto)
        val textViewCantidad: TextView = findViewById(R.id.textViewCantidad)
        val buttonAnadirAlCarrito: Button = findViewById(R.id.buttonAnadirAlCarrito)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        val id = intent?.getStringExtra("id")
        val nombre = intent?.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val precio = intent.getStringExtra("precio")
        val vendedor = intent.getStringExtra("vendedor")
        val cantidad = intent.getStringExtra("cantidad")
        val producto = Producto(id.toString(), nombre.toString(), descripcion.toString(), precio.toString(),
            vendedor.toString(), cantidad.toString()
        )
        val origen = intent?.getStringExtra("origen")
        if (origen != "ResultadosBusquedaActivity") {
            buttonAnadirAlCarrito.isVisible = false
        }

        textViewNombre.text = producto.nombre
        textViewDescripcion.text = producto.descripcion
        textViewPrecio.text = producto.precio
        textViewVendedor.text = producto.vendedor
        textViewCantidad.text = producto.cantidad

        buttonAnadirAlCarrito.setOnClickListener {
            addToCart(producto)
        }
        bottomNavigationView.selectedItemId = R.id.buscar

        // Configurar BottomNavigationView para manejar la navegación
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cuenta -> {
                    val intent = Intent(this, AuthActivity::class.java)
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
                R.id.buscar -> {
                    val intent = Intent(this, BusquedaActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun addToCart(producto: Producto?) {
        val db = FirebaseFirestore.getInstance()
        val cartItem = hashMapOf(
            "id" to (producto?.id ?: ""),
            "nombre" to (producto?.nombre ?: ""),
            "descripcion" to (producto?.descripcion ?: ""),
            "precio" to (producto?.precio ?: ""),
            "vendedor" to (producto?.vendedor ?: ""),
            "cantidad" to (producto?.cantidad ?: "")
        )
        println(producto)

        // Añadir el producto al carrito en Firestore
        if (producto != null) {
            db.collection("carrito").document(producto.id).set(producto)
                .addOnSuccessListener { documentReference ->
                    // Mostrar un mensaje o hacer algo al añadir con éxito el producto al carrito
                    Toast.makeText(this, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Manejar el error
                    Toast.makeText(this, "Error al añadir producto al carrito: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
