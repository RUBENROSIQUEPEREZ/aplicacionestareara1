package com.example.myapplication1.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication1.databinding.FragmentPreferencesBinding
import java.util.Locale

class PreferencesFragment : Fragment() {

    private var _binding: FragmentPreferencesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- LÓGICA IDIOMA (YA LA TIENES) ---
        val currentLang = resources.configuration.locales[0].language
        binding.switchLanguage.isChecked = currentLang == "en"

        binding.switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cambiarIdioma("en") else cambiarIdioma("es")
        }

        // --- LÓGICA TEMA (NUEVO) ---

        // 1. Detectar si el modo oscuro está activo actualmente
        // Miramos la configuración del sistema para ver si está en NOCHE (YES)
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        binding.switchTheme.isChecked = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        // 2. Escuchar el cambio
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Activar Modo Oscuro
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                )
            } else {
                // Activar Modo Claro
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
        // --- LÓGICA NOTIFICACIONES (CORREGIDA CON MEMORIA) ---

        // 1. Abrimos el archivo de preferencias (La "libreta" donde apuntamos cosas)
        val prefs = requireActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)

        // 2. Leemos el estado guardado.
        // El segundo parámetro (true) es el valor por defecto si es la primera vez que abres la app.
        val notificacionesActivas = prefs.getBoolean("opcion_notificaciones", true)

        // 3. Ponemos el switch como debe estar ANTES de activar el listener
        binding.switchNotifications.isChecked = notificacionesActivas

        // 4. Escuchamos el cambio y GUARDAMOS el dato
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // Guardamos el nuevo valor en la libreta para que no se olvide
            prefs.edit().putBoolean("opcion_notificaciones", isChecked).apply()

            val mensaje = if (isChecked) {
                "Notificaciones ACTIVADAS"
            } else {
                "Notificaciones DESACTIVADAS"
            }
            android.widget.Toast.makeText(context, mensaje, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    // FUNCIÓN MÁGICA PARA CAMBIAR EL IDIOMA
    private fun cambiarIdioma(languageCode: String) {
        // 1. Crear la configuración con el nuevo idioma
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)

        // 2. Actualizar la configuración de la app
        resources.updateConfiguration(config, resources.displayMetrics)

        // 3. ¡IMPORTANTE! Reiniciar la actividad para ver los cambios
        requireActivity().recreate()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}