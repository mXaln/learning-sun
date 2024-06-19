package org.bibletranslationtools.sun.ui.activities.set

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kennyc.bottomsheet.BottomSheetListener
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.dao.CardDAO
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Status
import org.bibletranslationtools.sun.databinding.ActivityViewSetBinding
import org.bibletranslationtools.sun.ui.activities.learn.LearnActivity
import org.bibletranslationtools.sun.ui.activities.learn.QuizActivity
import org.bibletranslationtools.sun.ui.activities.learn.TrueFalseFlashCardsActivity

class ViewSetActivity : AppCompatActivity() {
    private val binding by lazy { ActivityViewSetBinding.inflate(layoutInflater) }
    private val cardDAO by lazy { CardDAO(this) }
    private val cards = arrayListOf<Card>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupCardData()
        setupNavigationListener()
        setupUserDetails()
        setupReviewClickListener()
        setupLearnClickListener()
        setTrueFalseClickListener()
        setupToolbarNavigation()
    }

    private fun setTrueFalseClickListener() {
        binding.trueFalseCl.setOnClickListener {
            val intent = Intent(this, TrueFalseFlashCardsActivity::class.java)
            intent.putExtra("id", getIntent().getStringExtra("id"))
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupUserDetails() {
        val id = intent.getStringExtra("id")!!
        binding.termCountTv.text = getString(
            R.string.terms_count,
            cardDAO.countLessonCards(id)
        )
        binding.setNameTv.text = getString(
            R.string.lesson_name,
            id
        )
    }

    private fun setupReviewClickListener() {
        binding.reviewCl.setOnClickListener { v: View? ->
            val intent = Intent(this, LearnActivity::class.java)
            intent.putExtra("id", getIntent().getStringExtra("id"))
            startActivity(intent)
        }
    }

    private fun setupLearnClickListener() {
        binding.learnCl.setOnClickListener { v: View? ->
            if (cardDAO.countLessonCards(intent.getStringExtra("id")!!) < 4) {
                showReviewErrorDialog()
            } else {
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("id", getIntent().getStringExtra("id"))
                startActivity(intent)
            }
        }
    }

    private fun showReviewErrorDialog() {
        PopupDialog.getInstance(this)
            .setStyle(Styles.FAILED)
            .setHeading(getString(R.string.error))
            .setDescription(getString(R.string.learn_error))
            .setDismissButtonText(getString(R.string.ok))
            .setCancelable(true)
            .showDialog(object : OnDialogButtonClickListener() {
                override fun onDismissClicked(dialog: Dialog) {
                    super.onDismissClicked(dialog)
                    dialog.dismiss()
                }
            })
    }

    private fun setupToolbarNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupCardData() {
        val id = intent.getStringExtra("id")
        cards.clear()
        cards.addAll(cardDAO.getLessonCards(id!!))
        setUpProgress(cards)
    }

    private fun setupNavigationListener() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_view_set, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu) {
            BottomSheetMenuDialogFragment.Builder(this)
                .setSheet(R.menu.menu_bottom_view_set)
                .setTitle("Book")
                .setListener(object : BottomSheetListener {
                    override fun onSheetItemSelected(
                        bottomSheet: BottomSheetMenuDialogFragment,
                        item: MenuItem,
                        obj: Any?
                    ) {
                        val id = intent.getStringExtra("id")
                        when (item.itemId) {
                            else -> handleResetOption(id)
                        }
                    }

                    override fun onSheetShown(
                        bottomSheet: BottomSheetMenuDialogFragment,
                        obj: Any?
                    ) {}

                    override fun onSheetDismissed(
                        bottomSheet: BottomSheetMenuDialogFragment,
                        obj: Any?,
                        dismissEvent: Int
                    ) {}
                })
                .setCloseTitle(getString(R.string.close))
                .setAutoExpand(true)
                .setCancelable(true)
                .show(supportFragmentManager)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleResetOption(id: String?) {
        if (cardDAO.resetCard(id!!) > 0L) {
            Toast.makeText(
                this@ViewSetActivity,
                getString(R.string.reset_success),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@ViewSetActivity,
                getString(R.string.reset_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpProgress(cards: List<Card>) {
        var notLearned = 0
        var learning = 0
        var learned = 0
        for ((_, _, _, status) in cards) {
            when (status) {
                Status.IDLE -> notLearned++
                Status.LEARNED -> learned++
                Status.NOT_LEARNED -> learning++
            }
        }

        binding.notLearnTv.text = "Not learned: $notLearned"
        binding.isLearningTv.text = "Learning: $learning"
        binding.learnedTv.text = "Learned: $learned"
    }

    override fun onResume() {
        super.onResume()
        setupUserDetails()
    }
}
