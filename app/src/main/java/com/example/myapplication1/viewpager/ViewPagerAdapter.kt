package com.example.myapplication1.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication1.fragments.FavFragment
import com.example.myapplication1.fragments.ListFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // ¿Cuántas pestañas tenemos? 2 (Lista y Favoritos)
    override fun getItemCount(): Int = 2

    // ¿Qué fragmento cargo en cada posición?
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ListFragment() // Primera pestaña
            1 -> FavFragment()  // Segunda pestaña
            else -> ListFragment() // Por defecto (seguridad)
        }
    }
}