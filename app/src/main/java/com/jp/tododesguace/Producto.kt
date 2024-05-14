package com.jp.tododesguace

data class Producto(
    var id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "0.0",
    val vendedor: String = "",
    val cantidad: String= ""

)


data class ListaProductos(
    val productos: List<Producto>
)
