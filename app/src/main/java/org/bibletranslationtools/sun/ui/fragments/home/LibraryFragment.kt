package org.bibletranslationtools.sun.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import org.bibletranslationtools.sun.adapter.viewpager.MyViewPagerAdapter
import org.bibletranslationtools.sun.databinding.FragmentLibraryBinding
import org.bibletranslationtools.sun.preferen.UserSharePreferences
import org.bibletranslationtools.sun.ui.activities.create.CreateFolderActivity
import org.bibletranslationtools.sun.ui.activities.create.CreateSetActivity

class LibraryFragment : Fragment() {
    private lateinit var binding: FragmentLibraryBinding
    private var userSharePreferences: UserSharePreferences? = null
    private var currentTabPosition = 0
    private val idUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSharePreferences = UserSharePreferences(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabLayout()
        setupUserPreferences()
        setupAddButton()
    }

    private fun setupViewPager() {
        val myViewPagerAdapter = MyViewPagerAdapter(requireActivity())
        binding.viewPager.setAdapter(myViewPagerAdapter)
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = position.toString()
        }.attach()
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentTabPosition = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    private fun setupUserPreferences() {
        userSharePreferences = UserSharePreferences(requireActivity())
    }

    private fun setupAddButton() {
        binding.addBtn.setOnClickListener {
            if (currentTabPosition == 0) {
                startActivity(Intent(activity, CreateSetActivity::class.java))
            } else if (currentTabPosition == 1) {
                startActivity(Intent(activity, CreateFolderActivity::class.java))
            }
        }
    }
}