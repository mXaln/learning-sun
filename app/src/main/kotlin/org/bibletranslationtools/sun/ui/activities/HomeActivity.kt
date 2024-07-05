package org.bibletranslationtools.sun.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.bibletranslationtools.sun.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null

        binding.learnSymbols.setOnClickListener {
            val intent = Intent(baseContext, LessonActivity::class.java)
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
    }

}