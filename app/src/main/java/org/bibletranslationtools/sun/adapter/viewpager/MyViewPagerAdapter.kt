package org.bibletranslationtools.sun.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.bibletranslationtools.sun.ui.fragments.library.FoldersFragment
import org.bibletranslationtools.sun.ui.fragments.library.StudySetsFragment

class MyViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()

    fun getPageTitle(position : Int): String{
        return mFragmentTitleList[position]
    }

    fun getItem(position: Int): Fragment {
        return when(position) {
            1 -> FoldersFragment()
            else -> StudySetsFragment()
        }
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }
}