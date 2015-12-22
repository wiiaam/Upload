package com.wiiaam.upload.activities

import android.app.{AlertDialog, Activity}
import android.content.DialogInterface.OnClickListener
import android.content.{Context, DialogInterface, Intent}
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.{AdapterView, ListView}
import com.wiiaam.upload.R
import com.wiiaam.upload.tools.{UploadManager, Uploaders}
import com.wiiaam.upload.uploaders.Uploader


class UploadActivity extends Activity {

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val intent = getIntent
    val extras = intent.getExtras

    val context: Context = this

    val uploaders = Uploaders.getAll(context)
    val adapter = uploaders.adapter
    val names = uploaders.names
    setContentView(R.layout.activity_upload)
    val listView = findViewById(R.id.uploader_list).asInstanceOf[ListView]
    listView.setAdapter(adapter)


    if(intent.getAction == Intent.ACTION_SEND){
      if (extras.containsKey(Intent.EXTRA_STREAM)) {
        val uri = extras.getParcelable(Intent.EXTRA_STREAM).asInstanceOf[Uri]
        listView.setOnItemClickListener(new OnItemClickListener {
          override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long): Unit = {
            val uploader = adapter.getItem(position)
            UploadManager.upload(context, uri, uploader)
            finish()
          }
        })
      }
    }
  }
}
