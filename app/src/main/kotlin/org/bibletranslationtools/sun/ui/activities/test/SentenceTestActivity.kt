package org.bibletranslationtools.sun.ui.activities.test

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import org.bibletranslationtools.sun.adapter.test.TestSymbolAdapter
import org.bibletranslationtools.sun.data.model.SentenceWithSymbols
import org.bibletranslationtools.sun.data.model.Symbol
import org.bibletranslationtools.sun.databinding.ActivityTestBinding
import org.bibletranslationtools.sun.ui.viewmodels.TestViewModel

class SentenceTestActivity : AppCompatActivity(), TestSymbolAdapter.OnSymbolSelectedListener {
    private val binding by lazy { ActivityTestBinding.inflate(layoutInflater) }
    private val viewModel: TestViewModel by viewModels()
    private val variantsAdapter: TestSymbolAdapter by lazy {
        TestSymbolAdapter(this)
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
            val max = viewModel.getPassedSentences(id, false).size
            binding.timelineProgress.max = max
        }

        binding.nextButton.setOnClickListener {
            if (viewModel.questionDone.value == true) {
                setNextQuestion()
                viewModel.questionDone.value = false
            }
        }
    }

    override fun onSymbolSelected(symbol: Symbol) {
        println(symbol)
    }

    private fun checkAnswer(selectedAnswer: String, position: Int) {
        /*if (selectedAnswer == correctCard.symbol) {
            ioScope.launch(Dispatchers.IO) {
                correctCard.passed = true
                viewModel.updateCard(correctCard)
            }
            gridAdapter.selectCorrectCard(position)
            progress++
            setUpProgressBar()
        } else {
            gridAdapter.selectIncorrectCard(position)
        }*/
    }

    private fun setUpProgressBar() {
        binding.timelineProgress.progress = progress
        Log.d("progress", progress.toString())
    }

    private fun setNextQuestion() {
        answersAdapter.resetSelection()
        variantsAdapter.resetSelection()
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

            val testSymbols = (currentSentence.symbols + incorrectSymbols).shuffled()
            setVariants(testSymbols)

            val answersSymbols = currentSentence.symbols.map { it.copy(name = "") }
            setAnswers(answersSymbols)

            askedSentences.add(currentSentence)
        }
    }

    private fun setRandomSentence(sentences: List<SentenceWithSymbols>) {
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

    private fun setVariants(symbols: List<Symbol>) {
        binding.variantsRv.layoutManager = LinearLayoutManager(
            this@SentenceTestActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.variantsRv.adapter = variantsAdapter
        variantsAdapter.submitList(symbols)

        /*binding.variantsRv.setOnItemClickListener { _, _, position, _ ->
            if (viewModel.questionDone.value == false) {
                checkAnswer(testSymbols[position].symbol, position)
                viewModel.questionDone.postValue(true)
                binding.nextButton.isEnabled = true
            }
        }*/

        binding.variantsRv.setOnClickListener {  }
    }

    private fun setAnswers(symbols: List<Symbol>) {
        binding.answersRv.layoutManager = LinearLayoutManager(
            this@SentenceTestActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.answersRv.adapter = answersAdapter
        answersAdapter.submitList(symbols)
    }
}