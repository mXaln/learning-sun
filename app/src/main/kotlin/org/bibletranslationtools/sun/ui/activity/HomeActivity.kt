package org.bibletranslationtools.sun.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.bibletranslationtools.sun.databinding.ActivityHomeBinding
import org.bibletranslationtools.sun.ui.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {
    private val viewModel: HomeViewModel by viewModels()
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null

        binding.learnSymbols.setOnClickListener {
            val intent = Intent(baseContext, LessonListActivity::class.java)
            startActivity(intent)
        }

        binding.testSymbols.setOnClickListener {
            val intent = Intent(baseContext, GlobalTestActivity::class.java)
            startActivity(intent)
        }

        binding.trackProgress.setOnClickListener {
            val intent = Intent(baseContext, TrackProgressActivity::class.java)
            startActivity(intent)
        }

        viewModel.importLessons()
    }

}