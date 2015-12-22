package com.wiiaam.upload.uploaders

import android.content.Context
import android.graphics.Bitmap
import com.wiiaam.upload.R
import com.wiiaam.upload.tools.UploadManager.Upload
import com.wiiaam.upload.uploaders.Uploader
import rx.lang.scala.Observable


case class CustomUploader(image: Either[Int, Bitmap], name: String) extends Uploader{

  override val custom: Boolean = false
  override val key: Option[String] = None
  override val apiUrl: String = "https://api.teknik.io/upload/post"
  override val useKey: Boolean = false

  override def upload(context: Context, filepath: String): Observable[Upload] = {
    Observable(subscriber => {

    })
  }

}
