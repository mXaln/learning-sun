package org.bibletranslationtools.sun.adapter.card

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ItemCardAddBinding

class CardAdapter(
    private val context: Context,
    private val cards: List<Card>
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemCardAddBinding.inflate(inflater, parent, false)
        return CardViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.removeTextWatchers()

        if (position > 1) {
            holder.binding.termEt.requestFocus()
        }

        holder.binding.termEt.setText(card.front)
        holder.binding.definitionEt.setText(card.back)

        val frontWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                card.front = s.toString().trim { it <= ' ' }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                card.front = s.toString().trim { it <= ' ' }
            }

            override fun afterTextChanged(s: Editable) {
                card.front = s.toString().trim { it <= ' ' }
            }
        }

        val backWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                card.back = s.toString().trim { it <= ' ' }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                card.back = s.toString().trim { it <= ' ' }
            }

            override fun afterTextChanged(s: Editable) {
                card.back = s.toString().trim { it <= ' ' }
            }
        }

        holder.setTextWatchers(frontWatcher, backWatcher)
    }

    class CardViewHolder(
        val binding: ItemCardAddBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var frontWatcher: TextWatcher? = null
        private var backWatcher: TextWatcher? = null

        fun removeTextWatchers() {
            if (frontWatcher != null) {
                binding.termEt.removeTextChangedListener(frontWatcher)
            }
            if (backWatcher != null) {
                binding.definitionEt.removeTextChangedListener(backWatcher)
            }
        }

        fun setTextWatchers(frontWatcher: TextWatcher?, backWatcher: TextWatcher?) {
            this.frontWatcher = frontWatcher
            this.backWatcher = backWatcher

            binding.termEt.addTextChangedListener(frontWatcher)
            binding.definitionEt.addTextChangedListener(backWatcher)
        }
    }
}