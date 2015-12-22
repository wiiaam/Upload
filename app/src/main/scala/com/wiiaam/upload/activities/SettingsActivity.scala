package com.wiiaam.upload.activities

import android.os.Bundle
import android.preference.PreferenceActivity
import com.wiiaam.upload.R
import java.util

class SettingsActivity extends PreferenceActivity{

  override def onCreate(savedInstanceState: Bundle): Unit ={
    super.onCreate(savedInstanceState)

    addPreferencesFromResource(R.xml.settings)
  }
}
