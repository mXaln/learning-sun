package org.bibletranslationtools.sun.ui.activities.learn

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ActivityReviewBinding
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import kotlinx.coroutines.*
import org.bibletranslationtools.sun.adapter.grid.GridCardAdapter
import org.bibletranslationtools.sun.ui.viewmodels.ReviewViewModel

class SymbolReviewActivity : AppCompatActivity() {

    private val binding by lazy { ActivityReviewBinding.inflate(layoutInflater) }
    private val viewModel: ReviewViewModel by viewModels()
    private val gridAdapter: GridCardAdapter by lazy {
        GridCardAdapter(this, mutableListOf())
    }

    private var progress = 0
    private lateinit var currentCard: Card
    private val askedCards = arrayListOf<Card>()
    private lateinit var id: String
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        id = intent.getStringExtra("id") ?: ""

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setNextQuestion()

        ioScope.launch {
            val max = viewModel.getPassedCards(id, false).size
            binding.timelineProgress.max = max
        }

        binding.nextButton.setOnClickListener {
            if (viewModel.questionDone.value == true) {
                setNextQuestion()
                viewModel.questionDone.value = false
            }
        }
    }

    private fun checkAnswer(selectedAnswer: String, position: Int) {
        if (selectedAnswer == currentCard.symbol) {
            ioScope.launch(Dispatchers.IO) {
                currentCard.passed = true
                viewModel.updateCard(currentCard)
            }
            gridAdapter.selectCorrectCard(position)
            progress++
            setUpProgressBar()
        } else {
            gridAdapter.selectIncorrectCard(position)
        }
    }

    private fun setUpProgressBar() {
        binding.timelineProgress.progress = progress
        Log.d("progress", progress.toString())
    }

    private fun setNextQuestion() {
        gridAdapter.resetSelection()
        binding.nextButton.isEnabled = false

        scope.launch {
            // get all cards
            val allCards = viewModel.getAllCards(id) as MutableList
            // get a list of cards that are not passed
            val notPassedCards = allCards.filter { !it.passed }

            if (notPassedCards.isEmpty()) {
                finishReview()
                return@launch
            }

            // get a random card from a list of cards that are not passed
            setRandomCard(notPassedCards)

            // remove the current card from a list of all cards
            allCards.remove(currentCard)

            // get 3 random cards from list of all cards
            val incorrectCards = allCards.shuffled().take(3)

            // shuffle 4 cards
            val reviewCards = (listOf(currentCard) + incorrectCards).shuffled()

            withContext(Dispatchers.Main) {
                binding.answersGv.adapter = gridAdapter
                gridAdapter.clear()
                gridAdapter.addAll(reviewCards)

                Glide.with(baseContext)
                    .load(Uri.parse("file:///android_asset/images/learn/${currentCard.id}.jpg"))
                    .fitCenter()
                    .into(binding.itemImage)

                binding.answersGv.setOnItemClickListener { _, _, position, _ ->
                    if (viewModel.questionDone.value == false) {
                        checkAnswer(reviewCards[position].symbol, position)
                        viewModel.questionDone.postValue(true)
                        binding.nextButton.isEnabled = true
                    }
                }

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
        runOnUiThread {
            PopupDialog.getInstance(this)
                .setStyle(Styles.SUCCESS)
                .setHeading(getString(R.string.finish))
                .setDescription(getString(R.string.finish_quiz))
                .setDismissButtonText(getString(R.string.ok))
                .setNegativeButtonText(getString(R.string.cancel))
                .setPositiveButtonText(getString(R.string.ok))
                .setCancelable(true)
                .showDialog(object : OnDialogButtonClickListener() {
                    override fun onDismissClicked(dialog: Dialog?) {
                        super.onDismissClicked(dialog)
                        dialog?.dismiss()
                        finish()
                    }
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        viewModel.questionDone.postValue(false)
    }
}