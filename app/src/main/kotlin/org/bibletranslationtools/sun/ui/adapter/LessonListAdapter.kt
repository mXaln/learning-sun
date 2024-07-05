package org.bibletranslationtools.sun.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.databinding.ItemLessonBinding
import org.bibletranslationtools.sun.ui.activity.SymbolLearnActivity
import org.bibletranslationtools.sun.ui.activity.SymbolReviewActivity
import org.bibletranslationtools.sun.ui.activity.SentenceTestActivity
import org.bibletranslationtools.sun.ui.model.LessonModel
import org.bibletranslationtools.sun.utils.TallyMarkConverter

class LessonListAdapter(
    private val context: Context,
    private val listener: OnLessonSelectedListener? = null
) : ListAdapter<LessonModel, LessonListAdapter.SetsViewHolder>(callback) {

    interface OnLessonSelectedListener {
        fun onLessonSelected(lesson: LessonModel, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetsViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemLessonBinding.inflate(inflater, parent, false)
        return SetsViewHolder(context, binding)
    }

    override fun onBindViewHolder(holder: SetsViewHolder, position: Int) {
        val lesson = getItem(position)
        val id = lesson.lesson.id

        with(holder.binding) {
            lessonName.text = context.getString(R.string.lesson_name, id)
            lessonTally.text = TallyMarkConverter.toText(id)

            val cardsLearnedProgress = lesson.cardsLearnedProgress
            val testSymbolsAvailable = cardsLearnedProgress == 100.0
            val cardsPassedProgress = lesson.cardsPassedProgress
            val sentencesAvailable = cardsPassedProgress == 100.0
            val sentencesPassedProgress = lesson.sentencesPassedProgress

            setLessonStatus(lesson, holder)

            setLearnSymbols(id, cardsLearnedProgress, this)
            setTestSymbols(id, testSymbolsAvailable, cardsPassedProgress, this)
            setBuildSentences(id, sentencesAvailable, sentencesPassedProgress, this)
        }
    }

    class SetsViewHolder(
        val context: Context,
        val binding: ItemLessonBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val callback = object : DiffUtil.ItemCallback<LessonModel>() {
            override fun areItemsTheSame(
                oldItem: LessonModel,
                newItem: LessonModel
            ): Boolean {
                return oldItem.lesson.id == newItem.lesson.id
            }

            override fun areContentsTheSame(
                oldItem: LessonModel,
                newItem: LessonModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    private fun setLessonStatus(
        lesson: LessonModel,
        holder: SetsViewHolder
    ) {
        with(holder.binding) {
            root.isActivated = lesson.isAvailable

            when {
                lesson.isAvailable && lesson.totalProgress == 100.0 -> {
                    lessonStatus.visibility = View.VISIBLE
                    lessonProgress.visibility = View.GONE
                }

                lesson.isAvailable && lesson.totalProgress < 100.0 -> {
                    lessonStatus.visibility = View.GONE
                    lessonProgress.visibility = View.VISIBLE
                    lessonProgress.progress = lesson.totalProgress.toInt()
                }

                else -> {
                    lessonStatus.visibility = View.VISIBLE
                    lessonProgress.visibility = View.GONE
                }
            }

            updateLessonCardSelection(this, lesson.isSelected)

            root.setOnClickListener {
                val currentLesson = getItem(holder.bindingAdapterPosition)
                if (!currentLesson.isAvailable) return@setOnClickListener
                listener?.onLessonSelected(currentLesson, holder.bindingAdapterPosition)
            }
        }
    }

    fun refreshLesson(position: Int) {
        notifyItemChanged(position)
    }

    private fun setLearnSymbols(
        lessonId: Int,
        progress: Double,
        binding: ItemLessonBinding
    ) {
        with(binding) {
            learnSymbols.isActivated = true

            if (progress == 100.0) {
                learnStatus.visibility = View.VISIBLE
                learnProgress.visibility = View.GONE
            } else {
                learnStatus.visibility = View.GONE
                learnProgress.visibility = View.VISIBLE
                learnProgress.progress = progress.toInt()
            }

            learnSymbols.setOnClickListener {
                val intent = Intent(context, SymbolLearnActivity::class.java)
                intent.putExtra("id", lessonId)
                context.startActivity(intent)
            }
        }
    }

    private fun setTestSymbols(
        lessonId: Int,
        available: Boolean,
        progress: Double,
        binding: ItemLessonBinding
    ) {
        with(binding) {
            testSymbols.isActivated = available

            when {
                available && progress == 100.0 -> {
                    testStatus.visibility = View.VISIBLE
                    testProgress.visibility = View.GONE
                }
                available && progress < 100.0 -> {
                    testStatus.visibility = View.GONE
                    testProgress.visibility = View.VISIBLE
                    testProgress.progress = progress.toInt()
                }
                else -> {
                    testStatus.visibility = View.VISIBLE
                    testProgress.visibility = View.GONE
                }
            }

            testSymbols.setOnClickListener {
                if (!available) return@setOnClickListener

                val intent = Intent(context, SymbolReviewActivity::class.java)
                intent.putExtra("id", lessonId)
                context.startActivity(intent)
            }
        }
    }

    private fun setBuildSentences(
        lessonId: Int,
        available: Boolean,
        progress: Double,
        binding: ItemLessonBinding
    ) {
        with(binding) {
            buildSentences.isActivated = available

            when {
                available && progress == 100.0 -> {
                    sentencesStatus.visibility = View.VISIBLE
                    sentencesProgress.visibility = View.GONE
                }
                available && progress < 100.0 -> {
                    sentencesStatus.visibility = View.GONE
                    sentencesProgress.visibility = View.VISIBLE
                    sentencesProgress.progress = progress.toInt()
                }
                else -> {
                    sentencesStatus.visibility = View.VISIBLE
                    sentencesProgress.visibility = View.GONE
                }
            }

            buildSentences.setOnClickListener {
                if (!available) return@setOnClickListener

                val intent = Intent(context, SentenceTestActivity::class.java)
                intent.putExtra("id", lessonId)
                context.startActivity(intent)
            }
        }
    }

    private fun updateLessonCardSelection(binding: ItemLessonBinding, selected: Boolean) {
        with(binding) {
            if (selected) {
                rooms.visibility = View.VISIBLE
                lessonStatusContainer.visibility = View.GONE
                lessonName.typeface = Typeface.DEFAULT_BOLD

                root.isSelected = true
            } else {
                rooms.visibility = View.GONE
                lessonStatusContainer.visibility = View.VISIBLE
                lessonName.typeface = Typeface.DEFAULT

                root.isSelected = false
            }
        }
    }
}