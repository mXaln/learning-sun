package org.bibletranslationtools.sun.adapter.flashcard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.adapter.flashcard.SetCopyAdapter.SetCopyViewHolder
import org.bibletranslationtools.sun.data.dao.CardDAO
import org.bibletranslationtools.sun.data.model.FlashCard
import org.bibletranslationtools.sun.databinding.ItemSetCopyBinding
import org.bibletranslationtools.sun.preferences.UserSharePreferences
import org.bibletranslationtools.sun.ui.activities.set.ViewSetActivity

class SetCopyAdapter(
    private val context: Context,
    private val sets: List<FlashCard>
) : RecyclerView.Adapter<SetCopyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SetCopyViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemSetCopyBinding.inflate(inflater, parent, false)
        return SetCopyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    override fun onBindViewHolder(holder: SetCopyViewHolder, position: Int) {
        val set = sets[position]
        val cardDAO = CardDAO(context)
        val count = cardDAO.countCardByFlashCardId(set.id)

        holder.binding.setNameTv.text = set.name
        holder.binding.termCountTv.text = "$count terms"

        holder.itemView.setOnClickListener { v: View? ->
            val intent = Intent(context, ViewSetActivity::class.java)
            intent.putExtra("id", set.id)
            context.startActivity(intent)
        }
    }

    class SetCopyViewHolder(val binding: ItemSetCopyBinding) : RecyclerView.ViewHolder(binding.root)
}