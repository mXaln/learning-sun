package org.bibletranslationtools.sun.adapter.flashcard

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.data.dao.CardDAO
import org.bibletranslationtools.sun.data.dao.FolderDAO
import org.bibletranslationtools.sun.data.model.FlashCard
import org.bibletranslationtools.sun.databinding.ItemSetFolderBinding
import org.bibletranslationtools.sun.ui.activities.set.ViewSetActivity

class SetFolderViewAdapter(
    private val flashcardList: ArrayList<FlashCard>,
    private val isSelect: Boolean = false,
    private val folderId: String = ""
) : RecyclerView.Adapter<SetFolderViewAdapter.SetFolderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetFolderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSetFolderBinding.inflate(layoutInflater, parent, false)
        return SetFolderViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SetFolderViewHolder, position: Int) {
        val flashcard = flashcardList[position]
        val cardDAO = CardDAO(holder.itemView.context)
        val count = cardDAO.countCardByFlashCardId(flashcard.id)
        val folderDAO = FolderDAO(holder.itemView.context)

        holder.binding.setNameTv.text = flashcard.name
        holder.binding.termCountTv.text = "$count terms"
        if (isSelect) {

            if (folderDAO.isFlashCardInFolder(folderId, flashcard.id)) {
                holder.binding.setFolderItem.background =
                    AppCompatResources.getDrawable(
                        holder.itemView.context,
                        org.bibletranslationtools.sun.R.drawable.background_select
                    )
            } else {
                holder.binding.setFolderItem.background =
                    AppCompatResources.getDrawable(
                        holder.itemView.context,
                        org.bibletranslationtools.sun.R.drawable.background_unselect
                    )
            }


            }

        holder.binding.setFolderItem.setOnClickListener {
            if (isSelect) {
                if (folderDAO.isFlashCardInFolder(folderId, flashcard.id)) {
                    folderDAO.removeFlashCardFromFolder(folderId, flashcard.id)
                    holder.binding.setFolderItem.background =
                        AppCompatResources.getDrawable(
                            holder.itemView.context,
                            org.bibletranslationtools.sun.R.drawable.background_unselect
                        )
                } else {
                    folderDAO.addFlashCardToFolder(folderId, flashcard.id)
                    holder.binding.setFolderItem.background =
                        AppCompatResources.getDrawable(
                            holder.itemView.context,
                            org.bibletranslationtools.sun.R.drawable.background_select
                        )
                }
            } else {
                Intent(holder.itemView.context, ViewSetActivity::class.java).also {
                    it.putExtra("id", flashcard.id)
                    holder.itemView.context.startActivity(it)
                }
            }

        }
    }


    override fun getItemCount(): Int {
        return flashcardList.size
    }

    class SetFolderViewHolder(val binding: ItemSetFolderBinding) : RecyclerView.ViewHolder(binding.root)
}