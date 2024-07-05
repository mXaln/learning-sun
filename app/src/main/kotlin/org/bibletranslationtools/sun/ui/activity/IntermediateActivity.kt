package org.bibletranslationtools.sun.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.bibletranslationtools.sun.R
import org.bibletranslationtools.sun.databinding.ActivityIntermediateBinding
import org.bibletranslationtools.sun.utils.TallyMarkConverter

const val TEST_SYMBOLS = 0
const val BUILD_SENTENCES = 1

class IntermediateActivity : AppCompatActivity() {
    private val binding by lazy { ActivityIntermediateBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null

        val id = intent.getIntExtra("id", 1)
        val type = intent.getIntExtra("type", TEST_SYMBOLS)

        if (type == TEST_SYMBOLS) {
            binding.pageTitle.text = getString(R.string.test_symbols)
            binding.image.setImageResource(R.drawable.ic_test_large)
            binding.startButton.setOnClickListener {
                val intent = Intent(baseContext, SymbolReviewActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            }
        } else {
            binding.pageTitle.text = getString(R.string.build_sentences)
            binding.image.setImageResource(R.drawable.ic_sentences_large)
            binding.startButton.setOnClickListener {
                val intent = Intent(baseContext, SentenceTestActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            }
        }

        binding.lessonTitle.text = getString(R.string.lesson_name, id)
        binding.lessonTally.text = TallyMarkConverter.toText(id)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}