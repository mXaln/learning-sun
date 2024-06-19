package org.bibletranslationtools.sun.ui.activities.learn

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.dao.CardDAO
import org.bibletranslationtools.sun.data.dao.LessonDAO
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ActivityQuizBinding
import org.bibletranslationtools.sun.databinding.DialogCorrectBinding
import org.bibletranslationtools.sun.databinding.DialogWrongBinding
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import kotlinx.coroutines.*

class QuizActivity : AppCompatActivity() {

    private val binding by lazy { ActivityQuizBinding.inflate(layoutInflater) }
    private val cardDAO by lazy { CardDAO(this) }
    private val flashCardDAO by lazy { LessonDAO(this) }

    private val dialogCorrect by lazy { AlertDialog.Builder(this) }
    private val dialogWrong by lazy { AlertDialog.Builder(this) }

    private var progress = 0
    private lateinit var correctAnswer: String
    private val askedCards = arrayListOf<Card>()
    private lateinit var id: String
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        id = intent.getStringExtra("id") ?: ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setNextQuestion()
        val max = cardDAO.getIsLearnedCards(id, false).size
        binding.timelineProgress.max = max
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun checkAnswer(selectedAnswer: String, cardId: String): Boolean {
        return if (selectedAnswer == correctAnswer) {
            correctDialog(correctAnswer)
            GlobalScope.launch(Dispatchers.IO) {
                cardDAO.updateCardIsLearned(cardId, 1)
            }
            setNextQuestion()
            progress++
            setUpProgressBar()
            true
        } else {
            wrongDialog(correctAnswer, selectedAnswer)
            setNextQuestion()
            false
        }
    }

    private fun setUpProgressBar() {
        binding.timelineProgress.progress = progress
        Log.d("progress", progress.toString())
    }

    private fun setNextQuestion() {
        scope.launch {
            val cards = cardDAO.getIsLearnedCards(id, false) // get a list of cards that are not learned
            val randomCard = cardDAO.getLessonCards(id) // get all cards

            if (cards.isEmpty()) {
                finishQuiz()
                return@launch
            }

            val correctCard = cards.random() // get a random card from a list of cards that are not learned
            randomCard.remove(correctCard) // remove the correct card from a list of all cards

            val incorrectCards = randomCard.shuffled().take(3) // get 3 random cards from list of all cards

            val allCards = (listOf(correctCard) + incorrectCards).shuffled() // shuffle 4 cards
            correctAnswer = correctCard.symbol

            withContext(Dispatchers.Main) {
                binding.optionOne.text = allCards[0].symbol
                binding.optionTwo.text = allCards[1].symbol
                binding.optionThree.text = allCards[2].symbol
                binding.optionFour.text = allCards[3].symbol

                Glide.with(baseContext)
                    .load(Uri.parse("file:///android_asset/images/${correctCard.id}.jpg"))
                    .into(binding.itemImage)

                binding.optionOne.setOnClickListener {
                    checkAnswer(binding.optionOne.text.toString(), correctCard.id)
                }

                binding.optionTwo.setOnClickListener {
                    checkAnswer(binding.optionTwo.text.toString(), correctCard.id)
                }

                binding.optionThree.setOnClickListener {
                    checkAnswer(binding.optionThree.text.toString(), correctCard.id)
                }

                binding.optionFour.setOnClickListener {
                    checkAnswer(binding.optionFour.text.toString(), correctCard.id)
                }

                askedCards.add(correctCard)
            }
        }
    }

    private fun finishQuiz() { //1 quiz, 2 learn
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

    private fun correctDialog(answer: String) {
        val dialogBinding = DialogCorrectBinding.inflate(layoutInflater)
        dialogCorrect.setView(dialogBinding.root)
        dialogCorrect.setCancelable(true)
        val builder = dialogCorrect.create()
        dialogBinding.questionTv.text = answer
        dialogCorrect.setOnDismissListener {
            // startAnimations()
        }

        builder.show()
    }

    private fun wrongDialog(answer: String, userAnswer: String) {
        val dialogBinding = DialogWrongBinding.inflate(layoutInflater)
        dialogWrong.setView(dialogBinding.root)
        dialogWrong.setCancelable(true)
        val builder = dialogWrong.create()
        dialogBinding.explanationTv.text = answer
        dialogBinding.yourExplanationTv.text = userAnswer
        dialogBinding.continueTv.setOnClickListener {
            builder.dismiss()
        }
        builder.setOnDismissListener {
            //startAnimations()
        }
        builder.show()
    }

    private fun startAnimations() {
        val views =
            listOf(
                binding.optionOne,
                binding.optionTwo,
                binding.optionThree,
                binding.optionFour
            )
        val duration = 1000L
        val endValue = -binding.optionOne.width.toFloat()

        views.forEach { view ->
            val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, endValue)
            animator.duration = duration
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.translationX = 0f
                    if (view == binding.optionFour) {
                        setNextQuestion()
                    }
                }
            })
            animator.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        dialogCorrect.create().dismiss()
        dialogWrong.create().dismiss()
    }
}