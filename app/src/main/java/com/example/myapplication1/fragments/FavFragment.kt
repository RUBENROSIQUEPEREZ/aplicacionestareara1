package com.example.myapplication1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication1.databinding.FragmentFavBinding
import com.example.myapplication1.recycler.Book
import com.example.myapplication1.recycler.BookAdapter
import com.example.myapplication1.viewmodels.ListViewModel

class FavFragment : Fragment() {

    // Variable para poder tocar los elementos de la pantalla (el XML)
    private var _binding: FragmentFavBinding? = null
    private val binding get() = _binding!!

    // CONEXIÓN COMPARTIDA:
    // Usamos 'activityViewModels' para "enchufarnos" a los mismos datos que la otra pestaña.
    // Así, si marcas un favorito en la Lista, esta pantalla se entera automáticamente.
    private val viewModel: ListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Carga el diseño visual de la pantalla
        _binding = FragmentFavBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Le decimos a la lista que muestre los elementos uno debajo de otro (vertical)
        binding.rvFavs.layoutManager = LinearLayoutManager(context)

        // OBSERVADOR:
        // Nos quedamos vigilando la lista de libros. Si algo cambia, este código se ejecuta.
        viewModel.books.observe(viewLifecycleOwner) { bookList ->

            // EL FILTRO (La clave de esta pantalla):
            // De toda la lista de libros, creamos una nueva lista SOLO con los que tienen el corazón marcado.
            val favoriteBooks = bookList.filter { it.isFavorite }

            // PREPARAMOS LOS DATOS PARA PINTARLOS:
            // Creamos el adaptador pasándole un 'false'.
            // Esto significa: "Muestra la estrella, pero NO dejes que el usuario la toque aquí".
            val adapter = BookAdapter(favoriteBooks, isFavoriteClickable = false) { book ->
                // Este código es de seguridad, por si acaso el click funcionara.
                onFavoriteClicked(book)
            }

            // Finalmente, ponemos los libros filtrados en la pantalla
            binding.rvFavs.adapter = adapter
        }
    }

    // Esta función maneja lo que pasa al hacer clic (mostrar mensaje y actualizar datos)
    private fun onFavoriteClicked(book: Book) {
        viewModel.toggleFavorite(book)
        val mensaje = if (book.isFavorite) "Marcado como favorito" else "Desmarcado"
        Toast.makeText(context, "${book.title}: $mensaje", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpieza para no ocupar memoria cuando salimos de esta pantalla
        _binding = null
    }
}