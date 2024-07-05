package org.bibletranslationtools.sun.ui.activities.review

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ActivityReviewBinding
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import kotlinx.coroutines.*
import org.bibletranslationtools.sun.adapter.card.ReviewCardAdapter
import org.bibletranslationtools.sun.adapter.card.ItemOffsetDecoration
import org.bibletranslationtools.sun.ui.activities.learn.BUILD_SENTENCES
import org.bibletranslationtools.sun.ui.activities.learn.IntermediateActivity
import org.bibletranslationtools.sun.ui.activities.learn.TEST_SYMBOLS
import org.bibletranslationtools.sun.ui.viewmodels.ReviewViewModel

class SymbolReviewActivity : AppCompatActivity(), ReviewCardAdapter.OnCardSelectedListener {

    private val binding by lazy { ActivityReviewBinding.inflate(layoutInflater) }
    private val viewModel: ReviewViewModel by viewModels()
    private val gridAdapter: ReviewCardAdapter by lazy {
        ReviewCardAdapter(this)
    }

    private var progress = 0
    private lateinit var currentCard: Card
    private val askedCards = arrayListOf<Card>()
    private lateinit var id: String
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val reviewCards = arrayListOf<Card>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            id = intent.getStringExtra("id") ?: ""

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            lessonTitle.text = getString(R.string.lesson_name, id)

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

            setNextQuestion()

            ioScope.launch {
                val max = viewModel.getPassedCards(id, false).size
                timelineProgress.max = max
            }

            nextButton.setOnClickListener {
                if (viewModel.questionDone.value == true) {
                    setNextQuestion()
                    viewModel.questionDone.value = false
                }
            }
        }
    }

    override fun onCardSelected(card: Card, position: Int) {
        if (viewModel.questionDone.value == false) {
            checkAnswer(reviewCards[position], position)
            viewModel.questionDone.postValue(true)
            binding.nextButton.isEnabled = true
        }
    }

    private fun checkAnswer(selectedCard: Card, position: Int) {
        if (selectedCard.symbol == currentCard.symbol) {
            ioScope.launch(Dispatchers.IO) {
                currentCard.passed = true
                viewModel.updateCard(currentCard)
            }
            currentCard.correct = true
            gridAdapter.selectCorrect(position)
            progress++
            setUpProgressBar()
        } else {
            currentCard.correct = true
            selectedCard.correct = false
            gridAdapter.selectIncorrect(position)
            gridAdapter.selectCorrect(currentCard)
        }
    }

    private fun setUpProgressBar() {
        binding.timelineProgress.progress = progress
    }

    private fun setNextQuestion() {
        binding.nextButton.isEnabled = false

        scope.launch {
            val allCards = viewModel.getAllCards(id) as MutableList
            val notPassedCards = allCards.filter { !it.passed }

            if (notPassedCards.isEmpty()) {
                finishReview()
                return@launch
            }

            setRandomCard(notPassedCards)
            allCards.remove(currentCard)

            val incorrectCards = allCards.shuffled().take(3)

            setAnswers((listOf(currentCard) + incorrectCards).shuffled())

            withContext(Dispatchers.Main) {
                gridAdapter.submitList(reviewCards)

                Glide.with(baseContext)
                    .load(Uri.parse("file:///android_asset/images/learn/${currentCard.secondary}"))
                    .fitCenter()
                    .into(binding.itemImage)

                askedCards.add(currentCard)
            }
        }
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
        val intent = Intent(baseContext, IntermediateActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("type", BUILD_SENTENCES)
        startActivity(intent)
    }

    private fun setAnswers(cards: List<Card>) {
        reviewCards.clear()
        reviewCards.addAll(cards)
        gridAdapter.refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        viewModel.questionDone.postValue(false)
    }
}