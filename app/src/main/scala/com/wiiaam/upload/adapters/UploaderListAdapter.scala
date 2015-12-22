package com.wiiaam.upload.adapters

import android.content.Context
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ImageView, TextView, ArrayAdapter}
import com.wiiaam.upload.R
import com.wiiaam.upload.uploaders.Uploader


class UploaderListAdapter(context: Context, values: Array[Uploader]) extends ArrayAdapter(context: Context, R.layout.uploader_list_view , values: Array[Uploader]) {

  override def getView(position: Int, convertView: View, parent: ViewGroup): View ={
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]
    val view = inflater.inflate(R.layout.uploader_list_view,parent,false)
    val textView = view.findViewById(R.id.uploader_name).asInstanceOf[TextView]
    val imageView = view.findViewById(R.id.uploader_icon).asInstanceOf[ImageView]
    val uploader = values(position)
    textView.setText(uploader.name)

    uploader.image match{
      case Left(res) =>
        imageView.setImageResource(res)
      case Right(bitmap) =>
        imageView.setImageBitmap(bitmap)
    }

    view
  }
}
