package org.bibletranslationtools.sun.ui.activities.set

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kennyc.bottomsheet.BottomSheetListener
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ActivityViewSetBinding
import org.bibletranslationtools.sun.ui.activities.learn.LearnActivity
import org.bibletranslationtools.sun.ui.activities.learn.QuizActivity
import org.bibletranslationtools.sun.ui.viewmodels.LessonViewModel

class LessonActivity : AppCompatActivity() {
    private val binding by lazy { ActivityViewSetBinding.inflate(layoutInflater) }
    private val viewModel: LessonViewModel by viewModels()
    private val ioScope = CoroutineScope(Dispatchers.IO)

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
        setupToolbarNavigation()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUserDetails() {
        viewModel.cards.observe(this) { cards ->
            intent.getStringExtra("id")?.let { lessonId ->
                binding.termCountTv.text = getString(
                    R.string.cards_count,
                    cards.size
                )
                binding.setNameTv.text = getString(
                    R.string.lesson_name,
                    lessonId
                )
            }
        }
    }

    private fun setupReviewClickListener() {
        binding.reviewCl.setOnClickListener { v: View? ->
            val intent = Intent(this, LearnActivity::class.java)
            intent.putExtra("id", getIntent().getStringExtra("id"))
            startActivity(intent)
        }
    }

    private fun setupLearnClickListener() {
        binding.learnCl.setOnClickListener {
            val size = viewModel.cards.value?.size ?: 0
            if (size < 4) {
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

    private fun setupCardData() {
        intent.getStringExtra("id")?.let {
            viewModel.loadCards(it)
        }
        viewModel.cards.observe(this) {
            setUpProgress(it)
        }
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
                .setListener(object : BottomSheetListener {
                    override fun onSheetItemSelected(
                        bottomSheet: BottomSheetMenuDialogFragment,
                        item: MenuItem,
                        obj: Any?
                    ) {
                        val id = intent.getStringExtra("id")!!
                        handleResetOption(id)
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

    private fun handleResetOption(id: String) {
        ioScope.launch {
            val reset = viewModel.resetCards(id)
            runOnUiThread {
                if (reset > 0) {
                    Toast.makeText(
                        this@LessonActivity,
                        getString(R.string.reset_success),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@LessonActivity,
                        getString(R.string.reset_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpProgress(cards: List<Card>) {
        var notLearnedCount = 0
        var learnedCount = 0
        for ((_, _, _, learned) in cards) {
            when (learned) {
                false -> notLearnedCount++
                else -> learnedCount++
            }
        }

        binding.notLearnTv.text = getString(R.string.not_learned_cards, notLearnedCount)
        binding.learnedTv.text = getString(R.string.learned_cards, learnedCount)
    }

    override fun onResume() {
        super.onResume()
        setupUserDetails()
    }
}
