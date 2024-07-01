package org.bibletranslationtools.sun.adapter.lesson

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.model.LessonWithCards
import org.bibletranslationtools.sun.databinding.ItemSetBinding
import org.bibletranslationtools.sun.ui.activities.set.LessonActivity

class LessonListAdapter(
    private val context: Context
) : ListAdapter<LessonWithCards, LessonListAdapter.SetsViewHolder>(callback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetsViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemSetBinding.inflate(inflater, parent, false)
        return SetsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SetsViewHolder, position: Int) {
        val lesson = getItem(position)
        val count = lesson.cards.size

        holder.binding.setNameTv.text = context.getString(R.string.lesson_name, lesson.lesson.id)
        holder.binding.termCountTv.text = context.getString(R.string.cards_count, count)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, LessonActivity::class.java)
            intent.putExtra("id", lesson.lesson.id)
            context.startActivity(intent)
        }
    }

    class SetsViewHolder(val binding: ItemSetBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val callback = object : DiffUtil.ItemCallback<LessonWithCards>() {
            override fun areItemsTheSame(oldItem: LessonWithCards, newItem: LessonWithCards): Boolean {
                return oldItem.lesson.id == newItem.lesson.id
            }

            override fun areContentsTheSame(oldItem: LessonWithCards, newItem: LessonWithCards): Boolean {
                return oldItem == newItem
            }
        }
    }
}