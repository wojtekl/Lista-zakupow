package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ActivityInstrukcje 
  extends Activity
{
  @Override
  public void onCreate(final Bundle bundle)
  {
    super.onCreate(bundle);
    setContentView(R.layout.activityinstrukcje);
    
    getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple200)));
    ((TextView)findViewById(R.id.aiTextView)).setText(getIntent().getStringExtra("INSTRUKCJE"));
  }
  
  @Override
  public boolean onCreateOptionsMenu(final Menu menu)
  {
    getMenuInflater().inflate(R.menu.activityinstrukcjeoptions, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(final MenuItem mi)
  {
    switch(mi.getItemId())
    {
      case R.id.aiPowrot:
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(mi);
    }
  }
}
