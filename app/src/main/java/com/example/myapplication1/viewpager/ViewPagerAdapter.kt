package com.example.myapplication1.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication1.fragments.FavFragment
import com.example.myapplication1.fragments.ListFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // funcion para decirle cuantas pestañas vamos a tener en total
    override fun getItemCount(): Int = 2

    // funcion que decide que pantalla cargar dependiendo de la posicion
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ListFragment() // si estamos en la primera posicion cargamos la lista normal
            1 -> FavFragment()  // si estamos en la segunda cargamos la pantalla de favoritos
            else -> ListFragment() // esto es por seguridad por si falla algo cargar la lista
        }
    }
}