package com.gmail.lesniakwojciech.listazakupowa;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter
{
    
    public TabsPagerAdapter(final FragmentManager fm)
    {
        super(fm);
    }
    
    @Override
    public Fragment getItem(final int i)
    {
        switch(i)
        {
            case 0:
                return new FragmentZabrane();
            case 1:
                return new FragmentDoKupienia();
            case 2:
                return new FragmentProdukty();
        }
        return new FragmentDoKupienia();
    }
    
    @Override
    public int getCount()
    {
        return 3;
    }

}
