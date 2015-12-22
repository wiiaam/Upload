package com.wiiaam.upload.tools

import android.app.{Notification, NotificationManager, PendingIntent}
import android.content.{SharedPreferences, Context, Intent}
import android.net.Uri
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import com.wiiaam.upload.R

import scala.util.Random


object UploadNotificationManager {

  var mNotificationManager: Option[NotificationManager] = None

  def checkManagerDefined(ctx: Context): Unit ={
    if(mNotificationManager.isEmpty){
      mNotificationManager = Some(ctx.getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager])
    }
  }

  def createNotification(context: Context, title: String, filename: String): Int = {
    val id = Random.nextInt(100000)
    checkManagerDefined(context)
    val mBuilder = new NotificationCompat.Builder(context)
      .setProgress(100, 0, true)
      .setContentInfo("0%")
      .setContentTitle(title)
      .setContentText(filename)
      .setSmallIcon(R.drawable.appicon)

    val notification = mBuilder.build()

    notification.flags = Notification.FLAG_ONGOING_EVENT

    mNotificationManager.foreach(_.notify(id, notification))

    id
  }

  def updateProgress(context: Context, notificationID: Int, title: String, filename: String, total: Long, progress: Long): Unit ={
    checkManagerDefined(context)

    val prog: Int = math.floor((progress.asInstanceOf[Double] / total.asInstanceOf[Double]) * 100).asInstanceOf[Int]
    val percent = prog + "%"

    val mBuilder = new NotificationCompat.Builder(context)
      .setProgress(total.asInstanceOf[Int], progress.asInstanceOf[Int], false)
      .setContentInfo(percent)
      .setContentTitle(title)
      .setContentText(filename)
      .setSmallIcon(R.drawable.appicon)

    val notification = mBuilder.build()
    notification.flags = Notification.FLAG_ONGOING_EVENT

    mNotificationManager.foreach(_.notify(notificationID, notification))
  }

  def finish(context: Context, notificationID: Int, title: String, success: Boolean, url: String): Unit ={
    checkManagerDefined(context)


    if(success){
      val mBuilder = new NotificationCompat.Builder(context)
        .setContentTitle(title)
        .setContentText(url)
        .setSmallIcon(R.drawable.appicon)
      val browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
      val pendingIntent = PendingIntent.getActivity(context, 0, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT)

      mBuilder.setContentIntent(pendingIntent)

      addAlerts(mBuilder, PreferenceManager.getDefaultSharedPreferences(context))

      val notification = mBuilder.build()
      mNotificationManager.foreach(_.notify(notificationID, notification))
    }
    else{
      val mBuilder = new NotificationCompat.Builder(context)
        .setContentTitle(title)
        .setContentText(url)
        .setSmallIcon(R.drawable.appicon)

      val notification = mBuilder.build()
      mNotificationManager.foreach(_.notify(notificationID, notification))
    }

    def addAlerts(builder: NotificationCompat.Builder, preferences: SharedPreferences) {
      var defaults = 0
      if (preferences.getBoolean("notifications_enable_notifications", true)){
        if (preferences.getBoolean("notifications_sound", false)) defaults |= Notification.DEFAULT_SOUND
        if (preferences.getBoolean("notifications_vibrate", false)) defaults |= Notification.DEFAULT_VIBRATE else builder.setVibrate(Array(0L))
        if (preferences.getBoolean("notifications_light", false)) defaults |= Notification.DEFAULT_LIGHTS
        if (defaults != 0) builder.setDefaults(defaults)
      }

    }

  }
}
