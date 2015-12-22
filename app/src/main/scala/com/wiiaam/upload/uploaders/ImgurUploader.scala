package com.wiiaam.upload.uploaders

import java.io.File

import android.content.Context
import android.graphics.Bitmap
import com.loopj.android.http.{JsonHttpResponseHandler, SyncHttpClient, RequestParams}
import com.wiiaam.upload.R
import com.wiiaam.upload.tools.UploadManager.{UploadProgress, Upload, UploadResult}
import org.json.{JSONArray, JSONObject}
import rx.lang.scala.Observable
import cz.msebera.android.httpclient.Header

object ImgurUploader extends Uploader{

  override val custom = false
  override val apiUrl: String = ""
  override val useKey: Boolean = true
  override val key: Option[String] = None
  override val image: Either[Int, Bitmap] = Left(R.drawable.imgur)
  override val name: String = "Imgur"

  override def upload(context: Context, filepath: String): Observable[Upload] = {
    Observable(subscriber => {
      try{
        val uploadFile = new File(filepath)
        val params = new RequestParams()
        params.put("file", uploadFile)
        val client = new SyncHttpClient(true, 80, 443)
        client.post(context, apiUrl, params, new JsonHttpResponseHandler {
          override def onFailure(statusCode: Int, headers: Array[Header], error: Throwable, responseBody: JSONObject): Unit = {
            subscriber.onNext(Right(UploadResult(success = false, None)))
          }

          override def onProgress(bytesWritten: Long, total: Long): Unit = {
            subscriber.onNext(Left(UploadProgress(bytesWritten, total)))
          }

          override def onSuccess(statusCode: Int, headers: Array[Header], response: JSONArray): Unit = {
            val json = response.getJSONObject(0).getJSONObject("results").getJSONObject("file")
            subscriber.onNext(Right(UploadResult(success = true, Some(""))))
          }
        })
      }
      catch {
        case e: Exception =>
          subscriber.onNext(Right(UploadResult(success = false,None)))
      }

      subscriber.onCompleted()
    })
  }
}
