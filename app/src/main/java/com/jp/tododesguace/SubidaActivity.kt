package com.jp.tododesguace

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

class SubidaActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var selectFileButton: Button
    private lateinit var uploadFileButton: Button
    private lateinit var subirProducto: Button
    private var jsonContent: String? = null  // Holds the JSON content as a string

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TodoDesguace) // Forzar el tema aquí
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subida)

        selectFileButton = findViewById(R.id.selectFileButton)
        uploadFileButton = findViewById(R.id.uploadFileButton)
        subirProducto = findViewById(R.id.btnSubirProducto)

        subirProducto.setOnClickListener {
            val intent = Intent(this, SubirUnProducto::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)        }

        selectFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/json"
            startActivityForResult(intent, PICK_JSON_FILE_REQUEST_CODE)
        }

        uploadFileButton.setOnClickListener {
            jsonContent?.let {
                parseAndUploadJson(it)
            }
        }
        setupBottomMenu()

        uploadFileButton.isEnabled = false // Initially disabled
    }

    private fun setupBottomMenu() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.selector

        bottomNavigationView.setOnItemSelectedListener { item -> onItemSelectedListener(item) }
    }

    private fun onItemSelectedListener(item: MenuItem): Boolean {
        val intent = when (item.itemId) {
            R.id.cuenta -> Intent(this, AuthActivity::class.java)
            R.id.carrito -> Intent(this, CarritoActivity::class.java)
            R.id.selector -> Intent(this, SubidaActivity::class.java)
            R.id.buscar -> Intent(this, BusquedaActivity::class.java)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_JSON_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                jsonContent = readTextFromUri(uri)
                uploadFileButton.isEnabled = jsonContent != null // Enable the upload button if JSON is not null
            }
        }
    }

    private fun readTextFromUri(uri: Uri): String {
        return contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val stringBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                stringBuilder.toString()
            }
        } ?: "" // Returns an empty string in case of error
    }

    private fun parseAndUploadJson(jsonContent: String) {
        val gson = Gson()
        try {
            val listaProductos = gson.fromJson(jsonContent, ListaProductos::class.java)
            listaProductos.productos.forEach { producto ->
                uploadProductoToFirestore(producto)
            }
        } catch (e: Exception) {
            Log.e("SubidaActivity", "Error parsing JSON", e)
        }
    }

    private fun uploadProductoToFirestore(producto: Producto) {
        val db = FirebaseFirestore.getInstance()
        val productoMap = hashMapOf(
            "nombre" to producto.nombre,
            "descripcion" to producto.descripcion,
            "precio" to producto.precio,
            "cantidad" to producto.cantidad,
            "vendedor" to producto.vendedor
        )
        db.collection("productosSinId").add(productoMap)
            .addOnSuccessListener { documentReference ->
                // Aquí se obtiene correctamente el ID del documento recién creado.
                producto.id = documentReference.id
                Log.d("Firestore", ": ${producto.id}")
                db.collection("productos").document(producto.id).set(producto)

                Log.d("Firestore", "Producto añadido con éxito: ${producto.nombre}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al añadir producto", e)
            }
    }

    companion object {
        private const val PICK_JSON_FILE_REQUEST_CODE = 1
    }
}
