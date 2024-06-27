package org.bibletranslationtools.sun.adapter.test

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
        fun onSymbolSelected(symbol: Symbol)
    }

    private lateinit var binding: GridItemBinding

    private var selectedPosition = -1
    private var selectedCorrect = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = GridItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val symbol = getItem(position)
        holder.bind(symbol)
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<Symbol>() {
            override fun areItemsTheSame(oldItem: Symbol, newItem: Symbol): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Symbol, newItem: Symbol): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun bind(symbol: Symbol) {
            binding.apply {
                cardText.text = symbol.name

                root.setOnClickListener {
                    listener?.onSymbolSelected(symbol)
                }
            }
        }
    }

    fun selectCorrectCard(position: Int) {
        selectedPosition = position
        selectedCorrect = true
    }

    fun selectIncorrectCard(position: Int) {
        selectedPosition = position
        selectedCorrect = false
    }

    fun resetSelection() {
        selectedPosition = -1
        selectedCorrect = false
    }
}