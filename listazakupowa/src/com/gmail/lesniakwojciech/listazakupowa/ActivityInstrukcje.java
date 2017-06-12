package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ActivityInstrukcje 
  extends Activity
{
  @Override
  public void onCreate(final Bundle bundle)
  {
    super.onCreate(bundle);
    setContentView(R.layout.activityinstrukcje);
    ((TextView)findViewById(R.id.aiTextView)).setText(getIntent().getStringExtra("INSTRUKCJE"));
  }
}
