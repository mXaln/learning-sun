package org.bibletranslationtools.sun.ui.activities.learn

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.dao.CardDAO
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ActivityTrueFalseFlashCardsBinding
import org.bibletranslationtools.sun.databinding.DialogCorrectBinding
import org.bibletranslationtools.sun.databinding.DialogWrongBinding
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener

class TrueFalseFlashCardsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityTrueFalseFlashCardsBinding.inflate(layoutInflater) }
    private val cardDAO by lazy { CardDAO(this) }
    private val cardList = arrayListOf<Card>()
    private var progress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setUpQuestion()
        setUpProgressBar()
    }

    private fun setUpProgressBar(): Int {
        val id = intent.getStringExtra("id")
        cardList.clear()
        cardList.addAll(cardDAO.getIsLearnedCards(id!!, false))
        binding.timelineProgress.max = cardList.size
        return cardList.size
    }

    private fun setUpQuestion() {
        val id = intent.getStringExtra("id")!!
        cardList.clear()
        cardList.addAll(cardDAO.getIsLearnedCards(id, false))
        val cardListAll = cardDAO.getLessonCards(id)

        if (cardList.size == 0) {
            finishQuiz()
        }

        if (cardList.isNotEmpty()) {
            val randomCard = cardList.random()
            cardListAll.remove(randomCard)

            val incorrectAnswer = cardListAll.shuffled().take(1)

            val random = (0..1).random()
            if (random == 0) {
                binding.answerTv.text = randomCard.symbol
            } else {
                binding.answerTv.text = incorrectAnswer[0].symbol
            }

            Glide.with(baseContext)
                .load(Uri.parse("file:///android_asset/images/${randomCard.id}.jpg"))
                .into(binding.itemImage)

            binding.trueBtn.setOnClickListener {
                if (random == 0) {
                    correctDialog(randomCard.id)
                    cardDAO.updateCardIsLearned(randomCard.id, 1)
                    setUpQuestion()
                    progress++
                    increaseProgress()
                } else {
                    wrongDialog(randomCard.symbol, incorrectAnswer[0].symbol)
                    setUpQuestion()
                }
            }
            binding.falseBtn.setOnClickListener {
                if (random == 1) {
                    correctDialog(randomCard.id)
                    cardDAO.updateCardIsLearned(randomCard.id, 1)
                    setUpQuestion()
                    progress++
                    increaseProgress()
                } else {
                    wrongDialog(randomCard.symbol, incorrectAnswer[0].symbol)
                    setUpQuestion()
                }
            }
        }
    }

    private fun increaseProgress() {
        binding.timelineProgress.progress = progress
    }

    private fun finishQuiz() { //1 quiz, 2 learn
        binding.timelineProgress.progress = setUpProgressBar()
        runOnUiThread {

            PopupDialog.getInstance(this)
                .setStyle(Styles.SUCCESS)
                .setHeading(getString(R.string.finish))
                .setDescription(getString(R.string.finish_quiz))
                .setDismissButtonText(getString(R.string.ok))
                .setNegativeButtonText(getString(R.string.cancel))
                .setPositiveButtonText(getString(R.string.ok))
                .setCancelable(false)
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
        val dialog = AlertDialog.Builder(this)
        val dialogBinding = DialogCorrectBinding.inflate(layoutInflater)
        dialog.setView(dialogBinding.root)
        dialog.setCancelable(true)
        val builder = dialog.create()
        dialogBinding.questionTv.text = answer
        dialog.setOnDismissListener {
        }

        builder.show()
    }

    private fun wrongDialog(answer: String, userAnswer: String) {
        val dialog = AlertDialog.Builder(this)
        val dialogBinding = DialogWrongBinding.inflate(layoutInflater)
        dialog.setView(dialogBinding.root)
        dialog.setCancelable(true)
        val builder = dialog.create()
        dialogBinding.explanationTv.text = answer
        dialogBinding.yourExplanationTv.text = userAnswer
        dialogBinding.continueTv.setOnClickListener {
            builder.dismiss()
        }
        builder.setOnDismissListener {

        }
        builder.show()
    }
}