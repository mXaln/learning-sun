package org.bibletranslationtools.sun.ui.activities.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.databinding.ActivityLearnBinding
import org.bibletranslationtools.sun.adapter.card.LearnAdapter
import org.bibletranslationtools.sun.ui.viewmodels.LearnViewModel

class SymbolLearnActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLearnBinding.inflate(layoutInflater) }
    private val adapter by lazy { LearnAdapter() }
    private val viewModel: LearnViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null

        viewModel.setLessonId(intent.getStringExtra("id"))

        binding.lessonTitle.text =
            getString(R.string.lesson_name, viewModel.lessonId.value)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupCardsView()
        setupButtons()

        /*binding.startQuizBtn.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("id", viewModel.lessonId.value)
            startActivity(intent)
        }*/
    }

    private fun setupButtons() {
        with(binding) {
            nextButton.setOnClickListener {
                val currentItem = viewPager.currentItem
                if (currentItem < viewModel.cards.value!!.size - 1) {
                    viewPager.currentItem = currentItem + 1
                }
            }

            prevButton.setOnClickListener {
                val currentItem = viewPager.currentItem
                if (currentItem > 0) {
                    viewPager.currentItem = currentItem - 1
                }
            }
        }
    }

    private fun setupCardsView() {
        with(binding) {
            viewPager.adapter = adapter
            viewPager.registerOnPageChangeCallback(callback)

            TabLayoutMediator(tabs, viewPager) { _, _ -> }.attach()

            viewModel.cards.observe(this@SymbolLearnActivity) { cards ->
                adapter.submitList(cards)
            }

            loadCards()
        }
    }

    private fun loadCards() {
        viewModel.loadCards()
    }

    private val callback = object : ViewPager2.OnPageChangeCallback() {
        var prevPosition = 0
        var currentPosition = 0

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            prevPosition = currentPosition
            currentPosition = position
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                viewModel.cards.value?.let { cards ->
                    val card = cards[prevPosition]
                    if (!card.learned) {
                        card.learned = true
                        viewModel.saveCard(card)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(callback)
    }
}

