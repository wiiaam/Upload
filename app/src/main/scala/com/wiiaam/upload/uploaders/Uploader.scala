package com.wiiaam.upload.uploaders

import android.content.Context
import android.graphics.Bitmap
import com.wiiaam.upload.tools.UploadManager.Upload
import rx.lang.scala.Observable

trait Uploader {
  val custom: Boolean
  val name: String
  val apiUrl: String
  val useKey: Boolean
  val image: Either[Int, Bitmap]
  val key: Option[String]


  def upload(context: Context, filepath: String): Observable[Upload]
}
