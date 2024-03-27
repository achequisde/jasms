import 'package:jasms/jasms_method_channel.dart';

class Jasms {
  final _methodChannel = MethodChannelJasms();

  Future<String?> getPlatformVersion() {
    return _methodChannel.getPlatformVersion();
  }

  Future<bool> canSendSMS() async {
    return await _methodChannel.canSendSMS();
  }

  Future<String?> sendSMSDirect(List<String> numbers, String message) async {
    return await _methodChannel.sendSMSDirect(numbers, message);
  }

  Future<String?> sendSMSDialog(List<String> numbers, String message) async {
    return await _methodChannel.sendSMSDirect(numbers, message);
  }
}
