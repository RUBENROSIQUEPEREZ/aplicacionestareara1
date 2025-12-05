package com.example.myapplication1.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // ¡IMPORTANTE!
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication1.R
import com.example.myapplication1.databinding.FragmentListBinding
import com.example.myapplication1.recycler.BookAdapter
import com.example.myapplication1.viewmodels.ListViewModel
import com.example.myapplication1.recycler.Book

class ListFragment : Fragment() {

    // Variable para acceder a los elementos visuales (el XML) de forma segura
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    // CEREBRO COMPARTIDO:
    // Usamos 'activityViewModels' igual que en Favoritos.
    // Esto asegura que ambas pantallas miren a la misma lista de datos.
    // Si cambias algo aquí, la pantalla de Favoritos se entera al instante.
    private val viewModel: ListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // "Inflamos" (cargamos) el diseño visual de la lista
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Preparamos la estantería (RecyclerView) para que sea una lista vertical
        binding.rvBooks.layoutManager = LinearLayoutManager(context)

        // Nos quedamos vigilando la lista maestra del ViewModel.
        // Este código salta automáticamente cuando la lista cambia (ej: al iniciar la app).
        viewModel.books.observe(viewLifecycleOwner) { bookList ->

            // Aquí le pasamos la lista COMPLETA ('bookList') al adaptador.
            // Queremos ver todos los libros, sean favoritos o no.

            // Al crear el adaptador, NO bloqueamos los clics.
            // Definimos qué pasa cuando el usuario toca la estrella: llamamos a 'onFavoriteClicked'.
            val adapter = BookAdapter(bookList) { book ->
                // Acción al pulsar la estrella: Avisar al ViewModel para que guarde el cambio
                onFavoriteClicked(book)
            }

            // Colocamos los libros en la estantería
            binding.rvBooks.adapter = adapter
        }
    }

    // Lógica al hacer clic en la estrella
    private fun onFavoriteClicked(book: Book) {
        // 1. Cambiamos el estado en el ViewModel (de true a false o viceversa)
        viewModel.toggleFavorite(book)

        // 2. Mostramos un mensaje de confirmación al usuario
        val mensaje = if (book.isFavorite) "Marcado como favorito" else "Desmarcado"

        val sonidofav = if (book.isFavorite) R.raw.sound1 else R.raw.sound1

        val mediaPlayer = MediaPlayer.create(context, sonidofav)
        mediaPlayer.start()

        Toast.makeText(context, "${book.title}: $mensaje", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpieza de memoria
        _binding = null
    }
}