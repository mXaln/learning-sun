package org.bibletranslationtools.sun.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.bibletranslationtools.sun.databinding.ActivityTrackProgressBinding

class TrackProgressActivity : AppCompatActivity() {
    private val binding by lazy { ActivityTrackProgressBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}