package org.bibletranslationtools.sun.adapter.symbol

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.data.model.Symbol
import org.bibletranslationtools.sun.databinding.GridItemBinding

class TestSymbolAdapter(
    private val listener: OnSymbolSelectedListener? = null
) : ListAdapter<Symbol, TestSymbolAdapter.ViewHolder>(callback) {

    interface OnSymbolSelectedListener {
        fun onSymbolSelected(symbol: Symbol, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = GridItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val symbol = getItem(position)
        holder.bind(symbol, position)
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<Symbol>() {
            override fun areItemsTheSame(oldItem: Symbol, newItem: Symbol): Boolean {
                return oldItem.id == newItem.id &&
                        oldItem.correct == newItem.correct &&
                        oldItem.selected == newItem.selected
            }

            override fun areContentsTheSame(oldItem: Symbol, newItem: Symbol): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(
        private val binding: GridItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(symbol: Symbol, position: Int) {
            binding.apply {
                cardText.text = symbol.name

                root.setOnClickListener {
                    if (!symbol.selected) {
                        symbol.selected = true
                        listener?.onSymbolSelected(symbol, position)
                    }
                }

                when(symbol.correct) {
                    true -> cardFrame.isActivated = true
                    false -> cardFrame.isSelected = true
                    else -> {
                        cardFrame.isActivated = false
                        cardFrame.isSelected = false
                    }
                }
            }
        }
    }

    fun selectCorrect(position: Int) {
        notifyItemChanged(position)
    }

    fun selectIncorrect(position: Int) {
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        notifyDataSetChanged()
    }
}