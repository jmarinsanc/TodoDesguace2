package com.jp.tododesguace

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class ProductoBusquedaAdaptador(var listaProductos: List<Producto>): RecyclerView.Adapter<ProductoBusquedaAdaptador.ViewHolder>() {
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById<TextView>(R.id.textViewNombreProducto)
        val descripcion = view.findViewById<TextView>(R.id.textViewDescripcionProducto)
        val precio = view.findViewById<TextView>(R.id.textViewPrecioProducto)
        val vendedor = view.findViewById<TextView>(R.id.textViewVendedorProducto)
        val cantidad = view.findViewById<TextView>(R.id.textViewCantidad)
        val imagen: ImageView = view.findViewById(R.id.imageViewProducto)


        fun render(producto: Producto) {
            nombre.text = producto.nombre
            descripcion.text = producto.descripcion
            precio.text = producto.precio.toString()
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductoBusquedaAdaptador.ViewHolder {
        val vista  = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ProductoBusquedaAdaptador.ViewHolder, position: Int) {
        val producto: Producto = listaProductos[position]
        val item = listaProductos[position]
        holder.render(item)
        holder.itemView.setOnClickListener {
            val context = it.context
            val intent = Intent(context, DetalleProductoActivity::class.java)
            intent.putExtra("origen", "ResultadosBusquedaActivity")
            intent?.putExtra("id", item.id)
            intent?.putExtra("descripcion", item.descripcion)
            intent?.putExtra("nombre", item.nombre)
            intent?.putExtra("precio", item.precio)
            intent?.putExtra("vendedor", item.vendedor)
            intent?.putExtra("cantidad", item.cantidad)

            holder.itemView.context.startActivity(intent)
        }
    }

        override fun getItemCount() = listaProductos.size

    fun filtrar(listaFiltrada: List<Producto>){
        this.listaProductos = listaFiltrada
        notifyDataSetChanged()
    }

}