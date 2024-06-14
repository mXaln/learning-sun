package org.bibletranslationtools.sun.ui.activities.set

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.adapter.flashcard.SetFolderViewAdapter
import org.bibletranslationtools.sun.data.dao.FlashCardDAO
import org.bibletranslationtools.sun.data.model.FlashCard
import org.bibletranslationtools.sun.databinding.ActivityAddFlashCardBinding
import org.bibletranslationtools.sun.preferen.UserSharePreferences

class AddFlashCardActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAddFlashCardBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: SetFolderViewAdapter
    private val userSharePreferences by lazy {
        UserSharePreferences(this)
    }
    private lateinit var flashCardDAO: FlashCardDAO
    private lateinit var flashCardList: ArrayList<FlashCard>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        //TODO: get all flashcard
//        flashCardDAO = FlashCardDAO(this)
//        flashCardList = flashCardDAO.getAllFlashCardByUserId(userSharePreferences.id)
//        adapter = SetFolderViewAdapter(flashCardList, true, intent.getStringExtra("id_folder")!!)
//        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        binding.flashcardRv.layoutManager = linearLayoutManager
//        binding.flashcardRv.adapter = adapter
//        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tick, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.done -> {
                onBackPressedDispatcher.onBackPressed()
                Toast.makeText(this, "Added to folder", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }


}