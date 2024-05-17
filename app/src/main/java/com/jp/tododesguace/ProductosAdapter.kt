package com.jp.tododesguace

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.DecimalFormat

class ProductosAdapter(
    val productos: MutableList<Producto>
) : RecyclerView.Adapter<ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carrito, parent, false)
        return ProductoViewHolder(view)
    }

    override fun getItemCount() = productos.size

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto: Producto = productos[position]
        val tamano = productos.size
        val item = productos[position]


        holder.render(item)
        holder.deleteBtn.setOnClickListener {
            if (producto.id.isNotEmpty()) {
                FirebaseFirestore.getInstance().collection("carrito").document(producto.id)
                    .delete()
                    .addOnSuccessListener {
                        productos.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, productos.size)
                        Log.d("Firestore", "Producto eliminado correctamente")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error al eliminar el producto", e)
                    }
            } else {
                Log.w("Firestore", "Producto sin ID válido")
            }
        }

        holder.itemView.setOnClickListener {
            val context = it.context
            val intent = Intent(context, DetalleProductoActivity::class.java)
            intent?.putExtra("id", item.id)
            intent?.putExtra("nombre", item.nombre)
            intent?.putExtra("descripcion", item.descripcion)
            intent?.putExtra("precio", item.precio)
            intent?.putExtra("vendedor", item.vendedor)
            intent?.putExtra("cantidad", item.cantidad)


            holder.itemView.context.startActivity(intent) // Iniciar la actividad con el intent configurado
        }
    }

}

class ProductoViewHolder(view: View): RecyclerView.ViewHolder(view){

    val nombre = view.findViewById<TextView>(R.id.textViewNombreProducto)
    val descripcion = view.findViewById<TextView>(R.id.textViewDescripcionProducto)
    val precio = view.findViewById<TextView>(R.id.textViewPrecioProducto)
    val vendedor = view.findViewById<TextView>(R.id.textViewVendedorProducto)
    val cantidad = view.findViewById<TextView>(R.id.textViewCantidad)
    val deleteBtn = view.findViewById<ImageButton>(R.id.buttonDeleteProduct)
    val imagen: ImageView = view.findViewById(R.id.imageViewProducto)



    fun render(producto: Producto){
        nombre.text = producto.nombre
        descripcion.text = producto.descripcion
        precio.text = producto.precio
        vendedor.text = producto.vendedor
        cantidad.text = producto.cantidad

        val storageRef = FirebaseStorage.getInstance().reference.child("images/${producto.id}")
        storageRef.downloadUrl.addOnSuccessListener { uri ->

            Glide.with(itemView.context)
                .load(uri)
                .into(imagen)
        }
    }
    

}
