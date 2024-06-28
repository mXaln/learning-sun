package org.bibletranslationtools.sun.adapter.grid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.GridItemBinding

class GridCardAdapter(
    context: Context,
    items: List<Card>
) : ArrayAdapter<Card>(context, 0, items) {

    private lateinit var binding: GridItemBinding
    private var selectedPosition = -1
    private var selectedCorrect = false

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = if (convertView != null) {
            GridItemBinding.bind(convertView)
        } else {
            GridItemBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        val card = getItem(position)
        binding.cardText.text = card?.symbol

        if (selectedPosition == position) {
            if (selectedCorrect) {
                binding.cardFrame.isActivated = true
            } else {
                binding.cardFrame.isSelected = true
            }
        }

        return binding.root
    }

    fun selectCorrect(position: Int) {
        selectedPosition = position
        selectedCorrect = true
        notifyDataSetChanged()
    }

    fun selectIncorrect(position: Int) {
        selectedPosition = position
        selectedCorrect = false
        notifyDataSetChanged()
    }

    fun resetSelection() {
        selectedPosition = -1
        selectedCorrect = false
        notifyDataSetChanged()
    }
}