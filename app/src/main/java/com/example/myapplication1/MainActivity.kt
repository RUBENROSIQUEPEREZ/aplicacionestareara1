package com.example.myapplication1

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inflamos la vista usando Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Configurar la Toolbar (barra superior) personalizada
        setSupportActionBar(binding.toolbar)

        // 3. Obtener el NavController (el conductor de la navegación)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // 4. Configurar la "Top Level Destinations" (Pantallas principales)
        // En estas pantallas se mostrará el icono de Hamburguesa (Menú Lateral).
        // En las demás (ej: Login), se mostrará la flecha de "Atrás".
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.tabFragment,       // Lista
                R.id.contactFragment,   // Contacto
                R.id.preferencesFragment // Preferencias
            ),
            binding.drawerLayout // Vinculamos el menú lateral
        )

        // 5. Conectar la Toolbar con la navegación
        setupActionBarWithNavController(navController, appBarConfiguration)

        // 6. Conectar los Menús (Inferior y Lateral) con la navegación
        binding.bottomNav.setupWithNavController(navController)
        binding.navigationView.setupWithNavController(navController)

        // 7. Lógica visual: ¿Cuándo mostrar/ocultar barras y botones?
        navController.addOnDestinationChangedListener { _, destination, _ ->

            // A) Control del Botón Flotante (FAB)
            // Solo debe verse en la pantalla de Lista (TabFragment)
            if (destination.id == R.id.tabFragment) {
                binding.fab.show()
            } else {
                binding.fab.hide()
            }

            // B) Control de la visibilidad de los menús (Opcional pero recomendado)
            // Si estamos en el Login o Registro, NO queremos ver menús
            if (destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
                binding.bottomNav.visibility = View.GONE
                binding.toolbar.visibility = View.GONE
                // Bloqueamos el menú lateral en el login
                binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                binding.bottomNav.visibility = View.VISIBLE
                binding.toolbar.visibility = View.VISIBLE
                // Desbloqueamos el menú lateral en la app
                binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }

        // 8. Manejo manual del "Cerrar Sesión" del menú lateral
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.menu_logout) {
                // Aquí iría tu lógica de cerrar sesión
                // Por ahora, navegamos al login y limpiamos la pila
                navController.navigate(R.id.loginFragment)
                // Cerramos el menú lateral visualmente
                binding.drawerLayout.close()
                true
            } else {
                // Si no es cerrar sesión, dejamos que la navegación automática lo maneje
                // (Para esto llamamos al método original de la librería)
                androidx.navigation.ui.NavigationUI.onNavDestinationSelected(menuItem, navController)
                // Cerramos el drawer
                binding.drawerLayout.close()
                true
            }
        }
    }

    // 9. Hacer que el botón "Atrás" o la "Hamburguesa" funcionen en la Toolbar
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}