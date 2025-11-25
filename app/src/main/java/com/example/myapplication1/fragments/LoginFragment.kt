package com.example.myapplication1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged // Import para listeners de texto modernos
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Delegado para instaanciar ViewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication1.R
import com.example.myapplication1.databinding.FragmentLoginBinding
import com.example.myapplication1.viewmodels.AuthViewModel // Tu ViewModel
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    //ViewBinding: acceso seguro a las vistas
    //ViewBinding: acceso seguro a las vistas del layout
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // ViewModel usando el delegado 'by viewModels()'
    // Esta instancia sobrevive a los cambios de configuración.
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Se "infla" el layout y guardamos el binding
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        // Se devuelve la vista raíz del Binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuramos listeners de los EditText
        setupInputListeners()

        // Configuramos acciones de botones
        setupButtonListeners()

        // Observamos LiveData del ViewModel
        setupViewModelObservers()

        // Llamada inicial para evaluar si el botón debe empezar habilitado o no
        authViewModel.checkCredentialsValidity()
    }


    //Asocia los campos de texto del XML con las propiedades del ViewModel.
    //Cada vez que el usuario escribe, el ViewModel recibe el nuevo valor.
    private fun setupInputListeners() {

        // Listener para el campo usuario
        binding.tilUsername.editText?.doOnTextChanged { text, _, _, _ ->
            // Mandamos el texto al ViewModel
            authViewModel.setUsername(text.toString())
        }

        // Listener para el campo contraseña
        binding.tilPassword.editText?.doOnTextChanged { text, _, _, _ ->
            // Igual para la contraseña
            authViewModel.setPassword(text.toString())
        }
    }


     // Configura los listeners de clic de los botones.

    private fun setupButtonListeners(){

        // Botón de iniciar sesion
        binding.btnLogin.setOnClickListener {
            // Llama al ViewModel para realigar el login
            authViewModel.performLogin()
        }

        // Texto de crear cuenta en el Login | Navegacion al register fragment
        binding.tvCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Botón de iniciar sesion en GOOGLE (
        binding.btnGoogleLogin.setOnClickListener {
            Snackbar.make(
                binding.root,
                getString(R.string.snackbar_not_implemented), // res/strings.xmls
                Snackbar.LENGTH_LONG
            ).setAction(getString(R.string.snackbar_action_close)){}.show() // res/strings.xmls
        }
    }

    private fun setupViewModelObservers() {

        //Observa el estado del boton
        authViewModel.isLoginButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnLogin.isEnabled = isEnabled
        }

        //Observa el resultado del login
        authViewModel.loginResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                //Login correcto, mostramos el mensaje de bienvenida
                Toast.makeText(context, "Login Correcto. ¡Bienvenido, admin!", Toast.LENGTH_SHORT).show()
            } else {
                // Login fallido, muestra un mensaje de error
                Toast.makeText(context, "Credenciales incorrectas.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Evita fugas de memoria
        _binding = null
    }
}