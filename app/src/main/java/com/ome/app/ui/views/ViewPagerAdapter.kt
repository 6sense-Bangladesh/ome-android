package com.ome.app.ui.views

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    private val fragments: List<Fragment>,
    parent: Fragment
) : FragmentStateAdapter(parent) {

    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}