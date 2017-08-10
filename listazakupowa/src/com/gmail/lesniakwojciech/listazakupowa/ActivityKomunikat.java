package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ActivityKomunikat 
  extends Activity
{
  @Override
  protected void onCreate(final Bundle bundle)
  {
    super.onCreate(bundle);
    setContentView(R.layout.activityinstrukcje);
    
    ((TextView)findViewById(R.id.aiTextView)).setText(getIntent().getStringExtra(ActivityMain.IE_KOMUNIKAT));
  }
  
  @Override
  public boolean onCreateOptionsMenu(final Menu menu)
  {
    getMenuInflater().inflate(R.menu.activitykomunikatoptions, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(final MenuItem mi)
  {
    switch(mi.getItemId())
    {
      case R.id.akPowroc:
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(mi);
    }
  }
}
