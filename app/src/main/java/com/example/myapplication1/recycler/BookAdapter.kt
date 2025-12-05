package com.example.myapplication1.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication1.R
import com.example.myapplication1.databinding.ItemLayoutBinding

class BookAdapter(
    private val bookList: List<Book>,
    private val isFavoriteClickable: Boolean = true, // NUEVO PARÁMETRO (Interruptor)
    private val onFavoriteClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BookViewHolder(layoutInflater.inflate(R.layout.item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val item = bookList[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = bookList.size

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemLayoutBinding.bind(view)

        fun render(book: Book) {
            binding.tvTitle.text = book.title
            binding.tvDescription.text = book.description
            binding.ivItemImage.setImageResource(book.imageResId)

            // 1. Aplicamos el estado del favorito
            binding.cbFavorite.setOnCheckedChangeListener(null)
            binding.cbFavorite.isChecked = book.isFavorite

            // 2. APLICAMOS EL BLOQUEO (La lógica nueva)
            binding.cbFavorite.isEnabled = isFavoriteClickable

            // 3. Listener (Solo funcionará si isEnabled es true)
            binding.cbFavorite.setOnCheckedChangeListener { _, isChecked ->
                book.isFavorite = isChecked
                onFavoriteClick(book)
            }
        }
    }
}