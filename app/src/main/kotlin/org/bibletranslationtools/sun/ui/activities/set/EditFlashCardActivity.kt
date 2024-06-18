package org.bibletranslationtools.sun.ui.activities.set

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.adapter.card.CardAdapter
import org.bibletranslationtools.sun.data.dao.CardDAO
import org.bibletranslationtools.sun.data.dao.FlashCardDAO
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ActivityEditFlashCardBinding
import com.google.android.material.snackbar.Snackbar
import org.bibletranslationtools.sun.data.model.FlashCard
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class EditFlashCardActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditFlashCardBinding.inflate(layoutInflater) }
    private val flashCardDAO by lazy { FlashCardDAO(this) }
    private val cardDAO by lazy { CardDAO(this) }
    private val cards = arrayListOf<Card>()
    private val listIdCard = arrayListOf<String>()
    private val cardAdapter by lazy { CardAdapter(this, cards) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val flashCardId = intent.getStringExtra("flashcard_id")
        val flashCard = flashCardDAO.getFlashCardById(flashCardId!!)!!

        flashCard.let {
            binding.subjectEt.setText(it.name)
            binding.descriptionEt.setText(it.description)
        }

        cards.clear()
        cards.addAll(cardDAO.getCardsByFlashCardId(flashCardId))
        updateTotalCards()

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.cardsLv.layoutManager = layoutManager
        binding.cardsLv.adapter = cardAdapter

        binding.addFab.setOnClickListener {
            if (!checkTwoCardsEmpty()) {
                val newCard = Card(flashcardId = flashCardId)
                cards.add(newCard)
                cardAdapter.notifyItemInserted(cards.size - 1)

                binding.cardsLv.scrollToPosition(cards.size - 1)
                binding.cardsLv.post {
                    val viewHolder = binding.cardsLv.findViewHolderForAdapterPosition(cards.size - 1)
                    viewHolder?.itemView?.requestFocus()
                }
                updateTotalCards()
            } else {
                Toast.makeText(this, "Please enter question and answer", Toast.LENGTH_SHORT).show()
            }

        }
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Your existing code
                    val deletedItem = cards[position]

                    // Removing item from recycler view
                    cards.removeAt(position)
                    updateTotalCards()
                    if (cardDAO.checkCardExist(deletedItem.id)) {
                        listIdCard.add(deletedItem.id)
                    }
                    cardAdapter.notifyItemRemoved(position)

                    // Showing Snack bar with an Undo option
                    val snackbar = Snackbar.make(binding.root, "Item was removed from the list.", Snackbar.LENGTH_LONG)
                    snackbar.setAction("UNDO") { _ ->

                        // Check if the position is valid before adding the item back
                        if (position <= cards.size) {
                            cards.add(position, deletedItem)
                            updateTotalCards()

                            if (listIdCard.contains(deletedItem.id)) {
                                listIdCard.remove(deletedItem.id)
                                Toast.makeText(
                                    this@EditFlashCardActivity,
                                    "Card deleted successfully" + deletedItem.id,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            cardAdapter.notifyItemInserted(position)
                        } else {
                            // If the position isn't valid, show a message or handle the error appropriately
                            Toast.makeText(applicationContext, "Error restoring item", Toast.LENGTH_LONG).show()
                        }
                    }
                    snackbar.setActionTextColor(Color.Yellow.toArgb())
                    snackbar.show()
                }
                // Backup of removed item for undo purpose

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
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_delete)
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                val iconBottom = iconTop + icon.intrinsicHeight

                if (dX < 0) { // Swiping to the left
                    val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                    val background = ColorDrawable(Color.White.toArgb())
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    background.draw(c)
                } else { // No swipe
                    icon.setBounds(0, 0, 0, 0)
                }

                icon.draw(c)
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.cardsLv)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_set, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.done -> {
                saveChange()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveChange() {
        val subject = binding.subjectEt.text.toString()
        val description = binding.descriptionEt.text.toString()

        if (subject.isEmpty()) {
            binding.subjectTil.error = "Please enter subject"
            binding.subjectEt.requestFocus()
            return
        }

        if (cards.size < 2) {
            showToast("Please enter at least 2 cards")
            return
        }

        val flashCardId = intent.getStringExtra("flashcard_id") ?: return

        for (card in cards) {
            if (card.front == null) {
                return
            }
            if (card.back == null) {
                return
            }
            if (card.front?.isEmpty() == true || card.back?.isEmpty() == true) {
                showToast("Please enter question and answer")
                updateCardView(cards.indexOf(card))
                return
            }

            if (cardDAO.checkCardExist(card.id)) {
                if (cardDAO.updateCardById(card) <= 0) {
                    showToast("Error updating card")
                    return
                }
            } else {
                if (cardDAO.insertCard(card) <= 0) {
                    showToast("Error inserting card")
                    return
                }
            }

            for (cardId in listIdCard) {
                cardDAO.deleteCardById(cardId)
            }

            flashCardDAO.getFlashCardById(flashCardId)?.let {
                val flashCard = FlashCard(
                    id = it.id,
                    name = subject,
                    description = description
                )
                if (flashCardDAO.updateFlashCard(flashCard) <= 0) {
                    showToast("Error updating flashcard")
                    return
                }
            }
        }
        showToast("Flashcard updated successfully")
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateCardView(position: Int) {
        cardAdapter.notifyItemChanged(position)
        binding.cardsLv.scrollToPosition(position)
        binding.cardsLv.post {
            val viewHolder = binding.cardsLv.findViewHolderForAdapterPosition(position)
            viewHolder?.itemView?.requestFocus()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun updateTotalCards() {
        binding.totalCardsTv.text = String.format("Total Cards: %d", cards.size)
    }

    private fun checkTwoCardsEmpty(): Boolean {
        // check if 2 cards are empty return true
        var emptyCount = 0
        for (card in cards) {
            if (card.front == null || card.front!!.isEmpty() || card.back == null || card.back!!.isEmpty()) {
                emptyCount++
                if (emptyCount == 2) {
                    return true
                }
            }
        }
        return false
    }
}