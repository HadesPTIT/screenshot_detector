import 'package:flutter_screenshot_detect/flutter_screenshot_detect_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

abstract class FlutterScreenshotDetectPlatform extends PlatformInterface {
  FlutterScreenshotDetectPlatform() : super(token: _token);

  static final Object _token = Object();
  static FlutterScreenshotDetectPlatform _instance =
      MethodChannelFlutterScreenshotDetect();

  static FlutterScreenshotDetectPlatform get instance => _instance;

  static set instance(FlutterScreenshotDetectPlatform instance) {
    PlatformInterface.verify(instance, _token);
    _instance = instance;
  }

  Stream<void> get onScreenshot {
    throw UnimplementedError('onScreenshot has not been implemented.');
  }
}
