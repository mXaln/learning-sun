package org.bibletranslationtools.sun.ui.activities.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.bibletranslationtools.sun.adapter.viewpager.OnboardingAdapter
import org.bibletranslationtools.sun.databinding.ActivityAuthenticationBinding
import org.bibletranslationtools.sun.preferences.UserSharePreferences
import org.bibletranslationtools.sun.ui.activities.MainActivity

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userSharePreferences = UserSharePreferences(this)

        // Inflate the layout
        val binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Setup onboarding
        val onboardingAdapter = OnboardingAdapter(this)
        binding.onboardingVp.adapter = onboardingAdapter
        binding.indicator.setViewPager(binding.onboardingVp)


        // Setup view now button
        binding.startNowBtn.setOnClickListener { v ->
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}