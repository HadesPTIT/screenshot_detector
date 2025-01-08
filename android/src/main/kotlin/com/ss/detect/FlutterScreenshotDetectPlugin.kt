package com.ss.detect

import android.app.Activity
import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel

class FlutterScreenshotDetectPlugin : FlutterPlugin, EventChannel.StreamHandler, ActivityAware {

    private var contentResolver: ContentResolver? = null
    private var eventSink: EventChannel.EventSink? = null
    private var screenshotObserver: ContentObserver? = null
    private lateinit var channel: EventChannel
    private var activity: Activity? = null
    private var screenCaptureCallback: Activity.ScreenCaptureCallback? = null

    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        contentResolver = binding.applicationContext.contentResolver
        channel = EventChannel(binding.binaryMessenger, "com.ss.detect/events")
        channel.setStreamHandler(this)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setStreamHandler(null)
        cleanupResources()
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        registerScreenCaptureCallbackIfNeeded()
    }

    override fun onDetachedFromActivity() {
        unregisterScreenCaptureCallback()
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        registerScreenCaptureCallbackIfNeeded()
    }

    override fun onDetachedFromActivityForConfigChanges() {
        unregisterScreenCaptureCallback()
        activity = null
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerScreenCaptureCallback()
        } else {
            registerContentObserver()
        }
    }

    override fun onCancel(arguments: Any?) {
        cleanupResources()
        eventSink = null
    }

    private fun registerScreenCaptureCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            screenCaptureCallback = Activity.ScreenCaptureCallback {
                Log.d("INFO", "Android ${Build.VERSION.RELEASE_OR_CODENAME} detected")
                eventSink?.success(
                    mapOf(
                        "method" to "screen_capture_callback",
                        "timestamp" to System.currentTimeMillis()
                    )
                )
            }
            activity?.registerScreenCaptureCallback(
                activity!!.mainExecutor, screenCaptureCallback!!
            )
        }
    }

    private fun unregisterScreenCaptureCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && screenCaptureCallback != null) {
            activity?.unregisterScreenCaptureCallback(screenCaptureCallback!!)
            screenCaptureCallback = null
        }
    }

    private fun registerContentObserver() {
        screenshotObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                Log.d("INFO", "Android ${Build.VERSION.RELEASE_OR_CODENAME} detected")
                uri?.let {
                    if (isScreenshotPath(it.path)) {
                        eventSink?.success(
                            mapOf(
                                "method" to "content_observer",
                                "timestamp" to System.currentTimeMillis(),
                                "path" to it.path
                            )
                        )
                    }
                }
            }
        }
        contentResolver?.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenshotObserver!!
        )
    }

    private fun cleanupResources() {
        try {
            contentResolver?.unregisterContentObserver(screenshotObserver!!)
            unregisterScreenCaptureCallback()
            screenshotObserver = null
        } catch (_: Exception) {
        }
    }

    private fun isScreenshotPath(path: String?): Boolean {
        return path?.let {
            it.contains("DCIM", ignoreCase = true) || it.contains(
                "Screenshots", ignoreCase = true
            ) || it.contains("external/images", ignoreCase = true)
        } ?: false
    }

    private fun registerScreenCaptureCallbackIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerScreenCaptureCallback()
        } else {
            registerContentObserver()
        }
    }
}
