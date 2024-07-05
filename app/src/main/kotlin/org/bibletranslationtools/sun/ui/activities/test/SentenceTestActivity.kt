package org.bibletranslationtools.sun.ui.activities.test

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.adapter.symbol.TestSymbolAdapter
import org.bibletranslationtools.sun.data.model.SentenceWithSymbols
import org.bibletranslationtools.sun.data.model.Symbol
import org.bibletranslationtools.sun.databinding.ActivityTestBinding
import org.bibletranslationtools.sun.ui.activities.home.HomeActivity
import org.bibletranslationtools.sun.ui.activities.review.SymbolReviewActivity
import org.bibletranslationtools.sun.ui.viewmodels.TestViewModel

class SentenceTestActivity : AppCompatActivity(), TestSymbolAdapter.OnSymbolSelectedListener {
    private val binding by lazy { ActivityTestBinding.inflate(layoutInflater) }
    private val viewModel: TestViewModel by viewModels()
    private val variantsAdapter: TestSymbolAdapter by lazy {
        TestSymbolAdapter(listener = this)
    }
    private val answersAdapter: TestSymbolAdapter by lazy {
        TestSymbolAdapter()
    }

    private lateinit var currentSentence: SentenceWithSymbols
    private var progress = 0
    private lateinit var id: String
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val totalVariants = 4
    private val askedSentences = arrayListOf<SentenceWithSymbols>()
    private var lastAnswerPosition = -1

    private val variantSymbols = arrayListOf<Symbol>()
    private val answerSymbols = arrayListOf<Symbol>()

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

            answersList.layoutManager = LinearLayoutManager(
                this@SentenceTestActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            answersList.adapter = answersAdapter

            variantsList.layoutManager = LinearLayoutManager(
                this@SentenceTestActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            variantsList.adapter = variantsAdapter

            setNextSentence()

            ioScope.launch {
                val max = viewModel.getPassedSentences(id, false).size
                timelineProgress.max = max
            }

            nextButton.setOnClickListener {
                if (viewModel.sentenceDone.value == true) {
                    setNextSentence()
                    viewModel.sentenceDone.value = false
                }
            }
        }
    }

    override fun onSymbolSelected(symbol: Symbol, position: Int) {
        if (viewModel.sentenceDone.value == false) {
            symbol.correct = true
            variantsAdapter.selectCorrect(position)

            lastAnswerPosition++
            answerSymbols[lastAnswerPosition] = symbol.copy()
            answersAdapter.submitList(answerSymbols)
            answersAdapter.notifyItemChanged(lastAnswerPosition)

            if (lastAnswerPosition >= answersAdapter.itemCount - 1) {
                checkAnswer()
                viewModel.sentenceDone.postValue(true)
                binding.nextButton.isEnabled = true
                lastAnswerPosition = -1
            }
        }
    }

    private fun checkAnswer() {
        val correctSymbols = currentSentence.symbols

        val isSentenceCorrect = correctSymbols.map { it.id } == answerSymbols.map { it.id }

        if (isSentenceCorrect) {
            ioScope.launch(Dispatchers.IO) {
                currentSentence.sentence.passed = true
                viewModel.updateSentence(currentSentence.sentence)
            }
            progress++
            setUpProgressBar()
        }

        answerSymbols.zip(correctSymbols).withIndex().forEach { (index, pair) ->
            if (pair.first.name == pair.second.name) {
                pair.first.correct = true
                answersAdapter.selectCorrect(index)
            } else {
                pair.first.correct = false
                answersAdapter.selectIncorrect(index)
            }
        }

        correctSymbols.forEach {
            it.selected = true
            it.correct = true
        }
        variantsAdapter.submitList(correctSymbols)
        variantsAdapter.refresh()
    }

    private fun setUpProgressBar() {
        binding.timelineProgress.progress = progress
    }

    private fun setNextSentence() {
        binding.nextButton.isEnabled = false

        scope.launch {
            val allSentences = viewModel.getAllSentences(id) as MutableList
            val notPassedSentences = allSentences.filter { !it.sentence.passed }

            if (notPassedSentences.isEmpty()) {
                finishTest()
                return@launch
            }

            setRandomSentence(notPassedSentences)

            Glide.with(baseContext)
                .load(Uri.parse("file:///android_asset/images/test/${currentSentence.sentence.correct}"))
                .fitCenter()
                .into(binding.itemImage)

            val incorrectSymbols = allSentences
                .map { it.symbols }
                .flatten()
                .filter { symbol ->
                    val correctSymbols = currentSentence.symbols
                    correctSymbols.none { it.name == symbol.name }
                }
                .shuffled()
                .take(totalVariants - currentSentence.symbols.size)

            val variants = (currentSentence.symbols + incorrectSymbols).shuffled()
            setVariants(variants)

            val answers = currentSentence.symbols.map { it.copy(name = "") }
            setAnswers(answers)

            askedSentences.add(currentSentence)
        }
    }

    private fun setRandomSentence(sentences: List<SentenceWithSymbols>) {
        // Try to select a sentence that has not been asked before
        if (this::currentSentence.isInitialized && sentences.size > 1) {
            val oldSentence = currentSentence.copy()
            while (oldSentence == currentSentence) {
                currentSentence = sentences.random()
            }
        } else {
            currentSentence = sentences.random()
        }
    }

    private fun finishTest() {
        val intent = Intent(baseContext, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        viewModel.sentenceDone.postValue(false)
    }

    private fun setVariants(symbols: List<Symbol>) {
        variantSymbols.clear()
        variantSymbols.addAll(symbols)
        variantsAdapter.submitList(symbols)
        variantsAdapter.refresh()
    }

    private fun setAnswers(symbols: List<Symbol>) {
        answerSymbols.clear()
        answerSymbols.addAll(symbols)
        answersAdapter.submitList(symbols)
        answersAdapter.refresh()
    }
}