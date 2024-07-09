package org.bibletranslationtools.sun.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.ui.adapter.TestSymbolAdapter
import org.bibletranslationtools.sun.data.model.SentenceWithSymbols
import org.bibletranslationtools.sun.data.model.Symbol
import org.bibletranslationtools.sun.databinding.ActivitySentencesBinding
import org.bibletranslationtools.sun.ui.viewmodel.SentenceTestViewModel
import org.bibletranslationtools.sun.utils.TallyMarkConverter

class BuildSentencesActivity : AppCompatActivity(), TestSymbolAdapter.OnSymbolSelectedListener {
    private val binding by lazy { ActivitySentencesBinding.inflate(layoutInflater) }
    private val viewModel: SentenceTestViewModel by viewModels()
    private val variantsAdapter: TestSymbolAdapter by lazy {
        TestSymbolAdapter(listener = this)
    }
    private val answersAdapter: TestSymbolAdapter by lazy {
        TestSymbolAdapter()
    }

    private lateinit var currentSentence: SentenceWithSymbols
    private val totalVariants = 4
    private var lastAnswerPosition = -1

    private val variantSymbols = arrayListOf<Symbol>()
    private val answerSymbols = arrayListOf<Symbol>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            viewModel.lessonId.value = intent.getIntExtra("id", 1)
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

            answersList.layoutManager = LinearLayoutManager(
                this@BuildSentencesActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            answersList.adapter = answersAdapter

            variantsList.layoutManager = LinearLayoutManager(
                this@BuildSentencesActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            variantsList.adapter = variantsAdapter

            lifecycleScope.launch {
                viewModel.sentences.collect {
                    if (it.isNotEmpty()) {
                        setNextSentence()
                    }
                }
            }

            nextButton.setOnClickListener {
                if (viewModel.sentenceDone.value) {
                    setNextSentence()
                    viewModel.sentenceDone.value = false
                }
            }

            if (viewModel.isGlobal.value) {
                viewModel.loadAllPassedSentences()
            } else {
                viewModel.loadSentences()
            }
        }
    }

    override fun onSymbolSelected(symbol: Symbol, position: Int) {
        if (!viewModel.sentenceDone.value) {
            symbol.correct = true
            variantsAdapter.selectCorrect(position)

            lastAnswerPosition++
            answerSymbols[lastAnswerPosition] = symbol.copy()
            answersAdapter.submitList(answerSymbols)
            answersAdapter.notifyItemChanged(lastAnswerPosition)

            if (lastAnswerPosition >= answersAdapter.itemCount - 1) {
                checkAnswer()
                viewModel.sentenceDone.value = true
                binding.nextButton.isEnabled = true
                lastAnswerPosition = -1
            }
        }
    }

    private fun checkAnswer() {
        val correctSymbols = currentSentence.symbols

        val isSentenceCorrect = correctSymbols.map { it.id } == answerSymbols.map { it.id }

        if (isSentenceCorrect) {
            lifecycleScope.launch(Dispatchers.IO) {
                currentSentence.sentence.passed = true
                currentSentence.sentence.answered = true
                viewModel.updateSentence(currentSentence.sentence)
            }
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

    private fun setNextSentence() {
        binding.nextButton.isEnabled = false

        val allSentences = viewModel.sentences.value.toMutableList()
        val inProgressSentences = allSentences.filter { !it.sentence.answered }

        if (inProgressSentences.isEmpty()) {
            finishTest()
            return
        }

        setRandomSentence(inProgressSentences)

        Glide.with(baseContext)
            .load(Uri.parse("file:///android_asset/images/sentences/${currentSentence.sentence.correct}"))
            .fitCenter()
            .into(binding.itemImage)

        val incorrectSymbols = allSentences
            .map { it.symbols }
            .flatten()
            .filter { symbol ->
                val correctSymbols = currentSentence.symbols
                correctSymbols.none { it.name == symbol.name }
            }
            .distinctBy { it.name }
            .shuffled()
            .take(totalVariants - currentSentence.symbols.size)

        val variants = (currentSentence.symbols + incorrectSymbols).shuffled()
        setVariants(variants)

        val answers = currentSentence.symbols.map { it.copy(name = "") }
        setAnswers(answers)
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
        if (viewModel.isGlobal.value) {
            val intent = Intent(baseContext, GlobalTestActivity::class.java)
            startActivity(intent)
        } else {
            lifecycleScope.launch {
                val lessons = viewModel.getAllLessons().map { it.id }
                val current = lessons.indexOf(viewModel.lessonId.value)
                var next = 1
                if (current < lessons.size - 1) {
                    next = lessons[current + 1]
                }

                runOnUiThread {
                    val intent = Intent(baseContext, LessonListActivity::class.java)
                    intent.putExtra("next", next)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.sentenceDone.value = false
    }

    private fun setVariants(symbols: List<Symbol>) {
        symbols.forEach {
            it.selected = false
            it.correct = null
        }

        variantSymbols.clear()
        variantSymbols.addAll(symbols)
        variantsAdapter.submitList(symbols)
        variantsAdapter.refresh()
    }

    private fun setAnswers(symbols: List<Symbol>) {
        symbols.forEach {
            it.selected = false
            it.correct = null
        }

        answerSymbols.clear()
        answerSymbols.addAll(symbols)
        answersAdapter.submitList(symbols)
        answersAdapter.refresh()
    }
}