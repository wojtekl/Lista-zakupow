package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FPAdapterListaZakupow 
  extends FragmentPagerAdapter
{
  private final Resources resources;
  
  public FPAdapterListaZakupow(final FragmentManager fm, final Context context)
  {
    super(fm);
    resources = context.getResources();
  }
  
  @Override
  public Fragment getItem(final int i)
  {
    switch(i)
    {
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
  public int getCount()
  {
    return 3;
  }
  
  @Override
  public CharSequence getPageTitle(final int position)
  {
    switch(position)
    {
      case 0:
        return resources.getString(R.string.wKoszyku);
      case 1:
        return resources.getString(R.string.doKupienia);
      case 2:
        return resources.getString(R.string.produkty);
    }
    return null;
  }
}
