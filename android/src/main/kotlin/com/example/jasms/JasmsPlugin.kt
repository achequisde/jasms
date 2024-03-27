package com.example.jasms

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.security.Permission

/** JasmsPlugin */
class JasmsPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context: Context

  private var activity : Activity? = null

  private val REQUEST_CODE_SEND_SMS = 205

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "jasms")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onMethodCall(call: MethodCall, result: Result) {
    val message = call.argument<String?>("message")
    val numbers = call.argument<String?>("numbers")

    if (call.method == "sendSMSDialog") {
      if (canSendSMS() != true) {
        result.error(
          "device_not_able",
          "Device is not able to send SMS.",
          null,
        );
        return;
      }

      if (message != null && numbers != null) {
        sendSMSDialog(result, numbers, message)
        return;
      }

      result.error(
        "invalid_arguments",
        "Method received no valid arguments.",
        null,
      );

    } else if (call.method == "sendSMSDirect") {
      if (canSendSMS() != true) {
        result.error(
          "device_not_able",
          "Device is not able to send SMS.",
          null,
        );
        return;
      }

      if (message != null && numbers != null) {
        sendSMSDirect(result, numbers, message)
        return;
      }

      result.error(
        "invalid_arguments",
        "Method received no valid arguments.",
        null,
      );

    } else if (call.method == "canSendSMS") {
      result.success(canSendSMS())

    } else if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")

    } else {
      result.notImplemented()

    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  private fun canSendSMS(): Boolean? {
    return activity?.packageManager?.hasSystemFeature(
      android.content.pm.PackageManager.FEATURE_TELEPHONY
    )
  }

  private fun sendSMSDialog(result: Result, phones: String, message: String) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.SEND_SMS), REQUEST_CODE_SEND_SMS)
    }

    val intent = Intent(Intent.ACTION_SENDTO)

    intent.data = Uri.parse("smsto:$phones")
    intent.putExtra("sms_body", message)
    activity?.startActivityForResult(intent, REQUEST_CODE_SEND_SMS)

    result.success("")
  }

  private fun sendSMSDirect(result: Result, phones: String, message: String) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.SEND_SMS), REQUEST_CODE_SEND_SMS)
    }

    val sentIntent = PendingIntent.getBroadcast(
      activity,
      0,
      Intent("SMS_SENT"),
      PendingIntent.FLAG_IMMUTABLE,
    );

    val manager = android.telephony.SmsManager.getDefault();
    val numbers = phones.split(";")

    for (number in numbers) {
      if (message.toByteArray().size > 80) {
        val messagePart = manager.divideMessage(message)
        manager.sendMultipartTextMessage(number, null, messagePart, null, null)
      } else {
        manager.sendTextMessage(number, null, message, sentIntent, null)
      }
    }

    result.success("")
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivity() {
    activity = null
  }
}
