import 'package:flutter/cupertino.dart';
import 'package:provider/provider.dart';
import 'layout_connected.dart';
import 'layout_connecting.dart';
import 'layout_disconnected.dart';
import 'layout_disconnecting.dart';
import 'app_data.dart';

// Main application widget
class App extends StatefulWidget {
  const App({Key? key}) : super(key: key);

  @override
  AppState createState() => AppState();
}

// Main application state
class AppState extends State<App> {
  // Definir el contingut del widget 'App'
  Widget _setLayout(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);

    switch (appData.connectionStatus) {
      case ConnectionStatus.connected:
        return const LayoutConnected();
      case ConnectionStatus.connecting:
        return const LayoutConnecting();
      default:
        return const LayoutDisconnected();
    }
  }

  @override
  Widget build(BuildContext context) {
    // Farem servir la base 'Cupertino'
    return CupertinoApp(
      debugShowCheckedModeBanner: false,
      theme: const CupertinoThemeData(brightness: Brightness.light),
      home: _setLayout(context),
    );
  }
}
