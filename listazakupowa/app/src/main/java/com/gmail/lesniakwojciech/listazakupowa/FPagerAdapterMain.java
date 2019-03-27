package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;

public class FPagerAdapterMain
        extends FragmentPagerAdapter {
    private final Resources resources;

    public FPagerAdapterMain(final FragmentManager fm, final Context context) {
        super(fm);
        resources = context.getResources();
    }

    @Override
    public Fragment getItem(final int i) {
        switch (i) {
            case 0:
                return new FragmentWKoszyku();
            case 1:
                return new FragmentDoKupienia();
            case 2:
                return new FragmentProdukty();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        switch (position) {
            case 0:
                return resources.getString(R.string.wKoszyku);
            case 1:
                return resources.getString(R.string.doKupienia);
            case 2:
                return resources.getString(R.string.produkty);
        }
        return null;
    }

    public static void tabLayout(final ViewPager viewPager, final TabLayout tabLayout,
                                 final Resources resources){
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            private final int tabColors[] = {
                    ResourcesCompat.getColor(resources, R.color.amberA100, null),
                    ResourcesCompat.getColor(resources, R.color.redA100, null),
                    ResourcesCompat.getColor(resources, R.color.lightBlueA100, null)
            };

            @Override
            public void onPageSelected(final int i) {
                tabLayout.setSelectedTabIndicatorColor(tabColors[i]);
            }
        });

        tabLayout.setupWithViewPager(viewPager);

        final int tabIcons[] = {
                R.drawable.ic_shopping_cart,
                R.drawable.ic_list,
                R.drawable.ic_kitchen
        };
        for(int i = 0, d = tabLayout.getTabCount(); i < d; ++i) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }
}
