package org.bibletranslationtools.sun.adapter.flashcard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.data.dao.CardDAO
import org.bibletranslationtools.sun.data.model.Lesson
import org.bibletranslationtools.sun.databinding.ItemSetAllBinding
import org.bibletranslationtools.sun.ui.activities.set.ViewSetActivity

class SetAllAdapter(
    private val context: Context,
    private val sets: List<Lesson>
) : RecyclerView.Adapter<SetAllAdapter.SetAllViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetAllViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemSetAllBinding.inflate(inflater, parent, false)
        return SetAllViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    override fun onBindViewHolder(holder: SetAllViewHolder, position: Int) {
        val set = sets[position]
        val cardDAO = CardDAO(context)
        val count = cardDAO.countLessonCards(set.id)

        holder.binding.setNameTv.text = context.getString(R.string.lesson_name, set.id)
        holder.binding.termCountTv.text = context.getString(R.string.terms_count, count)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ViewSetActivity::class.java)
            intent.putExtra("id", set.id)
            context.startActivity(intent)
        }
    }

    class SetAllViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSetAllBinding.bind(itemView)
    }
}