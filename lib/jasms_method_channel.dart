import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'jasms_platform_interface.dart';

/// An implementation of [JasmsPlatform] that uses method channels.
class MethodChannelJasms extends JasmsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('jasms');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool> canSendSMS() async {
    final canSendSMS = await methodChannel.invokeMethod('canSendSMS');

    return canSendSMS;
  }

  @override
  Future<String> sendSMSDialog(List<String> numbers, String message) async {
    final data = {
      'numbers': numbers.join(';'),
      'message': message,
    };

    final result = await methodChannel.invokeMethod('sendSMSDialog', data);

    return result;
  }

  @override
  Future<String> sendSMSDirect(List<String> numbers, String message) async {
    final data = {
      'numbers': numbers.join(';'),
      'message': message,
    };

    final result = await methodChannel.invokeMethod('sendSMSDirect', data);

    return result;
  }
}
