package org.bibletranslationtools.sun.ui.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bibletranslationtools.sun.adapter.lesson.LessonListAdapter
import org.bibletranslationtools.sun.data.model.Setting
import org.bibletranslationtools.sun.data.model.LessonSuite
import org.bibletranslationtools.sun.data.model.TestSuite
import org.bibletranslationtools.sun.databinding.FragmentHomeBinding
import org.bibletranslationtools.sun.ui.viewmodels.MainViewModel
import org.bibletranslationtools.sun.utils.AssetsProvider

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: MainViewModel by viewModels()

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        importLessons().invokeOnCompletion {
            importTests()
        }

        setupLessons()
        setupSwipeRefreshLayout()

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
            binding.swipeRefreshLayout.isRefreshing = false
            Toast.makeText(requireActivity(), "Refreshed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importLessons(): Job {
        val mapper = ObjectMapper().registerKotlinModule()
        val reference = object : TypeReference<LessonSuite>() {}
        val json = AssetsProvider.readText(requireContext(), "lessons.json")

        return ioScope.launch {
            val dbVersion = viewModel.getLessonsVersion() ?: 0

            json?.let {
                val lessonSuite = mapper.readValue(it, reference)

                if (lessonSuite.version > dbVersion) {
                    for (lesson in lessonSuite.lessons) {
                        viewModel.insertLesson(lesson)
                        for (card in lesson.cards) {
                            card.lessonId = lesson.id
                            viewModel.insertCard(card)
                        }
                    }

                    viewModel.insertSetting(
                        Setting("lessonsVersion", lessonSuite.version.toString())
                    )
                }
            }
        }
    }

    private fun importTests(): Job {
        val mapper = ObjectMapper().registerKotlinModule()
        val reference = object : TypeReference<TestSuite>() {}
        val json = AssetsProvider.readText(requireContext(), "tests.json")

        return ioScope.launch {
            val dbVersion = viewModel.getTestsVersion() ?: 0

            json?.let {
                val testSuite = mapper.readValue(it, reference)

                if (testSuite.version > dbVersion) {
                    for (test in testSuite.tests) {
                        viewModel.insertTest(test)
                        for (sentence in test.sentences) {
                            sentence.testId = test.id
                            viewModel.insertSentence(sentence)
                            for (symbol in sentence.symbols) {
                                symbol.sentenceId = sentence.id
                                viewModel.insertSymbol(symbol)
                            }
                        }
                    }

                    viewModel.insertSetting(Setting("testsVersion", testSuite.version.toString()))

                    withContext(Dispatchers.Main) {
                        refreshData()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupLessons() {
        val setsAdapter = LessonListAdapter(requireActivity())
        viewModel.lessons.observe(viewLifecycleOwner) {
            setsAdapter.submitList(it)
        }
        val linearLayoutManager = LinearLayoutManager(
            requireActivity(),
            RecyclerView.VERTICAL,
            false
        )
        binding.setsRv.layoutManager = linearLayoutManager
        binding.setsRv.adapter = setsAdapter
        setsAdapter.notifyDataSetChanged()

        viewModel.loadLessons()
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun refreshData() {
        setupLessons()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }
}
