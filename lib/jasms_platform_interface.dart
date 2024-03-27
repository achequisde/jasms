import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'jasms_method_channel.dart';

abstract class JasmsPlatform extends PlatformInterface {
  /// Constructs a JasmsPlatform.
  JasmsPlatform() : super(token: _token);

  static final Object _token = Object();

  static JasmsPlatform _instance = MethodChannelJasms();

  /// The default instance of [JasmsPlatform] to use.
  ///
  /// Defaults to [MethodChannelJasms].
  static JasmsPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [JasmsPlatform] when
  /// they register themselves.
  static set instance(JasmsPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool> canSendSMS();
  Future<String> sendSMSDialog(List<String> numbers, String message);
  Future<String> sendSMSDirect(List<String> numbers, String message);
}
