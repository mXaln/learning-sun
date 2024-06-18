package org.bibletranslationtools.sun.adapter.card

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bibletranslationtools.sun.data.model.Card
import org.bibletranslationtools.sun.databinding.ItemViewSetBinding

class ViewSetAdapter(
    private val context: Context,
    private val cards: List<Card>
) : RecyclerView.Adapter<ViewSetAdapter.ViewSetViewHolder>() {

    private var textToSpeech: TextToSpeech? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewSetViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemViewSetBinding.inflate(inflater, parent, false)
        return ViewSetViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: ViewSetViewHolder, position: Int) {
        val card = cards[position]
        holder.binding.backTv.text = card.front
        holder.binding.frontTv.text = card.back
        holder.binding.cardViewFlip.flipDuration = 450
        holder.binding.cardViewFlip.isFlipEnabled = true
        holder.binding.cardViewFlip.setOnClickListener {
            holder.binding.cardViewFlip.flipTheView()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
        holder.binding.soundIv.setOnClickListener {
            if (holder.binding.backTv.text.toString().isNotEmpty()) {
                textToSpeech = TextToSpeech(context) { status: Int ->
                    if (status == TextToSpeech.SUCCESS) {
                        val result =
                            textToSpeech?.setLanguage(textToSpeech?.voice?.locale)
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Toast.makeText(context, "Language not supported", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val params = Bundle()
                            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                            textToSpeech?.speak(
                                holder.binding.backTv.text.toString(),
                                TextToSpeech.QUEUE_FLUSH,
                                params,
                                "UniqueID"
                            )
                        }
                    } else {
                        Toast.makeText(context, "Initialization failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        Glide.with(context)
            .load("https://raw.githubusercontent.com/mXaln/test_images/main/" + card.front + ".jpg")
            .into(holder.binding.itemImage)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    class ViewSetViewHolder(
        val binding: ItemViewSetBinding
    ) : RecyclerView.ViewHolder(binding.root)
}