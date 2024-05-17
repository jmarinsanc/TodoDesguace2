package com.jp.tododesguace

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class SubirUnProducto : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null
    private lateinit var editTextNombre: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var editTextPrecio: EditText
    private lateinit var editTextVendedor: EditText
    private lateinit var editTextCantidad: EditText
    private lateinit var imageViewProducto: ImageView
    private lateinit var buttonSubirFoto: Button
    private lateinit var buttonSubirProducto: Button
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subir_un_producto)

        editTextNombre = findViewById(R.id.editTextNombreProducto)
        editTextDescripcion = findViewById(R.id.editTextDescripcionProducto)
        editTextPrecio = findViewById(R.id.editTextPrecioProducto)
        editTextVendedor = findViewById(R.id.editTextVendedorProducto)
        editTextCantidad = findViewById(R.id.editTextCantidadProducto)
        imageViewProducto = findViewById(R.id.imageViewProducto)
        buttonSubirFoto = findViewById(R.id.buttonSubirFoto)
        buttonSubirProducto = findViewById(R.id.buttonSubirProducto)
        bottomNavigationView = findViewById(R.id.bottom_navigation)


        buttonSubirFoto.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST)
        }

        buttonSubirProducto.setOnClickListener {
            subirProducto()
        }

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

    private fun subirProducto() {
        val db = FirebaseFirestore.getInstance()
        val producto = Producto(
            id = "",
            nombre = editTextNombre.text.toString(),
            descripcion = editTextDescripcion.text.toString(),
            precio = editTextPrecio.text.toString(),
            vendedor = editTextVendedor.text.toString(),
            cantidad = editTextCantidad.text.toString()
        )

        db.collection("productosSinId").add(producto)
            .addOnSuccessListener { documentReference ->
                // Aquí se obtiene correctamente el ID del documento recién creado.
                producto.id = documentReference.id
                Log.d("Firestore", ": ${producto.id}")
                db.collection("productos").document(producto.id).set(producto).addOnSuccessListener {
                    if (bitmap!=null){
                        uploadImage(bitmap, producto.id)

                    }
                }


                Log.d("Firestore", "Producto añadido con éxito: ${producto.nombre}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al añadir producto", e)
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                imageUri = uri
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                imageViewProducto.setImageBitmap(bitmap)
            }
        }
    }

    private fun uploadImage(bitmap: Bitmap, idProducto: String) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        try {
            val storageReference = FirebaseStorage.getInstance().reference.child("images/${idProducto}")
            val uploadTask = storageReference.putBytes(data)

            uploadTask.addOnSuccessListener {
                Toast.makeText(this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir la imagen: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }catch (e: ApiException){
            println("error")
        }

    }

}