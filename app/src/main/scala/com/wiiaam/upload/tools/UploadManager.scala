package com.wiiaam.upload.tools

import android.content.{ClipData, ClipboardManager, Context}
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.wiiaam.upload.R
import com.wiiaam.upload.uploaders.Uploader
import rx.android.schedulers.AndroidSchedulers
import rx.lang.scala.{Observable, JavaConversions}
import rx.lang.scala.schedulers.IOScheduler

import scala.collection.immutable.Queue
import scala.util.Random


object UploadManager {

  case class UploadResult(success: Boolean, url: Option[String])

  case class UploadProgress(bytesWritten: Long, totalSize: Long)

  abstract case class UploadRunnable(uploadID: Long) extends Runnable

  type Upload = Either[UploadProgress, UploadResult]

  var uploadQueue : Queue[UploadRunnable] = Queue()

  def upload(context: Context, fileLocation: Uri, uploader: Uploader): Unit = {

    val uploadRunnable = new UploadRunnable(Random.nextLong()) {

      override def run(): Unit = {
        val useNotifications = PreferenceManager.getDefaultSharedPreferences(context)
          .getBoolean("notifications_enabled",true)

        val filestring = parseUriToFilename(context, fileLocation)
        val subscriber = uploader.upload(context, filestring)
        val title = String.format(context.getString(R.string.upload_progress), uploader.name)
        val filename = filestring.split("/")(filestring.split("/").length - 1)
        val notifID = if(useNotifications)UploadNotificationManager.createNotification(context, title, filename) else 0

        subscriber.foreach {
          case Left(progress) =>
            if(useNotifications)UploadNotificationManager.updateProgress(context, notifID, title, filename, progress.totalSize, progress.bytesWritten)
          case Right(result) =>
            val finished = {
              if (result.success) String.format(context.getString(R.string.upload_finished), uploader.name)
              else String.format(context.getString(R.string.upload_failed), uploader.name)
            }
            val text = {
              if (result.success) result.url.get
              else filename
            }
            if(useNotifications)UploadNotificationManager.finish(context, notifID, finished, result.success, text)

            if (result.success) {
              Toast.makeText(context, R.string.upload_completed_toast, Toast.LENGTH_SHORT)

              result.url.foreach(url => {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[ClipboardManager]
                val clip = ClipData.newPlainText(s"${uploader.name} url", url)
                clipboard.setPrimaryClip(clip)
              })

            }

            Log.d(UploadManager.getClass.getSimpleName, s"Upload $filename to ${uploader.name} complete")
            if (UploadManager.uploadQueue.nonEmpty) {
              if (UploadManager.uploadQueue.front.uploadID == this.uploadID) {
                UploadManager.uploadQueue = UploadManager.uploadQueue.filterNot(_.uploadID == uploadID)
                if (UploadManager.uploadQueue.nonEmpty) {
                  UploadManager.doNextUpload()
                }
              }
            }
        }
      }
    }
    if(!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("uploads_allow_multiple", true)){
      uploadQueue = uploadQueue.enqueue(uploadRunnable)
      if(uploadQueue.length == 1) doNextUpload()
    }
    else uploadRunnable.run()
  }

  private def parseUriToFilename(ctx: Context, uri: Uri): String = {
    var selectedImagePath: String = null
    val fileManagerPath: String = uri.getPath
    val projection: Array[String] = Array("_data")
    val cursor = ctx.getContentResolver.query(uri, projection, null, null, null)
    if (cursor != null) {
      val column_index = cursor.getColumnIndexOrThrow("_data")
      cursor.moveToFirst()
      selectedImagePath = cursor.getString(column_index)
    }
    if (selectedImagePath != null) {
      selectedImagePath
    }
    else if (fileManagerPath != null) {
      fileManagerPath
    }
    else null
  }

  def doNextUpload(): Unit ={
    val nextUpload = uploadQueue.front
    nextUpload.run()
  }
}
