package com.jp.tododesguace

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var cerrarSesionBttn: Button
    private lateinit var guardarBtn: Button
    private lateinit var eliminarBtn: Button
    private lateinit var subirArchivoBtn: Button

    private lateinit var recuperarBtn: Button

    private lateinit var direccionEditText: EditText
    private lateinit var phoneEditText: EditText

    private lateinit var emailTextView: TextView
    private lateinit var contraTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        subirArchivoBtn = findViewById(R.id.subirArchivoButton)
        direccionEditText = findViewById(R.id.addressEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        guardarBtn = findViewById(R.id.guardarButton)
        eliminarBtn = findViewById(R.id.eliminarButon)
        recuperarBtn = findViewById(R.id.recuperarButon)
        cerrarSesionBttn = findViewById(R.id.cerrarSesionButton)
        emailTextView = findViewById(R.id.emailTextView)
        contraTextView = findViewById(R.id.providerTextView)

        val bundle: Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")
        setup(email ?: "", provider ?: "")

        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
        setupBottomMenu()
    }

    private fun setupBottomMenu() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.cuenta

        bottomNavigationView.setOnItemSelectedListener { item -> onItemSelectedListener(item) }
    }

    private fun onItemSelectedListener(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (item.itemId) {
            R.id.cuenta -> {
                val intent = Intent(this, AuthActivity::class.java)
                startActivityWithAnimation(intent)
            }
            R.id.carrito -> {
                val intent = Intent(this, CarritoActivity::class.java)
                startActivityWithAnimation(intent)
            }
            R.id.selector -> {
                val intent = Intent(this, SubidaActivity::class.java)
                startActivityWithAnimation(intent)
            }
            R.id.buscar -> {
                val intent = Intent(this, BusquedaActivity::class.java)
                startActivityWithAnimation(intent)
            }
        }
        return true
    }

    private fun startActivityWithAnimation(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun setup(email: String, provider: String) {
        title = "Detalle Usuario"
        emailTextView.text = email
        contraTextView.text = provider

        cerrarSesionBttn.setOnClickListener {
            val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

        guardarBtn.setOnClickListener {
            val phoneText = phoneEditText.text.toString()

            if (phoneText.length == 9 && phoneText.all { it.isDigit() }) {
                db.collection("users").document(email).set(
                    hashMapOf(
                        "provider" to provider,
                        "address" to direccionEditText.text.toString(),
                        "phone" to phoneText
                    )
                ).addOnSuccessListener {
                    Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar los datos: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "El número de teléfono debe tener 9 dígitos", Toast.LENGTH_SHORT).show()
            }
        }

        recuperarBtn.setOnClickListener {
            db.collection("users").document(email).get().addOnSuccessListener {
                println(it.get("address") as String?)
                direccionEditText.setText(it.get("address") as String?)
                phoneEditText.setText(it.get("phone") as String?)
            }
        }
        eliminarBtn.setOnClickListener {
            db.collection("users").document(email).delete()
        }

        subirArchivoBtn.setOnClickListener {
            val intent = Intent(this, SubidaActivity::class.java)
            startActivityWithAnimation(intent)
        }
    }
}
