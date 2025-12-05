package com.example.myapplication1.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication1.R
import com.example.myapplication1.databinding.ItemLayoutBinding

class BookAdapter(
    private val bookList: List<Book>, // la lista de datos que vamos a mostrar
    private val isFavoriteClickable: Boolean = true, // nuevo interruptor para saber si se puede tocar la estrella o no
    private val onFavoriteClick: (Book) -> Unit // funcion que se ejecuta al hacer clic
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    // este metodo crea el hueco visual cargando el diseño xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BookViewHolder(layoutInflater.inflate(R.layout.item_layout, parent, false))
    }

    // este metodo se ejecuta una y otra vez para rellenar los datos en cada hueco
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val item = bookList[position]
        holder.render(item)
    }

    // le dice a la lista cuantos elementos hay en total
    override fun getItemCount(): Int = bookList.size

    // clase interna que guarda las referencias a los textos e imagenes para ir mas rapido
    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemLayoutBinding.bind(view)

        fun render(book: Book) {
            // asignamos los textos y la imagen al diseño
            binding.tvTitle.text = book.title
            binding.tvDescription.text = book.description
            binding.ivItemImage.setImageResource(book.imageResId)

            // 1. aplicamos el estado del favorito
            // quitamos el listener temporalmente para que no salte solo al hacer scroll
            binding.cbFavorite.setOnCheckedChangeListener(null)
            // marcamos o desmarcamos la estrella segun venga en el dato
            binding.cbFavorite.isChecked = book.isFavorite

            // 2. aplicamos el bloqueo
            // aqui decidimos si el usuario puede tocar el boton o esta bloqueado
            binding.cbFavorite.isEnabled = isFavoriteClickable

            // 3. listener real
            binding.cbFavorite.setOnCheckedChangeListener { _, isChecked ->
                // guardamos el cambio en el objeto
                book.isFavorite = isChecked
                // avisamos al fragmento de que han tocado el boton
                onFavoriteClick(book)
            }
        }
    }
}