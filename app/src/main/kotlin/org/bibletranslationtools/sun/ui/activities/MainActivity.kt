package org.bibletranslationtools.sun.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.dao.CardDAO
import org.bibletranslationtools.sun.data.dao.LessonDAO
import org.bibletranslationtools.sun.data.dao.SettingsDAO
import org.bibletranslationtools.sun.data.model.Study
import org.bibletranslationtools.sun.databinding.ActivityMainBinding
import org.bibletranslationtools.sun.utils.AssetsProvider

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

        val settingsDao = SettingsDAO(this)
        val lessonDao = LessonDAO(this)
        val cardDao = CardDAO(this)

        val mapper = ObjectMapper().registerKotlinModule()
        val reference = object : TypeReference<Study>() {}
        val json = AssetsProvider.readText(this, "lessons.json")
        json?.let {
            val study = mapper.readValue(it, reference)
            val dbVersion = settingsDao.get("version")?.toInt() ?: 0

            if (study.version > dbVersion) {
                // Insert lessons
                for (lesson in study.lessons) {
                    lessonDao.insertLesson(lesson)
                    for (card in lesson.cards) {
                        card.lessonId = lesson.id
                        cardDao.insertCard(card)
                    }
                }

                if (dbVersion == 0) {
                    settingsDao.insert("version", study.version.toString())
                } else {
                    settingsDao.update("version", study.version.toString())
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
