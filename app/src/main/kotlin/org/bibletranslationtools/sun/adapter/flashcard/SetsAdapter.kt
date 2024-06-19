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
import org.bibletranslationtools.sun.databinding.ItemSetBinding
import org.bibletranslationtools.sun.ui.activities.set.ViewSetActivity

class SetsAdapter(
    private val context: Context,
    private val sets: List<Lesson>,
    private val isLibrary: Boolean
) : RecyclerView.Adapter<SetsAdapter.SetsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetsViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemSetBinding.inflate(inflater, parent, false)
        return SetsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    override fun onBindViewHolder(holder: SetsViewHolder, position: Int) {
        if (isLibrary) {
            //set weight of card
            val params = holder.binding.setCv.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        val set = sets[position]
        val cardDAO = CardDAO(context)
        val count = cardDAO.countLessonCards(set.id)

        holder.binding.setNameTv.text = context.getString(R.string.lesson_name, set.id)
        holder.binding.termCountTv.text = context.getString(R.string.terms_count, count)

        holder.itemView.setOnClickListener { v: View? ->
            val intent = Intent(context, ViewSetActivity::class.java)
            intent.putExtra("id", set.id)
            context.startActivity(intent)
        }
    }

    class SetsViewHolder(val binding: ItemSetBinding) : RecyclerView.ViewHolder(binding.root)
}