package org.bibletranslationtools.sun.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.model.Setting
import org.bibletranslationtools.sun.data.model.Study
import org.bibletranslationtools.sun.databinding.ActivityMainBinding
import org.bibletranslationtools.sun.ui.viewmodels.MainViewModel
import org.bibletranslationtools.sun.utils.AssetsProvider

class MainActivity : AppCompatActivity() {
    private val navController by lazy {
        findNavController(this, R.id.nav_host_fragment_activity_main)
    }
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels()

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view: View = binding.root
        setContentView(view)
        setupNavigation()

        val mapper = ObjectMapper().registerKotlinModule()
        val reference = object : TypeReference<Study>() {}
        val json = AssetsProvider.readText(this, "lessons.json")

        ioScope.launch {
            val dbVersion = viewModel.getDatabaseVersion() ?: 0

            json?.let {
                val study = mapper.readValue(it, reference)

                if (study.version > dbVersion) {
                    // Insert lessons
                    for (lesson in study.lessons) {
                        viewModel.insertLesson(lesson)
                        for (card in lesson.cards) {
                            card.lessonId = lesson.id
                            viewModel.insertCard(card)
                        }
                    }

                    viewModel.insertSetting(Setting("version", study.version.toString()))
                }
            }
        }
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
