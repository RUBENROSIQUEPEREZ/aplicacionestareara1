package com.example.myapplication1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication1.R
import com.example.myapplication1.databinding.FragmentLoginBinding
import com.example.myapplication1.viewmodels.AuthViewModel

class LoginFragment : Fragment() {

    // --- CONFIGURACIÓN DE VIEWBINDING ---
    // _binding: Variable privada y mutable que sirve para acceder a los elementos del XML.
    // Puede ser nula porque la vista se destruye antes que el fragmento.
    private var _binding: FragmentLoginBinding? = null

    // binding: Variable pública que usamos en el código.
    // El '!!' asegura que, cuando la usemos, no sea nula.
    private val binding get() = _binding!!

    // --- INSTANCIA DEL VIEWMODEL (EL CEREBRO) ---
    // Usamos el delegado 'by viewModels()'.
    // Esto crea el ViewModel la primera vez y lo recupera automáticamente si giras la pantalla.
    private val viewModel: AuthViewModel by viewModels()

    //CREACIÓN DE LA VISTA
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // "Inflamos" el diseño XML para convertirlo en código Kotlin utilizable
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    //METODOS CUANDO LA VISTA ESTA CREADA
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputs()     // 1. INPUTS: Escuchar lo que escribe el usuario
        setupObservers()  // 2. OUTPUTS: Reaccionar a lo que dice el ViewModel
        setupListeners()  // 3. EVENTOS: Gestionar los clics de los botones
    }


    private fun setupInputs() {
        // Cada vez que el usuario escribe una letra en el campo "Usuario"...
        binding.tilUsername.editText?.doOnTextChanged { text, _, _, _ ->
            // ...se lo enviamos al ViewModel para que lo guarde y valide.
            viewModel.onUsernameChanged(text.toString())
        }

        // Lo mismo para el campo "Contraseña"
        binding.tilPassword.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.onPasswordChanged(text.toString())
        }
    }

    // --- SALIDA DE DATOS (ViewModel -> Vista) ---
    private fun setupObservers() {
        // OBSERVADOR 1: Estado del Botón
        // Nos quedamos vigilando la variable 'isLoginButtonEnabled' del ViewModel.
        viewModel.isLoginButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            // Si el ViewModel dice 'true', activamos el botón. Si dice 'false', lo desactivamos.
            binding.btnLogin.isEnabled = isEnabled
        }

        // OBSERVADOR 2: Resultado del Login
        // Nos quedamos vigilando si el login fue exitoso o fallido.
        viewModel.loginResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                // CASO ÉXITO:
                // 1. Damos feedback positivo al usuario (usando strings.xml)
                Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show()

                // 2. NAVEGACIÓN:
                // Usamos el componente Navigation para ir a la pantalla principal (Tabs).
                // Al usar el ID del 'action', nos aseguramos de que borre el historial (popUpTo) si así lo configuramos.
                findNavController().navigate(R.id.action_loginFragment_to_tabFragment)
            } else {
                // CASO ERROR:
                // Avisamos al usuario de que los datos están mal.
                Toast.makeText(requireContext(), getString(R.string.login_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        // Clic en el botón "Iniciar Sesión"
        binding.btnLogin.setOnClickListener {
            // Le pedimos al ViewModel que intente hacer el login con los datos que ya tiene.
            viewModel.performLogin()
        }

        // Clic en el texto/botón "Crear cuenta"
        binding.tvCreateAccount.setOnClickListener {
            // Navegamos hacia la pantalla de Registro usando la flecha definida en nav_graph.xml
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    //libera memoria
    override fun onDestroyView() {
        super.onDestroyView()
        // Es obligatorio poner el binding a null aquí.
        // Esto evita "fugas de memoria" porque el Fragmento vive más tiempo que su Vista.
        _binding = null
    }
}