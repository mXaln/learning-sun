package org.bibletranslationtools.sun.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ActivityReviewBinding
import kotlinx.coroutines.*
import org.bibletranslationtools.sun.ui.adapter.ReviewCardAdapter
import org.bibletranslationtools.sun.ui.adapter.ItemOffsetDecoration
import org.bibletranslationtools.sun.ui.viewmodel.ReviewViewModel
import org.bibletranslationtools.sun.utils.TallyMarkConverter

class SymbolReviewActivity : AppCompatActivity(), ReviewCardAdapter.OnCardSelectedListener {

    private val binding by lazy { ActivityReviewBinding.inflate(layoutInflater) }
    private val viewModel: ReviewViewModel by viewModels()
    private val gridAdapter: ReviewCardAdapter by lazy {
        ReviewCardAdapter(this)
    }

    private lateinit var currentCard: Card
    private val reviewCards = arrayListOf<Card>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            viewModel.lessonId.value = intent.getIntExtra("id", 1)
            viewModel.part.value = intent.getIntExtra("part", 1)
            viewModel.isGlobal.value = intent.getBooleanExtra("global", false)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {
                val intent = if (viewModel.isGlobal.value) {
                    Intent(baseContext, GlobalTestActivity::class.java)
                } else {
                    Intent(baseContext, LessonListActivity::class.java)
                }
                startActivity(intent)
            }

            if (!viewModel.isGlobal.value) {
                lessonNameContainer.visibility = View.VISIBLE
                lessonTitle.text = getString(R.string.lesson_name, viewModel.lessonId.value)
                lessonTally.text = TallyMarkConverter.toText(viewModel.lessonId.value)
            } else {
                lessonNameContainer.visibility = View.GONE
            }

            answersList.layoutManager = GridLayoutManager(
                this@SymbolReviewActivity,
                2
            )
            answersList.addItemDecoration(
                ItemOffsetDecoration(
                    2,
                    30,
                    false
                )
            )
            answersList.adapter = gridAdapter

            lifecycleScope.launch {
                viewModel.cards.collect {
                    if (it.isNotEmpty()) {
                        setNextQuestion()
                    }
                }
            }

            nextButton.setOnClickListener {
                if (viewModel.questionDone.value) {
                    setNextQuestion()
                    viewModel.questionDone.value = false
                }
            }

            if (viewModel.isGlobal.value) {
                viewModel.loadAllPassedCards()
            } else {
                viewModel.loadLessonCards()
            }

        }
    }

    override fun onCardSelected(card: Card, position: Int) {
        if (!viewModel.questionDone.value) {
            checkAnswer(reviewCards[position], position)
            viewModel.questionDone.value = true
            binding.nextButton.isEnabled = true
        }
    }

    private fun checkAnswer(selectedCard: Card, position: Int) {
        if (selectedCard.symbol == currentCard.symbol) {
            lifecycleScope.launch(Dispatchers.IO) {
                when (viewModel.part.value) {
                    PART_ONE, PART_TWO -> {
                        currentCard.partiallyDone = true
                    }
                    else -> {
                        currentCard.passed = true
                        currentCard.done = true
                        viewModel.updateCard(currentCard)
                    }
                }
            }
            currentCard.correct = true
            gridAdapter.selectCorrect(position)
        } else {
            currentCard.correct = true
            selectedCard.correct = false
            gridAdapter.selectIncorrect(position)
            gridAdapter.selectCorrect(currentCard)
        }
    }

    private fun setNextQuestion() {
        binding.nextButton.isEnabled = false

        val allCards = viewModel.cards.value.toMutableList()
        allCards.forEach { it.correct = null }

        val inProgressCards = allCards.filter {
            when (viewModel.part.value) {
                PART_ONE, PART_TWO -> !it.partiallyDone
                else -> !it.done
            }
        }

        if (inProgressCards.isEmpty()) {
            finishReview()
            return
        }

        setRandomCard(inProgressCards)
        allCards.remove(currentCard)

        val incorrectCards = allCards.shuffled().take(3)

        setAnswers((listOf(currentCard) + incorrectCards).shuffled())

        gridAdapter.submitList(reviewCards)

        Glide.with(baseContext)
            .load(Uri.parse("file:///android_asset/images/symbols/${currentCard.secondary}"))
            .fitCenter()
            .into(binding.itemImage)
    }

    private fun setRandomCard(cards: List<Card>) {
        if (this::currentCard.isInitialized && cards.size > 1) {
            val oldCard = currentCard.copy()
            while (oldCard == currentCard) {
                currentCard = cards.random()
            }
        } else {
            currentCard = cards.random()
        }
    }

    private fun finishReview() {
        if (viewModel.isGlobal.value) {
            val intent = Intent(baseContext, GlobalTestActivity::class.java)
            startActivity(intent)
        } else {
            val type: Int
            when (viewModel.part.value) {
                PART_ONE -> {
                    viewModel.part.value = PART_TWO
                    type = LEARN_SYMBOLS
                }
                PART_TWO -> {
                    viewModel.part.value = PART_ALL
                    type = TEST_SYMBOLS
                }
                else -> {
                    viewModel.part.value = PART_FINAL
                    type = BUILD_SENTENCES
                }
            }

            val intent = Intent(baseContext, IntermediateActivity::class.java)
            intent.putExtra("id", viewModel.lessonId.value)
            intent.putExtra("part", viewModel.part.value)
            intent.putExtra("type", type)
            startActivity(intent)
        }
    }

    private fun setAnswers(cards: List<Card>) {
        reviewCards.clear()
        reviewCards.addAll(cards)
        gridAdapter.refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.questionDone.value = false
    }
}