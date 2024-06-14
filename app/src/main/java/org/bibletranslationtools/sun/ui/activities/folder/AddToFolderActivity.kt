package org.bibletranslationtools.sun.ui.activities.folder

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.adapter.folder.FolderSelectAdapter
import org.bibletranslationtools.sun.data.dao.FolderDAO
import org.bibletranslationtools.sun.databinding.ActivityAddToFolderBinding
import org.bibletranslationtools.sun.ui.activities.create.CreateFolderActivity

class AddToFolderActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAddToFolderBinding.inflate(layoutInflater) }
    private val folderDAO by lazy { FolderDAO(this) }
    private lateinit var adapter: FolderSelectAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupCreateNewFolder()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        // TODO: get all folder
//        val userSharePreferences = UserSharePreferences(this)
//        val folders = folderDAO.getAllFolderByUserId(userSharePreferences.id)
//        adapter = FolderSelectAdapter(folders, intent.getStringExtra("flashcard_id")!!)
//        val linearLayoutManager = LinearLayoutManager(
//            this,
//            LinearLayoutManager.VERTICAL,
//            false
//        )
//        binding.folderRv.layoutManager = linearLayoutManager
//        binding.folderRv.adapter = adapter
//        adapter.notifyDataSetChanged()
    }

    private fun setupCreateNewFolder() {
        binding.createNewFolderTv.setOnClickListener {
            startActivity(Intent(this, CreateFolderActivity::class.java))
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tick, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.done) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }
}