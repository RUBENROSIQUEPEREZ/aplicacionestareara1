package com.example.myapplication1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication1.di.ServiceLocator
import com.example.myapplication1.recycler.BookAdapter
import com.example.myapplication1.viewmodels.ListViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapplication1.databinding.FragmentListBinding // Reutilizamos el layout de lista o crea FragmentFavBinding
import com.example.myapplication1.viewmodels.ListViewModelFactory
import kotlinx.coroutines.launch

class FavFragment : Fragment() {

    class FavFragment : Fragment() {

        private var _binding: FragmentListBinding? = null

        // Si usas un layout distinto para favoritos, cambia el tipo de binding
        private val binding get() = _binding!!

        // SOLUCIÓN AL ERROR:
        // Usamos 'activityViewModels' para compartir los datos con ListFragment
        // y usamos la Factory para que el repositorio funcione.
        private val viewModel: ListViewModel by activityViewModels {
            ListViewModelFactory(
                ServiceLocator.bookRepository,
                ServiceLocator.authRepository
            )
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            // Puedes reutilizar fragment_list.xml si solo tiene un RecyclerView y ProgressBar
            _binding = FragmentListBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // 1. Configurar RecyclerView
            binding.rvBooks.layoutManager = LinearLayoutManager(context)

            // Ocultamos el botón flotante (FAB) porque en favoritos no se añade
            binding.fab.visibility = View.GONE

            // 2. Crear el Adaptador
            // REGLA TAREA 2: "El botón de favorito debe estar bloqueado en la lista de favoritos" [1]
            val adapter = BookAdapter(
                isFavoriteClickable = false, // Bloqueamos el click
                onFavoriteClick = { book ->
                    // Si el usuario intenta clicar (aunque esté bloqueado visualmente),
                    // mostramos un aviso en lugar de borrarlo.
                    Toast.makeText(
                        context,
                        "Gestione los favoritos desde la lista principal",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
            binding.rvBooks.adapter = adapter

            // 3. Observar datos con StateFlow
            viewLifecycleOwner.lifecycleScope.launch {
                // Solución al error de extensión: llamar a repeatOnLifecycle sobre 'lifecycle'
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.books.collect { allBooks ->
                        // Filtramos solo los favoritos
                        val favoriteBooks = allBooks.filter { it.isFavorite }

                        adapter.books = favoriteBooks
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
}