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
import com.example.myapplication1.databinding.FragmentRegisterBinding
import com.example.myapplication1.viewmodels.NewUserViewModel

class RegisterFragment : Fragment() {

    // Accedo por ViewBinding, permite acceder a las vistas del layout de forma segura
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Instancia del ViewModel usando el delegado 'by viewModels()'
    // Esto hace que sobreviva a cambios de configuracion
    private val viewModel: NewUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Se "infla" y creamos el binding
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputs() // Captura entrada de texto del usuario
        setupObservers()  //Observa LiveData del ViewModel
        setupButtons() // Acciones del botón de registro
    }

    // 1. Capturar lo que escribe el usuario
    private fun setupInputs() {
        // Captura del nombre de usuario
        binding.tilRegisterUsername.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.username = text.toString()
        }

        // Captura de la contraseña
        binding.tilRegisterPassword.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.password = text.toString()
        }

        // Captura de la confirmación de contraseña
        binding.tilRegisterConfirmPassword.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.confirmPassword = text.toString()
        }
    }

    // 2. Observar los cambios del ViewModel (LiveData), sin necesidad de comprobar valores manualmente
    private fun setupObservers() {
        // Habilitar/Deshabilitar botón
        //Con viewLifeCycleOwner se esta observando siempre
        viewModel.isRegisterButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnRegister.isEnabled = isEnabled
        }

        // Mostrar/Ocultar error de contraseña
        viewModel.passwordMatchError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                // Mostramos el error en el TextInputLayout de confirmar contraseña
                binding.tilRegisterConfirmPassword.error = getString(R.string.error_password_mismatch) // res/strings.xmls
            } else {
                // Quitamos el error (null)
                binding.tilRegisterConfirmPassword.error = null
            }
        }

        // Navegar si el registro es exitoso
        viewModel.registrationSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                // Navegar de vuelta al Login limpiando la pila (definido en nav_graph)
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    // 3. Configurar clic del botón
    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            viewModel.createAccount()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Evitamos fugas de memoria asociadas al ViewBinding
        _binding = null
    }
}