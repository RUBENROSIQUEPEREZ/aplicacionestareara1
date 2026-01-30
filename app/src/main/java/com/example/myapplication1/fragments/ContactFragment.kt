package com.example.myapplication1.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication1.R
import com.example.myapplication1.databinding.FragmentContactBinding

class ContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. CONFIGURACIÓN DEL VIDEO DE FONDO ---
        val videoPath = "android.resource://" + requireActivity().packageName + "/" + R.raw.video_demo
        val uri = Uri.parse(videoPath)

        binding.videoBackground.setVideoURI(uri)

        // Listener para cuando el video esté listo para reproducirse
        binding.videoBackground.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true // Que se repita infinitamente

            // Ajuste para que el video llene la pantalla sin deformarse demasiado
            val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
            val screenRatio = binding.videoBackground.width / binding.videoBackground.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                binding.videoBackground.scaleX = scaleX
            } else {
                binding.videoBackground.scaleY = 1f / scaleX
            }

            binding.videoBackground.start()
        }

        // TELEFONO
        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+34123456789")
            startActivity(intent)
        }

        // WHATSAPP
        // Abre la app de WhatsApp con el número indicado
        binding.btnWhatsApp.setOnClickListener {
            val url = "https://wa.me/34123456789" // Número en formato internacional
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)

            // Un pequeño truco de seguridad: Try/Catch por si no tiene WhatsApp instalado
            try {
                startActivity(intent)
            } catch (e: Exception) {
                // Si no tiene WhatsApp, podríamos mostrar un Toast, pero por ahora no hacemos nada
            }
        }

        // EMAIL
        // ACTION_SENDTO con "mailto:" asegura que solo se abran apps de correo
        binding.btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:info@lalibreria.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta desde la app")
            startActivity(intent)
        }

    }

    // IMPORTANTE: Cuando salimos y volvemos, hay que asegurarse de que el video no se quede colgado
    override fun onResume() {
        super.onResume()
        binding.videoBackground.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}