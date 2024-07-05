package org.bibletranslationtools.sun.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.adapter.lesson.LessonListAdapter
import org.bibletranslationtools.sun.databinding.ActivityLessonBinding
import org.bibletranslationtools.sun.ui.viewmodels.MainViewModel

class LessonActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLessonBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null

        val lessonsAdapter = LessonListAdapter(this)
        binding.lessonsList.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
        binding.lessonsList.adapter = lessonsAdapter

        viewModel.lessons.observe(this) {
            lessonsAdapter.submitList(it)
            lessonsAdapter.notifyDataSetChanged()
        }

        viewModel.importStudyData().invokeOnCompletion {
            refreshData()
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun refreshData() {
        viewModel.loadLessons()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }
}
