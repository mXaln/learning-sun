package org.bibletranslationtools.sun.ui.activities.learn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import org.bibletranslationtools.sun.R
import androidx.recyclerview.widget.DefaultItemAnimator
import org.bibletranslationtools.sun.adapter.card.CardLearnAdapter
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ActivityLearnBinding
import com.yuyakaido.android.cardstackview.*
import org.bibletranslationtools.sun.ui.viewmodels.LearnViewModel

class LearnActivity : AppCompatActivity(), CardStackListener, CardLearnAdapter.CardFlipListener {
    private val binding by lazy { ActivityLearnBinding.inflate(layoutInflater) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardLearnAdapter(this) }
    private val viewModel: LearnViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        viewModel.setLessonId(intent.getStringExtra("id"))

        binding.lessonTitle.text =
            getString(R.string.lesson_name, viewModel.lessonId.value)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupCardStackView()
        setupButton()

        binding.startQuizBtn.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("id", viewModel.lessonId.value)
            startActivity(intent)
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = $direction, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
        viewModel.filteredCards.value?.let { cards ->
            val card = cards[manager.topPosition - 1]
            card.learned = true
            viewModel.saveCard(card)
            checkLessonFinished(cards)
        }
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
    }

    override fun onCardDisappeared(view: View?, position: Int) {
    }

    override fun onCardRewound() {
    }

    override fun onCardFlipped(card: Card) {
        binding.buttonContainer.visibility = View.VISIBLE
    }

    private fun setupButton() {
        binding.nextBtn.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            binding.cardStackView.swipe()
            binding.buttonContainer.visibility = View.GONE
        }
    }

    private fun setupCardStackView() {
        initialize()
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.Bottom)
        manager.setVisibleCount(1)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setSwipeableMethod(SwipeableMethod.Automatic)
        manager.setOverlayInterpolator(LinearInterpolator())

        viewModel.filteredCards.observe(this) { cards ->
            adapter.submitList(cards)
            checkLessonFinished(cards)
        }

        loadCards()

        binding.cardStackView.layoutManager = manager
        binding.cardStackView.adapter = adapter
        binding.cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    private fun loadCards() {
        viewModel.loadCards()
    }

    private fun checkLessonFinished(cards: List<Card>) {
        if (cards.isEmpty()) {
            val cardStack = binding.cardStackView.visibility == View.VISIBLE
            if (cardStack) {
                binding.cardStackView.visibility = View.GONE
                binding.buttonContainer.visibility = View.GONE
                binding.reviewContainer.visibility = View.VISIBLE
                preview()
            }

            Toast.makeText(this, "No cards to learn", Toast.LENGTH_SHORT).show()
        }
    }

    private fun preview() {
        val learned = getCardStatus(true)
        val notLearned = getCardStatus(false)

        binding.knowNumberTv.text = learned.toString()
        val sum = (learned.toFloat() / (notLearned.toFloat() + learned.toFloat())) * 100
        binding.reviewProgress.setSpinningBarLength(sum)
        binding.reviewProgress.isEnabled = false
        binding.reviewProgress.isFocusableInTouchMode = false
        binding.reviewProgress.setValueAnimated(sum, 1000)
    }

    private fun getCardStatus(learned: Boolean): Int {
        return viewModel.cards.value
            ?.filter { it.learned == learned }
            ?.size ?: 0
    }
}

