package com.example.myapplication1.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.myapplication1.R
import com.example.myapplication1.databinding.FragmentRegisterBinding
import com.example.myapplication1.di.ServiceLocator
import com.example.myapplication1.viewmodels.NewUserUiState
import com.example.myapplication1.viewmodels.NewUserViewModel
import com.example.myapplication1.viewmodels.NewUserViewModelFactory
import kotlinx.coroutines.launch
import java.util.Calendar

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Inyección del ViewModel usando la Factory y ServiceLocator (Correcto según Tarea 4)
    private val viewModel: NewUserViewModel by viewModels {
        NewUserViewModelFactory(
            ServiceLocator.authRepository,
            ServiceLocator.userRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialmente deshabilitamos el botón hasta que los datos sean válidos
        binding.btnRegister.isEnabled = false

        setupInputs()
        setupObservers()
        setupButtons()
    }

    private fun setupInputs() { // FALTABA ESTA LLAVE DE APERTURA {

        // 1. Email / Usuario
        binding.tilRegisterUsername.editText?.doOnTextChanged { text, _, _, _ ->
            // Enviamos el dato al ViewModel
            viewModel.username = text.toString()
            // Comprobamos si podemos activar el botón
            checkFormState()
        }

        // 2. Contraseña
        binding.tilRegisterPassword.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.password = text.toString()
            checkFormState()
        }

        // 3. Confirmar Contraseña
        binding.tilRegisterConfirmPassword.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.confirmPassword = text.toString()
            checkFormState()
        }

        // 4. Fecha de nacimiento
        binding.etBirthDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Botón para ir al Login si ya tienes cuenta
        binding.tvLogin?.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    // FUNCIÓN NUEVA: Valida el formulario en tiempo real
    private fun checkFormState() {
        val username = binding.tilRegisterUsername.editText?.text.toString().trim()
        val password = binding.tilRegisterPassword.editText?.text.toString().trim()
        val confirmPassword = binding.tilRegisterConfirmPassword.editText?.text.toString().trim()
        val birthDate = binding.etBirthDate.text.toString().trim()

        // REGLAS DE VALIDACIÓN (Tarea 4):
        val isUsernameValid = username.isNotEmpty()
        // La contraseña debe tener al menos 6 caracteres [1]
        val isPasswordValid = password.length >= 6
        val doPasswordsMatch = password == confirmPassword
        val isDateValid = birthDate.isNotEmpty()

        // El botón solo se activa si TODO es correcto
        binding.btnRegister.isEnabled = isUsernameValid && isPasswordValid && doPasswordsMatch && isDateValid
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observamos el StateFlow (Requisito Tarea 4) [2]
                viewModel.uiState.collect { state ->
                    when(state) {
                        is NewUserUiState.Idle -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnRegister.isEnabled = true // Reactivar si hubo error y se corrigió
                        }
                        is NewUserUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnRegister.isEnabled = false // Evitar doble click
                        }
                        is NewUserUiState.Created -> { // Usamos Created en lugar de Authenticated [3]
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(context, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                        is NewUserUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnRegister.isEnabled = true
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            // Al hacer clic, el ViewModel lanza la corrutina para crear el usuario
            viewModel.createAccount()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Nota: Sumamos 1 al mes porque Calendar.MONTH empieza en 0
                val selectedDate = "$dayOfMonth/${month + 1}/$year"

                binding.etBirthDate.setText(selectedDate)
                viewModel.birthDate = selectedDate

                // IMPORTANTE: Validar también cuando cambiamos la fecha
                checkFormState()
            },
            currentYear,
            currentMonth,
            currentDay
        )
        datePicker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}