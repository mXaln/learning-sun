package org.bibletranslationtools.sun.ui.activities.create

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.adapter.card.CardAdapter
import org.bibletranslationtools.sun.data.dao.CardDAO
import org.bibletranslationtools.sun.data.dao.FlashCardDAO
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.data.model.FlashCard
import org.bibletranslationtools.sun.databinding.ActivityCreateSetBinding
import org.bibletranslationtools.sun.ui.activities.set.ViewSetActivity

class CreateSetActivity : AppCompatActivity() {

    private val cardAdapter by lazy { CardAdapter(this, cards) }
    private val binding by lazy { ActivityCreateSetBinding.inflate(layoutInflater) }
    private val cardDAO by lazy { CardDAO(this) }
    private val cards = arrayListOf<Card>()
    private val flashCard = FlashCard()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view: View = binding.root
        setContentView(view)

        setupToolbar()
        setupSubjectEditText()
        setupDescriptionTextView()
        setupCardsList()
        setupCardAdapter()
        setupAddFab()
        setupItemTouchHelper()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSubjectEditText() {
        if (binding.subjectEt.text.toString().isEmpty()) {
            binding.subjectEt.requestFocus()
        }
    }

    private fun setupDescriptionTextView() {
        binding.descriptionTv.setOnClickListener {
            if (binding.descriptionTil.visibility == View.GONE) {
                binding.descriptionTil.visibility = View.VISIBLE
            } else {
                binding.descriptionTil.visibility = View.GONE
            }
        }
    }

    private fun setupCardsList() {
        cards.add(Card(flashcardId = flashCard.id))
        cards.add(Card(flashcardId = flashCard.id))
        updateTotalCards()
    }

    private fun updateTotalCards() {
        binding.totalCardsTv.text = String.format("Total Cards: %s", cards.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupCardAdapter() {
        binding.cardsLv.adapter = cardAdapter
        binding.cardsLv.layoutManager = LinearLayoutManager(this)
        binding.cardsLv.setHasFixedSize(true)
        cardAdapter.notifyDataSetChanged()
    }

    private fun setupAddFab() {
        binding.addFab.setOnClickListener {
            if (!checkTwoCardsEmpty()) {
                val newCard = Card(flashcardId = flashCard.id)
                cards.add(newCard)
                //scroll to last item
                binding.cardsLv.smoothScrollToPosition(cards.size - 1)
                //notify adapter
                cardAdapter.notifyItemInserted(cards.size - 1)
                updateTotalCards()
            } else {
                Toast.makeText(this, "Please enter front and back", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setupItemTouchHelper() {
        val callback: ItemTouchHelper.SimpleCallback = createItemTouchHelperCallback()
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.cardsLv)
    }

    private fun createItemTouchHelperCallback(): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                handleOnSwiped(viewHolder)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                handleOnChildDraw(c, viewHolder, dX)
            }
        }
    }

    private fun handleOnSwiped(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.bindingAdapterPosition

        // Backup of removed item for undo purpose
        val deletedItem: Card = cards[position]

        // Removing item from recycler view
        cards.removeAt(position)
        updateTotalCards()
        cardAdapter.notifyItemRemoved(position)

        // Showing Snack bar with an Undo option
        val snackBar =
            Snackbar.make(binding.root, "Item was removed from the list.", Snackbar.LENGTH_LONG)
        snackBar.setAction("UNDO") {
            // Check if the position is valid before adding the item back
            if (position <= cards.size) {
                cards.add(position, deletedItem)
                cardAdapter.notifyItemInserted(position)
                updateTotalCards()
            } else {
                // If the position isn't valid, show a message or handle the error appropriately
                Toast.makeText(applicationContext, "Error restoring item", Toast.LENGTH_LONG)
                    .show()
            }
        }
        snackBar.setActionTextColor(Color.YELLOW)
        snackBar.show()
    }

    private fun handleOnChildDraw(c: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float) {
        val icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_delete)
        val itemView = viewHolder.itemView
        assert(icon != null)
        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        if (dX < 0) { // Swiping to the left
            val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
            val iconRight = itemView.right - iconMargin
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

            val background = ColorDrawable(Color.WHITE)
            background.setBounds(
                itemView.right + (dX.toInt()),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            background.draw(c)
        } else { // No swipe
            icon.setBounds(0, 0, 0, 0)
        }

        icon.draw(c)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_set, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.done) {
            saveChanges()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveChanges() {
        val subject = binding.subjectEt.text.toString()
        val description = binding.descriptionEt.text.toString()

        if (subject.isEmpty()) {
            binding.subjectTil.error = "Please enter subject"
            binding.subjectEt.requestFocus()
            return
        } else {
            binding.subjectTil.error = null
        }

        if (!saveAllCards()) {
            return
        }

        saveFlashCard(subject, description)?.let {
            val intent = Intent(this, ViewSetActivity::class.java)
            intent.putExtra("id", it.id)
            startActivity(intent)
            finish()
        } ?: run {
            Toast.makeText(this, "Insert flashcard failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveAllCards(): Boolean {
        for (card in cards) {
            if (!saveCard(card)) {
                return false
            }
        }
        return true
    }

    private fun saveCard(card: Card): Boolean {
        if (card.front.isNullOrEmpty()) {
            binding.cardsLv.requestFocus()
            Toast.makeText(this, "Please enter front", Toast.LENGTH_SHORT).show()
            return false
        }

        if (card.back.isNullOrEmpty()) {
            binding.cardsLv.requestFocus()
            Toast.makeText(this, "Please enter back", Toast.LENGTH_SHORT).show()
            return false
        }

        if (cardDAO.insertCard(card) < 0) {
            Toast.makeText(this, "Insert card failed${flashCard.id}", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveFlashCard(subject: String, description: String): FlashCard? {
        val flashCardDAO = FlashCardDAO(this)
        flashCard.name = subject
        flashCard.description = description

        return if (flashCardDAO.insertFlashCard(flashCard) > 0) {
            flashCard
        } else null
    }

    private fun checkTwoCardsEmpty(): Boolean {
        // check if 2 cards are empty return true
        var emptyCount = 0
        for ((_, front, back) in cards) {
            if (front.isNullOrEmpty() || back.isNullOrEmpty()) {
                emptyCount++
                if (emptyCount == 2) {
                    return true
                }
            }
        }
        return false
    }
}