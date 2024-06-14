package org.bibletranslationtools.sun.ui.activities.auth;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.bibletranslationtools.sun.adapter.viewpager.OnboardingAdapter;
import org.bibletranslationtools.sun.databinding.ActivityAuthenticationBinding;
import org.bibletranslationtools.sun.preferen.UserSharePreferences;
import org.bibletranslationtools.sun.ui.activities.MainActivity;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserSharePreferences userSharePreferences = new UserSharePreferences(this);

        // Assuming that userSharePreferences is initialized somewhere else
//        if (userSharePreferences.getLogin()) {
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//        }

        // Inflate the layout
        ActivityAuthenticationBinding binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        // Setup onboarding
        OnboardingAdapter onboardingAdapter = new OnboardingAdapter(this);
        binding.onboardingVp.setAdapter(onboardingAdapter);
        binding.indicator.setViewPager(binding.onboardingVp);

        // Setup view now button
        binding.startNowBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

    }
}