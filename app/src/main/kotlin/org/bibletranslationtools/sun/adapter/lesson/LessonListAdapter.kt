package org.bibletranslationtools.sun.adapter.lesson

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
import org.bibletranslationtools.sun.ui.activities.learn.SymbolLearnActivity
import org.bibletranslationtools.sun.ui.activities.review.SymbolReviewActivity
import org.bibletranslationtools.sun.ui.activities.test.SentenceTestActivity
import org.bibletranslationtools.sun.ui.model.LessonModel

class LessonListAdapter(
    private val context: Context
) : ListAdapter<LessonModel, LessonListAdapter.SetsViewHolder>(callback) {

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

            val lessonAvailable = lesson.isAvailable
            val cardsLearnedProgress = lesson.cardsLearnedProgress
            val testSymbolsAvailable = cardsLearnedProgress == 100.0
            val cardsPassedProgress = lesson.cardsPassedProgress
            val sentencesAvailable = cardsPassedProgress == 100.0
            val sentencesPassedProgress = lesson.sentencesPassedProgress

            setLessonStatus(lessonAvailable, lesson.totalProgress, holder)

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
        available: Boolean,
        progress: Double,
        holder: SetsViewHolder
    ) {
        with(holder.binding) {
            root.isActivated = available

            when {
                available && progress == 100.0 -> {
                    lessonStatus.visibility = View.VISIBLE
                    lessonProgress.visibility = View.GONE
                }

                available && progress < 100.0 -> {
                    lessonStatus.visibility = View.GONE
                    lessonProgress.visibility = View.VISIBLE
                    lessonProgress.progress = progress.toInt()
                }

                else -> {
                    lessonStatus.visibility = View.VISIBLE
                    lessonProgress.visibility = View.GONE
                }
            }

            root.setOnClickListener {
                if (!available) return@setOnClickListener

                if (rooms.visibility == View.VISIBLE) {
                    rooms.visibility = View.GONE
                    lessonStatusContainer.visibility = View.VISIBLE
                    lessonName.typeface = Typeface.DEFAULT

                    root.isSelected = false
                } else {
                    rooms.visibility = View.VISIBLE
                    lessonStatusContainer.visibility = View.GONE
                    lessonName.typeface = Typeface.DEFAULT_BOLD

                    root.isSelected = true
                }
            }
        }
    }

    private fun setLearnSymbols(
        lessonId: String,
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
        lessonId: String,
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
        lessonId: String,
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
}