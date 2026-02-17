package com.example.myapplication1.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication1.R
import com.example.myapplication1.databinding.ItemLayoutBinding

class BookAdapter(
    // 1. Quitamos la lista del constructor. Solo dejamos configuraciones y listeners.
    private val isFavoriteClickable: Boolean = true,
    private val onFavoriteClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    // 2. Creamos una variable pública para la lista de datos
    var books: List<Book> = emptyList()
        set(value) {
            field = value
            // 3. Importante: Avisamos a la lista de que los datos han cambiado para que se repinte
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BookViewHolder(layoutInflater.inflate(R.layout.item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        // Usamos la variable 'books' en lugar de 'bookList'
        val item = books[position]
        holder.render(item)
    }

    // Usamos el tamaño de 'books'
    override fun getItemCount(): Int = books.size

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemLayoutBinding.bind(view)

        fun render(book: Book) {
            binding.tvTitle.text = book.title
            binding.tvDescription.text = book.description
            // Asegúrate de que tu clase Book tenga 'imageResId' o cambia esto si usas URLs
            binding.ivItemImage.setImageResource(book.imageResId)

            // Limpiamos el listener previo para evitar conflictos al reciclar vistas
            binding.cbFavorite.setOnCheckedChangeListener(null)

            // Asignamos el estado actual
            binding.cbFavorite.isChecked = book.isFavorite
            binding.cbFavorite.isEnabled = isFavoriteClickable

            // Configuramos el nuevo listener
            binding.cbFavorite.setOnCheckedChangeListener { _, isChecked ->
                // Actualizamos el objeto localmente para reflejar el cambio inmediato
                book.isFavorite = isChecked
                // Avisamos al fragmento (que llamará al ViewModel -> Firestore)
                onFavoriteClick(book)
            }
        }
    }
}