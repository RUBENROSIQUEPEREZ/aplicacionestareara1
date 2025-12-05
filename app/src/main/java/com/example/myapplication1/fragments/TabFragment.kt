package com.example.myapplication1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication1.databinding.FragmentTabBinding
import com.example.myapplication1.viewpager.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class TabFragment : Fragment() {

    private var _binding: FragmentTabBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Instanciamos nuestro adaptador (El cerebro de las pestañas)
        val adapter = ViewPagerAdapter(this)

        // 2. Se lo asignamos al ViewPager (El visualizador)
        binding.viewPager.adapter = adapter

        // 3. La MAGIA: TabLayoutMediator conecta las pestañas con el ViewPager
        // Aquí definimos qué título lleva cada pestaña
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Lista"      // Título de la primera pestaña
                1 -> tab.text = "Favoritos"  // Título de la segunda
            }
        }.attach() // ¡Muy importante llamar a attach()!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}