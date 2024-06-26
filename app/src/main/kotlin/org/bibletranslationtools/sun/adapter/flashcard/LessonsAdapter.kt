package org.bibletranslationtools.sun.adapter.flashcard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.databinding.ItemSetBinding
import org.bibletranslationtools.sun.ui.activities.set.LessonActivity

class LessonsAdapter(
    private val context: Context
) : ListAdapter<Lesson, LessonsAdapter.SetsViewHolder>(callback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetsViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemSetBinding.inflate(inflater, parent, false)
        return SetsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SetsViewHolder, position: Int) {
        val lesson = getItem(position)
        val count = lesson.cards.size

        holder.binding.setNameTv.text = context.getString(R.string.lesson_name, lesson.id)
        holder.binding.termCountTv.text = context.getString(R.string.cards_count, count)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, LessonActivity::class.java)
            intent.putExtra("id", lesson.id)
            context.startActivity(intent)
        }
    }

    class SetsViewHolder(val binding: ItemSetBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val callback = object : DiffUtil.ItemCallback<Lesson>() {
            override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
                return oldItem == newItem
            }
        }
    }
}