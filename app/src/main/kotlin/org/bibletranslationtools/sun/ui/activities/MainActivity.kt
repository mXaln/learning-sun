package org.bibletranslationtools.sun.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val navController by lazy {
        findNavController(this, R.id.nav_host_fragment_activity_main)
    }
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view: View = binding.root
        setContentView(view)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navGraphId = R.navigation.main_nav
        val menuId = R.menu.menu_nav

        navController.setGraph(navGraphId)
        binding.bottomNavigationView.menu.clear()
        binding.bottomNavigationView.inflateMenu(menuId)

        setupWithNavController(binding.bottomNavigationView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
