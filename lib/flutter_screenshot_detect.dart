import 'package:flutter/services.dart';

class FlutterScreenshotDetect {
  static const EventChannel _eventChannel =
      EventChannel('com.ss.detect/events');

  Stream<FlutterScreenshotEvent>? _screenshotStream;

  /// Represents a screenshot detection event
  Stream<FlutterScreenshotEvent> get onScreenshot {
    _screenshotStream ??= _eventChannel.receiveBroadcastStream().map((event) =>
        FlutterScreenshotEvent.fromMap(event as Map<Object?, Object?>));
    return _screenshotStream!;
  }

  /// Starts listening for screenshot events
  void startListening(
      Function(FlutterScreenshotEvent event) onScreenshotTaken) {
    onScreenshot.listen(onScreenshotTaken);
  }

  /// Disposes of the screenshot stream
  void dispose() {
    _screenshotStream = null;
  }
}

/// Represents a screenshot detection event with additional metadata
class FlutterScreenshotEvent {
  /// The method used to detect the screenshot
  final String method;

  /// Timestamp when the screenshot was detected
  final DateTime timestamp;

  /// Path to the screenshot file (only available for content observer method)
  final String? path;

  FlutterScreenshotEvent({
    required this.method,
    required this.timestamp,
    this.path,
  });

  factory FlutterScreenshotEvent.fromMap(Map<Object?, Object?> map) {
    return FlutterScreenshotEvent(
      method: map['method'] as String,
      timestamp: DateTime.fromMicrosecondsSinceEpoch(map['timestamp'] as int),
      path: map['path'] as String?,
    );
  }

  @override
  String toString() {
    return 'FlutterScreenshotEvent(method: $method, timestamp: $timestamp, path: $path)';
  }
}
