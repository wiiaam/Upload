package com.wiiaam.upload.tools

import android.content.Context
import com.wiiaam.upload.adapters.UploaderListAdapter
import com.wiiaam.upload.uploaders.{TeknikUploader, Uploader, ImgurUploader}


object Uploaders {

  case class Uploaders(adapter: UploaderListAdapter, names: Array[String])

  def getAll(context: Context): Uploaders ={
    val uploaders: Array[Uploader] = Array(ImgurUploader, TeknikUploader)
    val names = Array(ImgurUploader.name, TeknikUploader.name)
    Uploaders(new UploaderListAdapter(context, uploaders),names)
  }

  def getUploader(name: String): Option[Uploader] = name match {
    case ImgurUploader.name => Some(ImgurUploader)
    case TeknikUploader.name => Some(TeknikUploader)
    case _ => None
  }
}


