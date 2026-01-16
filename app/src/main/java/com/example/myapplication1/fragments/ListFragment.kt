package com.example.myapplication1.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.* // Importamos todo lo necesario para menús
import android.widget.Toast
import androidx.appcompat.widget.SearchView // Importante para la lupa
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // ¡IMPORTANTE!
import androidx.lifecycle.Lifecycle
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
    private val viewModel: ListViewModel by activityViewModels()

    // NUEVO: Variable para guardar TODOS los libros originales (Copia de seguridad para el buscador)
    private var listaCompleta: List<Book> = ArrayList()

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

        // --- NUEVO BLOQUE: CONFIGURACIÓN DEL MENÚ (Lupa y Ordenar) ---
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Inflamos el menú que creaste (toolbar_menu.xml)
                menuInflater.inflate(R.menu.toolbar_menu, menu)

                // Lógica de la Lupa (Buscador)
                val itemBusqueda = menu.findItem(R.id.action_search)
                val searchView = itemBusqueda.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean { return false }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        // Cada vez que escribes, filtramos
                        filtrarLista(newText)
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_sort -> {
                        // Lógica de ordenar (alfabéticamente por título)
                        val listaOrdenada = listaCompleta.sortedBy { it.title }
                        actualizarAdapter(listaOrdenada)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        // -------------------------------------------------------------

        // Nos quedamos vigilando la lista maestra del ViewModel.
        // Este código salta automáticamente cuando la lista cambia (ej: al iniciar la app).
        viewModel.books.observe(viewLifecycleOwner) { bookList ->
            // NUEVO: Guardamos la copia completa antes de mostrar nada
            listaCompleta = bookList

            // Usamos la función auxiliar para mostrar la lista
            actualizarAdapter(listaCompleta)
        }
    }

    // --- NUEVO: Función auxiliar para filtrar la lista ---
    private fun filtrarLista(texto: String?) {
        if (texto.isNullOrEmpty()) {
            // Si borras el texto, mostramos la copia de seguridad completa
            actualizarAdapter(listaCompleta)
        } else {
            // Filtramos buscando coincidencias (ignorando mayúsculas/minúsculas)
            val listaFiltrada = listaCompleta.filter { book ->
                book.title.contains(texto, ignoreCase = true)
            }
            actualizarAdapter(listaFiltrada)
        }
    }

    // --- MODIFICADO: He movido tu lógica de crear el adaptador aquí para reutilizarla ---
    private fun actualizarAdapter(lista: List<Book>) {
        // Al crear el adaptador, NO bloqueamos los clics.
        // Definimos qué pasa cuando el usuario toca la estrella: llamamos a 'onFavoriteClicked'.
        val adapter = BookAdapter(lista) { book ->
            // Acción al pulsar la estrella: Avisar al ViewModel para que guarde el cambio
            onFavoriteClicked(book)
        }
        // Colocamos los libros en la estantería
        binding.rvBooks.adapter = adapter
    }

    // Lógica al hacer clic en la estrella (TU LÓGICA ORIGINAL INTACTA)
    private fun onFavoriteClicked(book: Book) {
        // 1. Cambiamos el estado en el ViewModel (de true a false o viceversa)
        viewModel.toggleFavorite(book)

        // 2. Mostramos un mensaje de confirmación al usuario
        // CAMBIO: Usamos getString() para leer el idioma correcto
        val mensaje = if (book.isFavorite) {
            getString(R.string.msg_fav_added)
        } else {
            getString(R.string.msg_fav_removed)
        }

        val sonidofav = if (book.isFavorite) R.raw.sound else R.raw.sound1

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