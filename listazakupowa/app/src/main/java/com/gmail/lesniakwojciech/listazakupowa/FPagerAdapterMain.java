package com.gmail.lesniakwojciech.listazakupowa;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class FPagerAdapterMain
        extends FragmentPagerAdapter {
    private final String[] titles;

    public FPagerAdapterMain(final FragmentManager fm, final Resources resources) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        titles = new String[]{
                resources.getString(FragmentWKoszyku.title),
                resources.getString(FragmentDoKupienia.title),
                resources.getString(FragmentProdukty.title)
        };
    }

    public static void tabLayout(final ViewPager viewPager, final TabLayout tabLayout,
                                 final Resources resources) {
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            private final int[] tabColors = {
                    ResourcesCompat.getColor(resources, FragmentWKoszyku.color, null),
                    ResourcesCompat.getColor(resources, FragmentDoKupienia.color, null),
                    ResourcesCompat.getColor(resources, FragmentProdukty.color, null)
            };

            @Override
            public void onPageSelected(final int i) {
                tabLayout.setSelectedTabIndicatorColor(tabColors[i]);
            }
        });

        tabLayout.setupWithViewPager(viewPager);

        final int[] tabIcons = {
                FragmentWKoszyku.icon,
                FragmentDoKupienia.icon,
                FragmentProdukty.icon
        };
        for (int i = 0, d = tabLayout.getTabCount(); d > i; ++i) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }

    @NonNull
    @Override
    public Fragment getItem(final int i) {
        switch (i) {
            case 0:
                return new FragmentWKoszyku();
            case 2:
                return new FragmentProdukty();
            default:
                return new FragmentDoKupienia();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return titles[position];
    }
}
