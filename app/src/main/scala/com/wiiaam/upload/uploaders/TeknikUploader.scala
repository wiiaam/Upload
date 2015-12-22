package com.wiiaam.upload.uploaders

import java.io.File

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.loopj.android.http._
import com.wiiaam.upload.R
import com.wiiaam.upload.tools.UploadManager.{UploadProgress, UploadResult, Upload}
import cz.msebera.android.httpclient.Header
import org.json.{JSONArray, JSONObject}
import rx.lang.scala.{Subscription, Observable}


object TeknikUploader extends Uploader{
  override val custom: Boolean = false
  override val key: Option[String] = None
  override val apiUrl: String = "https://api.teknik.io/upload/post"
  override val name: String = "Teknik"
  override val useKey: Boolean = false
  override val image: Either[Int, Bitmap] = Left(R.drawable.teknik)

  override def upload(context: Context, filepath: String): Observable[Upload] = {
    Observable(subscriber => {
      try{
        val uploadFile = new File(filepath)
        val params = new RequestParams()
        params.put("file", uploadFile)
        val client = new AsyncHttpClient(true, 80, 443)
        val post = client.post(context, apiUrl, params, new AsyncHttpResponseHandler() {

          override def onFailure(statusCode: Int, headers: Array[Header], response: Array[Byte], error: Throwable): Unit = {
            subscriber.onNext(Right(UploadResult(success = false, None)))

            subscriber.onCompleted()
          }

          override def onProgress(bytesWritten: Long, total: Long): Unit = {
            subscriber.onNext(Left(UploadProgress(bytesWritten, total)))
          }

          override def onSuccess(statusCode: Int, headers: Array[Header], response: Array[Byte]): Unit = {
            val json = new JSONArray(new String(response)).getJSONObject(0).getJSONObject("results").getJSONObject("file")
            if(json.has("url")) subscriber.onNext(Right(UploadResult(success = true, Some(json.getString("url")))))
            else subscriber.onNext(Right(UploadResult(success = false, None)))

            subscriber.onCompleted()
          }
        })

        subscriber.add(new Subscription {
          override def unsubscribe(): Unit ={
            post.cancel(true)
          }
        })

      }
      catch {
        case e: Exception =>
          Log.e("teknikuploader","upload failed", e)
          subscriber.onNext(Right(UploadResult(success = false,None)))
          subscriber.onCompleted()
      }
    })
  }
}
