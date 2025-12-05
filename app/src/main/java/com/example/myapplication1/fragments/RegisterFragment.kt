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

    // variable para acceder a los elementos visuales del xml de forma segura
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // conectamos con el viewmodel para que los datos no se pierdan al girar la pantalla
    private val viewModel: NewUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // cargamos el diseño visual de la pantalla de registro
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputs() // configuramos los campos de texto
        setupObservers()  // empezamos a vigilar los cambios del viewmodel
        setupButtons() // configuramos el boton de registrar
    }

    // funcion para capturar lo que escribe el usuario
    private fun setupInputs(){
        // cuando escribe el nombre de usuario se lo pasamos al viewmodel
        binding.tilRegisterUsername.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.username = text.toString()
        }

        // lo mismo para la contraseña
        binding.tilRegisterPassword.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.password = text.toString()
        }

        // y para confirmar la contraseña
        binding.tilRegisterConfirmPassword.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.confirmPassword = text.toString()
        }

        // para la fecha usamos un clic porque no dejamos escribir a mano
        binding.etBirthDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    // funcion para reaccionar a lo que decida el viewmodel
    private fun setupObservers() {
        // activamos o desactivamos el boton segun si el formulario esta completo
        viewModel.isRegisterButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnRegister.isEnabled = isEnabled
        }

        // si hay error de contraseñas mostramos el mensaje en rojo
        viewModel.passwordMatchError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                // mostramos el error usando el texto guardado en strings xml
                binding.tilRegisterConfirmPassword.error = getString(R.string.error_password_mismatch)
            } else {
                // quitamos el error
                binding.tilRegisterConfirmPassword.error = null
            }
        }

        // si el registro sale bien navegamos al login
        viewModel.registrationSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                // volvemos a la pantalla de login borrando el historial
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    // configuramos que pasa al pulsar el boton
    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            viewModel.createAccount()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // limpiamos la memoria al salir de la pantalla
        _binding = null
    }

    // funcion extra para enseñar el calendario
    private fun showDatePickerDialog() {
        // cogemos la fecha de hoy para que el calendario empiece ahi
        val calendar = java.util.Calendar.getInstance()
        val currentYear = calendar.get(java.util.Calendar.YEAR)
        val currentMonth = calendar.get(java.util.Calendar.MONTH)
        val currentDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        // creamos el dialogo del calendario
        val datePicker = android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // esto se ejecuta al pulsar ok en el calendario
                // formatea la fecha sumando 1 al mes porque empiezan en 0
                val selectedDate = "$dayOfMonth/${month + 1}/$year"

                // ponemos la fecha en la caja de texto
                binding.etBirthDate.setText(selectedDate)

                // y tambien la guardamos en el viewmodel
                viewModel.birthDate = selectedDate
            },
            currentYear,
            currentMonth,
            currentDay
        )

        // mostramos el calendario en pantalla
        datePicker.show()
    }
}