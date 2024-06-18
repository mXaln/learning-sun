package org.bibletranslationtools.sun.adapter.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.databinding.ItemOnboardingBinding

class OnboardingAdapter(private val context: Context) : PagerAdapter() {
    private val onBoardingTitles = intArrayOf(
        R.string.onboarding_title_1,
        R.string.onboarding_title_2,
        R.string.onboarding_title_3,
        R.string.onboarding_title_4
    )

    private val onBoardingImages = intArrayOf(
        R.drawable.onboarding_1,
        R.drawable.ic_start_svg,
        R.drawable.onboarding_3,
        R.drawable.onboarding_2
    )

    override fun getCount(): Int {
        return onBoardingTitles.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding: ItemOnboardingBinding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(context), container, false
        )

        binding.onboardingTitleTv.setText(onBoardingTitles[position])
        binding.onboardingIv.setImageResource(onBoardingImages[position])

        container.addView(binding.root)

        return binding.getRoot()
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}