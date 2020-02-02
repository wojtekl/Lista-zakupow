package com.gmail.lesniakwojciech.listazakupowa

import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.google.android.material.tabs.TabLayout

class FPagerAdapterMain(fragmentManager: FragmentManager, resources: Resources) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    companion object {
        fun tabLayout(
            viewPager: ViewPager,
            tabLayout: TabLayout,
            resources: Resources,
            currentItem: Int
        ) {
            viewPager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
                private val tabColors = intArrayOf(
                    ResourcesCompat.getColor(resources, FragmentWKoszyku.color, null),
                    ResourcesCompat.getColor(resources, FragmentDoKupienia.color, null),
                    ResourcesCompat.getColor(resources, FragmentProdukty.color, null)
                )

                override fun onPageSelected(i: Int) {
                    tabLayout.setSelectedTabIndicatorColor(tabColors[i])
                }
            })
            tabLayout.setupWithViewPager(viewPager)
            val tabIcons = intArrayOf(
                FragmentWKoszyku.icon,
                FragmentDoKupienia.icon,
                FragmentProdukty.icon
            )
            for (i in 0 until tabLayout.tabCount) {
                tabLayout.getTabAt(i)?.setIcon(tabIcons[i])
            }
            viewPager.currentItem = currentItem
        }
    }

    private val titles = arrayOf(
        resources.getString(FragmentWKoszyku.title),
        resources.getString(FragmentDoKupienia.title),
        resources.getString(FragmentProdukty.title)
    )

    override fun getItem(i: Int): Fragment {
        return when (i) {
            0 -> FragmentWKoszyku()
            2 -> FragmentProdukty()
            else -> FragmentDoKupienia()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}