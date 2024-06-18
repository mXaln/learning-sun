package org.bibletranslationtools.sun.ui.activities.set

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kennyc.bottomsheet.BottomSheetListener
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.adapter.card.ViewSetAdapter
import org.bibletranslationtools.sun.data.dao.CardDAO
import org.bibletranslationtools.sun.data.dao.FlashCardDAO
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.Status
import org.bibletranslationtools.sun.databinding.ActivityViewSetBinding
import org.bibletranslationtools.sun.ui.activities.learn.LearnActivity
import org.bibletranslationtools.sun.ui.activities.learn.QuizActivity
import org.bibletranslationtools.sun.ui.activities.learn.TrueFalseFlashCardsActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID

class ViewSetActivity : AppCompatActivity() {
    private val binding by lazy { ActivityViewSetBinding.inflate(layoutInflater) }
    private val cardDAO by lazy { CardDAO(this) }
    private val flashCardDAO by lazy { FlashCardDAO(this) }
    private val linearLayoutManager by lazy { LinearLayoutManager(this) }
    private val cards = arrayListOf<Card>()
    private var listPosition = 0
    private var idCard: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupRecyclerView(savedInstanceState)
        setupCardData()
        setupNavigationListener()
        setupScrollListeners()
        setupOnScrollListener()
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

    private fun setupOnScrollListener() {
        binding.recyclerViewSet.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val centerPosition = linearLayoutManager.findFirstVisibleItemPosition() + 1
                binding.centerTv.text = centerPosition.toString()
                binding.previousTv.text =
                    if (centerPosition > 1) (centerPosition - 1).toString() else ""
                binding.nextTv.text =
                    if (centerPosition < cards.size) (centerPosition + 1).toString() else ""
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setupUserDetails() {
        val id = intent.getStringExtra("id")
        binding.descriptionTv.text = flashCardDAO.getFlashCardById(id!!)?.description
        binding.termCountTv.text = cardDAO.countCardByFlashCardId(intent.getStringExtra("id")!!)
            .toString() + " " + getString(R.string.term)
        binding.setNameTv.text =
            flashCardDAO.getFlashCardById(intent.getStringExtra("id")!!)?.name
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
            if (cardDAO.countCardByFlashCardId(intent.getStringExtra("id")!!) < 4) {
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

    private fun setupRecyclerView(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            listPosition = savedInstanceState.getInt(LIST_POSITION)
        }
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        binding.recyclerViewSet.layoutManager = linearLayoutManager
        binding.recyclerViewSet.scrollToPosition(listPosition)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupCardData() {
        val id = intent.getStringExtra("id")
        cards.clear()
        cards.addAll(cardDAO.getCardsByFlashCardId(id!!))
        setUpProgress(cards)
        val viewSetAdapter = ViewSetAdapter(this, cards)
        binding.recyclerViewSet.adapter = viewSetAdapter
        viewSetAdapter.notifyDataSetChanged()
    }

    private fun setupNavigationListener() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher
        }
    }

    private fun setupScrollListeners() {
        binding.previousIv.setOnClickListener { v: View? ->
            val currentPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
            if (currentPosition > 0) {
                binding.recyclerViewSet.scrollToPosition(currentPosition - 1)
            }
        }

        binding.nextIv.setOnClickListener { v: View? ->
            val currentPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            if (currentPosition < cards.size - 1) {
                binding.recyclerViewSet.scrollToPosition(currentPosition + 1)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(LIST_POSITION, linearLayoutManager.findFirstVisibleItemPosition())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_view_set, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu) {
            BottomSheetMenuDialogFragment.Builder(this)
                .setSheet(R.menu.menu_bottom_view_set)
                .setTitle(R.string.book)
                .setListener(object : BottomSheetListener {
                    override fun onSheetItemSelected(
                        bottomSheet: BottomSheetMenuDialogFragment,
                        item: MenuItem,
                        obj: Any?
                    ) {
                        val id = intent.getStringExtra("id")
                        when (item.itemId) {
                            R.id.edit -> handleEditOption(id)
                            R.id.delete_set -> handleDeleteSetOption(id)
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

    private fun handleEditOption(id: String?) {
        val intent = Intent(this@ViewSetActivity, EditFlashCardActivity::class.java)
        intent.putExtra("flashcard_id", id)
        startActivity(intent)
    }

    private fun handleDeleteSetOption(id: String?) {
        showDeleteSetDialog(id)
    }

    private fun handleResetOption(id: String?) {
        if (cardDAO.resetIsLearnedAndStatusCardByFlashCardId(id!!) > 0L) {
            Toast.makeText(
                this@ViewSetActivity,
                getString(R.string.reset_success),
                Toast.LENGTH_SHORT
            ).show()
            setupCardData()
        } else {
            Toast.makeText(
                this@ViewSetActivity,
                getString(R.string.reset_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDeleteSetDialog(id: String?) {
        PopupDialog.getInstance(this@ViewSetActivity)
            .setStyle(Styles.STANDARD)
            .setHeading(getString(R.string.delete_set))
            .setDescription(getString(R.string.delete_set_description))
            .setPopupDialogIcon(R.drawable.ic_delete)
            .setCancelable(true)
            .showDialog(object : OnDialogButtonClickListener() {
                override fun onPositiveClicked(dialog: Dialog) {
                    super.onPositiveClicked(dialog)
                    deleteSet(id)
                }

                override fun onNegativeClicked(dialog: Dialog) {
                    super.onNegativeClicked(dialog)
                    dialog.dismiss()
                }
            })
    }

    private fun deleteSet(id: String?) {
        val flashCardDAO = FlashCardDAO(this@ViewSetActivity)
        if (flashCardDAO.deleteFlashcardAndCards(id!!)) {
            PopupDialog.getInstance(this@ViewSetActivity)
                .setStyle(Styles.SUCCESS)
                .setHeading(getString(R.string.success))
                .setDescription(getString(R.string.delete_set_success))
                .setCancelable(false)
                .setDismissButtonText(getString(R.string.ok))
                .showDialog(object : OnDialogButtonClickListener() {
                    override fun onDismissClicked(dialog: Dialog) {
                        super.onDismissClicked(dialog)
                        finish()
                    }
                })
        } else {
            PopupDialog.getInstance(this@ViewSetActivity)
                .setStyle(Styles.FAILED)
                .setHeading(getString(R.string.error))
                .setDescription(getString(R.string.delete_set_error))
                .setCancelable(true)
                .showDialog(object : OnDialogButtonClickListener() {
                    override fun onPositiveClicked(dialog: Dialog) {
                        super.onPositiveClicked(dialog)
                    }
                })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpProgress(cards: List<Card>) {
        var notLearned = 0
        var learning = 0
        var learned = 0
        for ((_, _, _, _, status) in cards) {
            when (status) {
                Status.INITIAL -> notLearned++
                Status.RIGHT -> learned++
                Status.LEFT -> learning++
            }
        }

        binding.notLearnTv.text = "Not learned: $notLearned"
        binding.isLearningTv.text = "Learning: $learning"
        binding.learnedTv.text = "Learned: $learned"
    }

    override fun onResume() {
        super.onResume()
        setupCardData()
        setupUserDetails()
    }

    companion object {
        private const val LIST_POSITION = "list_position"
    }
}
