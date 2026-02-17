package com.example.myapplication1.fragments

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.* // Importamos todo lo necesario para menús
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView // Importante para la lupa
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // ¡IMPORTANTE!
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication1.R
import com.example.myapplication1.databinding.FragmentListBinding
import com.example.myapplication1.di.ServiceLocator
import com.example.myapplication1.di.ServiceLocator.authRepository
import com.example.myapplication1.di.ServiceLocator.bookRepository
import com.example.myapplication1.recycler.BookAdapter
import com.example.myapplication1.viewmodels.ListViewModel
import com.example.myapplication1.recycler.Book
import com.example.myapplication1.viewmodels.ListViewModelFactory
import kotlinx.coroutines.launch

class ListFragment : Fragment() {

    // Variable para acceder a los elementos visuales (el XML) de forma segura
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    // CEREBRO COMPARTIDO:
    // Usamos 'activityViewModels' igual que en Favoritos.
   // private val viewModel: ListViewModel by activityViewModels()

    private val viewModel: ListViewModel by activityViewModels {
        ListViewModelFactory(
            ServiceLocator.bookRepository,
            ServiceLocator.authRepository
        )
    }

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

    private lateinit var adapter: BookAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupRecyclerView()

        setupListeners()

        setupObservers()

        viewModel.loadBooks()

        // 1. Configuración inicial del RecyclerView
        binding.rvBooks.layoutManager = LinearLayoutManager(context)
        // Asegúrate de inicializar tu adapter aquí si no lo has hecho
        // val adapter = BookAdapter(...)
        // binding.rvBooks.adapter = adapter

        // 2. Configuración del Menú (Búsqueda y Ordenación)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)

                val itemBusqueda = menu.findItem(R.id.action_search)
                val searchView = itemBusqueda.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean { return false }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        // Mantenemos tu lógica local de filtrado
                        filtrarLista(newText)
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_sort -> {
                        // Ordenamos la lista localmente
                        val listaOrdenada = listaCompleta.sortedBy { it.title }
                        actualizarAdapter(listaOrdenada)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED) // [2]

        // 3. Botón Flotante (Añadir Libro a Firestore)
        binding.fab.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_add_book, null)

            builder.setView(dialogView)
                .setTitle("Añadir Nuevo Elemento")
                .setPositiveButton("Guardar") { dialog, id ->
                    val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
                    val etDesc = dialogView.findViewById<EditText>(R.id.etDesc)

                    val titleText = etTitle.text.toString()
                    val descText = etDesc.text.toString()

                    if (titleText.isNotEmpty() && descText.isNotEmpty()) {
                        // Generamos datos temporales necesarios para tu clase Book
                        val randomId = (0..1000000).random()
                        val defaultImage = R.drawable.ic_launcher_foreground

                        val newBook = Book(
                            id = randomId,
                            title = titleText,
                            description = descText,
                            imageResId = defaultImage,
                            isFavorite = false
                        )

                        // Llamamos al ViewModel que guardará en Firestore
                        viewModel.addBook(newBook)

                    } else {
                        Toast.makeText(context, "Por favor, rellena los campos", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar") { dialog, id ->
                    dialog.cancel()
                }
            builder.create().show()
        }

        // 4. OBSERVACIÓN DE DATOS (El cambio principal)
        // Usamos corrutinas para recolectar el StateFlow que viene de Firestore
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.books.collect { bookList ->
                    // Actualizamos la lista maestra con los datos reales de la nube
                    listaCompleta = bookList

                    // Actualizamos la interfaz
                    actualizarAdapter(listaCompleta)
                }
            }
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

    private fun setupListeners() {
        // 1. Buscamos el FAB que vive en la MainActivity (no en el fragmento)
        val fab = requireActivity().findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab)

        // 2. Le asignamos el listener
        fab.setOnClickListener {
            showAddBookDialog()
        }

        // Opcional: Asegurarnos de que se vea (por si se ocultó en otra pantalla)
        fab.show()
    }

    private fun showAddBookDialog() {
        // 1. Inflar la vista del diálogo
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_book, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDesc)

        // 2. Construir el AlertDialog usando el patrón Builder
        AlertDialog.Builder(requireContext())
            .setTitle("Añadir nuevo libro")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val title = etTitle.text.toString().trim()
                val description = etDescription.text.toString().trim()

                if (title.isNotEmpty() && description.isNotEmpty()) {
                    // 3. Crear el objeto libro
                    // Nota: Firestore asignará su propio ID de documento, pero aquí creamos el objeto local
                    val newBook = Book(
                        id = System.currentTimeMillis().toInt(), // ID temporal numérico
                        title = title,
                        description = description,
                        imageResId = R.drawable.ic_launcher_foreground, // Imagen por defecto
                        isFavorite = false
                    )

                    // 4. Llamar al ViewModel para guardar en Firestore
                    viewModel.addBook(newBook)
                } else {
                    Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupRecyclerView() {
        // Configuramos el adaptador. Le pasamos la función para cuando se pulsa el corazón (favorito)
        adapter = BookAdapter { book ->
            // Lógica al pulsar favorito (llamamos al ViewModel)
            viewModel.toggleFavorite(book)
        }

        // Asignamos el adaptador y el diseño al RecyclerView de tu xml (binding.rvBooks)
        binding.rvBooks.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.rvBooks.adapter = adapter
    }

    private fun setupObservers() {
        // Observamos la lista de libros del ViewModel (StateFlow)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.books.collect { listaDeLibros ->
                    // Cuando llegan datos nuevos de Firestore:
                    if (listaDeLibros.isEmpty()) {
                        // Opcional: Mostrar un texto de "No hay libros" y ocultar la lista
                        binding.rvBooks.visibility = View.GONE
                        // binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        binding.rvBooks.visibility = View.VISIBLE
                        // Actualizamos los datos del adaptador
                        adapter.books = listaDeLibros
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    // --- MODIFICADO: He movido tu lógica de crear el adaptador aquí para reutilizarla ---
    private fun actualizarAdapter(lista: List<Book>) {
        // Intentamos obtener el adaptador existente
        val adapter = binding.rvBooks.adapter as? BookAdapter

        if (adapter != null) {
            // Si ya existe, solo actualizamos sus datos y notificamos
            adapter.books = lista
            adapter.notifyDataSetChanged()
        } else {
            // Si es la primera vez (es nulo), lo creamos desde cero
            val newAdapter = BookAdapter { book ->
                onFavoriteClicked(book)
            }
            binding.rvBooks.adapter = newAdapter
        }
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

    override fun onResume() {
        super.onResume()
        // Cuando entramos en la lista, mostramos el botón
        val fab = requireActivity().findViewById<View>(R.id.fab)
        fab.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        // Cuando salimos de la lista (ej. a Favoritos), lo ocultamos
        val fab = requireActivity().findViewById<View>(R.id.fab)
        fab.visibility = View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Limpieza de memoria
        _binding = null
    }

}