package org.bibletranslationtools.sun.adapter.card

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ItemLearnSetBinding

class CardLearnAdapter(
    private val cardList: List<Card>
) : RecyclerView.Adapter<CardLearnAdapter.ViewHolder>() {
    private var flippedStates = MutableList(cardList.size) { false }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLearnSetBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cardList[position]
        holder.bind(card)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    fun setCards(cards: List<Card>) {
        this.cardList as MutableList
        this.cardList.clear()
        this.cardList.addAll(cards)
        flippedStates = MutableList(cards.size) { false }
    }

    fun getCards(): List<Card> {
        return cardList
    }

    fun getCount(): Int {
        return cardList.size
    }

    inner class ViewHolder(private val binding: ItemLearnSetBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card) {
            binding.frontTv.text = card.symbol
            binding.cardViewFlip.setFlipTypeFromRight()
            binding.cardViewFlip.setFlipDuration(500)
            binding.cardViewFlip.setToHorizontalType()
            binding.cardViewFlip.setOnClickListener {
                binding.cardViewFlip.flipTheView()
            }

            Glide.with(binding.frontTv.context)
                .load(Uri.parse("file:///android_asset/images/${card.id}.jpg"))
                .into(binding.itemImage)
        }
    }
}