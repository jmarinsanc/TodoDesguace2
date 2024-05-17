package com.jp.tododesguace

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.ByteArrayOutputStream

class DetalleProductoActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null
    private lateinit var producto: Producto
    private lateinit var imageViewProducto: ImageView
    val bitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_producto)

        val textViewNombre: TextView = findViewById(R.id.textViewNombreProducto)
        val textViewDescripcion: TextView = findViewById(R.id.textViewDescripcionProducto)
        val textViewPrecio: TextView = findViewById(R.id.textViewPrecioProducto)
        val textViewVendedor: TextView = findViewById(R.id.textViewVendedorProducto)
        val textViewCantidad: TextView = findViewById(R.id.textViewCantidad)
        val buttonAnadirAlCarrito: Button = findViewById(R.id.buttonAnadirAlCarrito)
        val buttonSubirFoto: Button = findViewById(R.id.buttonSubirFoto)
        imageViewProducto = findViewById(R.id.imageViewProducto)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val id = intent?.getStringExtra("id")
        val nombre = intent?.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val precio = intent.getStringExtra("precio")
        val vendedor = intent.getStringExtra("vendedor")
        val cantidad = intent.getStringExtra("cantidad")
        producto = Producto(id.toString(), nombre.toString(), descripcion.toString(), precio.toString(),
            vendedor.toString(), cantidad.toString()
        )
        val origen = intent?.getStringExtra("origen")
        if (origen != "ResultadosBusquedaActivity") {
            buttonAnadirAlCarrito.isVisible = false
            buttonSubirFoto.isVisible = false
        }

        textViewNombre.text = producto.nombre
        textViewDescripcion.text = producto.descripcion
        textViewPrecio.text = producto.precio
        textViewVendedor.text = producto.vendedor
        textViewCantidad.text = producto.cantidad


        buttonAnadirAlCarrito.setOnClickListener {
            addToCart(producto)
        }

        buttonSubirFoto.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST)
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
        cargarImagen(producto.id)
    }
    fun cargarImagen(productId: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${producto.id}")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            imageViewProducto.isVisible=true
            Glide.with(this@DetalleProductoActivity).load(uri).into(imageViewProducto)
        }.addOnFailureListener {

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
        if (producto != null) {
            db.collection("carrito").document(producto.id).set(producto)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al añadir producto al carrito: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                imageUri = uri
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                imageViewProducto.setImageBitmap(bitmap)

                uploadImage(bitmap)
            }
        }
    }

    private fun uploadImage(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val storageReference = FirebaseStorage.getInstance().reference.child("images/${producto.id}")
        val uploadTask = storageReference.putBytes(data)

        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al subir la imagen: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
