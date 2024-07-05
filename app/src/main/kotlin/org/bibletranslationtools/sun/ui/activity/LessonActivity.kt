package org.bibletranslationtools.sun.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.ui.adapter.LessonListAdapter
import org.bibletranslationtools.sun.databinding.ActivityLessonBinding
import org.bibletranslationtools.sun.ui.model.LessonModel
import org.bibletranslationtools.sun.ui.viewmodel.LessonViewModel

class LessonActivity : AppCompatActivity(), LessonListAdapter.OnLessonSelectedListener {
    private val binding by lazy { ActivityLessonBinding.inflate(layoutInflater) }
    private val viewModel: LessonViewModel by viewModels()
    private val lessonsAdapter by lazy { LessonListAdapter(this, this) }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null

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

        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent(baseContext, HomeActivity::class.java)
            startActivity(intent)
        }

        intent.getStringExtra("next")?.let { nextLessonId ->
            viewModel.setActiveLesson(nextLessonId)
        }
    }

    override fun onLessonSelected(lesson: LessonModel, position: Int) {
        viewModel.setActiveLesson(lesson.lesson.id)

        viewModel.lessons.value?.indexOfFirst { it.isSelected }?.let { prevPosition ->
            if (prevPosition >= 0 && prevPosition != position) {
                viewModel.lessons.value?.get(prevPosition)?.let { prevLesson ->
                    prevLesson.isSelected = false
                    lessonsAdapter.refreshLesson(prevPosition)
                }
            }
        }

        lesson.isSelected = !lesson.isSelected
        lessonsAdapter.refreshLesson(position)
    }

    private fun refreshData() {
        viewModel.loadLessons()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }
}
