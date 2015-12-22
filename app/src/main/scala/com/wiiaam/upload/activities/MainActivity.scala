package com.wiiaam.upload.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.{Menu, MenuItem}
import android.widget.ListView
import com.wiiaam.upload.R
import com.wiiaam.upload.tools.Uploaders

class MainActivity extends AppCompatActivity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val listView = findViewById(R.id.uploader_list).asInstanceOf[ListView]
    val uploaders = Uploaders.getAll(getApplicationContext)
    val adapter = uploaders.adapter
    listView.setAdapter(adapter)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    val inflater = getMenuInflater
    inflater.inflate(R.menu.main_activity, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.action_create =>
        val intent = new Intent(getApplicationContext, classOf[CreateUploaderActivity])
        startActivity(intent)
        true
      case R.id.action_settings =>
        val intent = new Intent(getApplicationContext, classOf[SettingsActivity])
        startActivity(intent)
        true
      case _ =>
        super.onOptionsItemSelected(item)
    }
  }

}
