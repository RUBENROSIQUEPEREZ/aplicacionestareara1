package com.example.myapplication1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication1.R
import com.example.myapplication1.databinding.FragmentTabBinding
import com.example.myapplication1.viewpager.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class TabFragment : Fragment() {

    // variable para poder acceder a los elementos del xml
    private var _binding: FragmentTabBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // cargamos el diseño visual de este fragmento
        _binding = FragmentTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // creamos el adaptador que se encarga de cambiar los fragmentos
        val adapter = ViewPagerAdapter(this)

        // le decimos al viewpager que use ese adaptador para funcionar
        binding.viewPager.adapter = adapter

        // conectamos las pestañas de arriba con el deslizador de abajo
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // ponemos los nombres a cada pestaña segun su posicion
            when (position) {
                0 -> tab.text = getString(R.string.tab_list)
                1 -> tab.text = getString(R.string.tab_favorites)
            }
        }.attach() // esto es necesario para que se active la conexion
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // limpiamos la variable binding para liberar memoria
        _binding = null
    }
}