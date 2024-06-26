package org.bibletranslationtools.sun.adapter.card

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wajahatkarim3.easyflipview.EasyFlipView
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ItemLearnSetBinding

class CardLearnAdapter(
    private val listener: CardFlipListener
) : ListAdapter<Card, CardLearnAdapter.ViewHolder>(callback) {
    private lateinit var binding: ItemLearnSetBinding

    init {
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                if (binding.cardViewFlip.currentFlipState == EasyFlipView.FlipState.BACK_SIDE) {
                    binding.cardViewFlip.flipTheView()
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = ItemLearnSetBinding.inflate(layoutInflater, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = getItem(position)
        holder.bind(card)
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card) {
            binding.apply {
                frontTv.text = card.symbol
                cardViewFlip.setFlipTypeFromRight()
                cardViewFlip.setFlipDuration(500)
                cardViewFlip.setToHorizontalType()

                cardViewFlip.setOnFlipListener { _, _ ->
                    listener.onCardFlipped(card)
                }

                Glide.with(itemImage.context)
                    .load(Uri.parse("file:///android_asset/images/${card.id}.jpg"))
                    .fitCenter()
                    .into(itemImage)
            }
        }
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<Card>() {
            override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface CardFlipListener {
        fun onCardFlipped(card: Card)
    }
}