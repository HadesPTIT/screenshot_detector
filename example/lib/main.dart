import 'package:flutter/material.dart';
import 'package:flutter_screenshot_detect/flutter_screenshot_detect.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: ScreenshotDetectDemo(),
    );
  }
}

class ScreenshotDetectDemo extends StatefulWidget {
  const ScreenshotDetectDemo({super.key});

  @override
  // ignore: library_private_types_in_public_api
  _ScreenshotDetectDemoState createState() => _ScreenshotDetectDemoState();
}

class _ScreenshotDetectDemoState extends State<ScreenshotDetectDemo> {
  final FlutterScreenshotDetect _detector = FlutterScreenshotDetect();
  final List<FlutterScreenshotEvent> _events = [];

  @override
  void initState() {
    super.initState();
    _detector.startListening((event) {
      debugPrint('Screenshot Taken');
      setState(() {
        _events.insert(0, event);
      });
    });
  }

  @override
  void dispose() {
    _detector.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Screenshot Detector Example'),
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Text(
              'Screenshots detected: ${_events.length}',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
          ),
          const Padding(
            padding: EdgeInsets.all(16.0),
            child: Text(
              'Take a screenshot to test the detector!',
              style: TextStyle(fontSize: 16),
            ),
          ),
          Expanded(
            child: ListView.builder(
              itemCount: _events.length,
              itemBuilder: (context, index) {
                final event = _events[index];
                return ListTile(
                  title: Text('Method: ${event.method}'),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('Time: ${event.timestamp}'),
                      if (event.path != null) Text('Path: ${event.path}'),
                    ],
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
