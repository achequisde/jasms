import 'package:flutter_test/flutter_test.dart';
import 'package:jasms/jasms.dart';
import 'package:jasms/jasms_platform_interface.dart';
import 'package:jasms/jasms_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockJasmsPlatform
    with MockPlatformInterfaceMixin
    implements JasmsPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final JasmsPlatform initialPlatform = JasmsPlatform.instance;

  test('$MethodChannelJasms is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelJasms>());
  });

  test('getPlatformVersion', () async {
    Jasms jasmsPlugin = Jasms();
    MockJasmsPlatform fakePlatform = MockJasmsPlatform();
    JasmsPlatform.instance = fakePlatform;

    expect(await jasmsPlugin.getPlatformVersion(), '42');
  });
}
