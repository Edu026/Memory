import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';
// ignore: depend_on_referenced_packages
import 'package:web_socket_channel/io.dart';

// Access appData globaly with:
// AppData appData = Provider.of<AppData>(context);
// AppData appData = Provider.of<AppData>(context, listen: false)

enum ConnectionStatus {
  disconnected,
  disconnecting,
  connecting,
  connected,
}

class AppData with ChangeNotifier {
  String ip = "localhost";
  String port = "8888";
  String username = "Player";

  IOWebSocketChannel? _socketClient;
  ConnectionStatus connectionStatus = ConnectionStatus.disconnected;

  String? mySocketId;
  List<String> clients = [];
  String selectedClient = "";
  int? selectedClientIndex;
  String messages = "";

  bool file_saving = false;
  bool file_loading = false;

  AppData() {
    _getLocalIpAddress();
  }

  void _getLocalIpAddress() async {
    try {
      final List<NetworkInterface> interfaces = await NetworkInterface.list(
          type: InternetAddressType.IPv4, includeLoopback: false);
      if (interfaces.isNotEmpty) {
        final NetworkInterface interface = interfaces.first;
        final InternetAddress address = interface.addresses.first;
        ip = address.address;
        notifyListeners();
      }
    } catch (e) {
      // ignore: avoid_print
      print("Can't get local IP address : $e");
    }
  }

  void connectToServer() async {
    connectionStatus = ConnectionStatus.connecting;
    notifyListeners();

    // Simulate connection delay
    await Future.delayed(const Duration(seconds: 1));

    _socketClient = IOWebSocketChannel.connect("ws://$ip:$port");
    _socketClient!.stream.listen(
      (message) {
        final data = jsonDecode(message);

        if (connectionStatus != ConnectionStatus.connected) {
          connectionStatus = ConnectionStatus.connected;
        }

        switch (data['type']) {
          case 'list':
            clients = (data['list'] as List).map((e) => e.toString()).toList();
            clients.remove(mySocketId);
            messages += "List of clients: ${data['list']}\n";
            break;
          case 'id':
            mySocketId = data['value'];
            messages += "Id received: ${data['value']}\n";
            break;
          case 'connected':
            clients.add(data['id']);
            clients.remove(mySocketId);
            messages += "Connected client: ${data['id']}\n";
            break;
          case 'disconnected':
            String removeId = data['id'];
            if (selectedClient == removeId) {
              selectedClient = "";
            }
            clients.remove(data['id']);
            messages += "Disconnected client: ${data['id']}\n";
            break;
          case 'private':
            messages +=
                "Private message from '${data['from']}': ${data['value']}\n";
            break;
          default:
            messages += "Message from '${data['from']}': ${data['value']}\n";
            break;
        }

        notifyListeners();
      },
      onError: (error) {
        connectionStatus = ConnectionStatus.disconnected;
        mySocketId = "";
        selectedClient = "";
        clients = [];
        messages = "";
        notifyListeners();
      },
      onDone: () {
        connectionStatus = ConnectionStatus.disconnected;
        mySocketId = "";
        selectedClient = "";
        clients = [];
        messages = "";
        notifyListeners();
      },
    );
  }

  disconnectFromServer() async {
    connectionStatus = ConnectionStatus.disconnecting;
    notifyListeners();

    // Simulate connection delay
    await Future.delayed(const Duration(seconds: 1));

    _socketClient!.sink.close();
  }

  selectClient(int index) {
    if (selectedClientIndex != index) {
      selectedClientIndex = index;
      selectedClient = clients[index];
    } else {
      selectedClientIndex = null;
      selectedClient = "";
    }
    notifyListeners();
  }

  refreshClientsList() {
    final message = {
      'type': 'list',
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  send(String msg) {
    if (selectedClientIndex == null) {
      broadcastMessage(msg);
    } else {
      privateMessage(msg);
    }
  }

  broadcastMessage(String msg) {
    final message = {
      'type': 'broadcast',
      'value': msg,
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  privateMessage(String msg) {
    if (selectedClient == "") return;
    final message = {
      'type': 'private',
      'value': msg,
      'destination': selectedClient,
    };
    _socketClient!.sink.add(jsonEncode(message));
  }

  /*
  * Save file example:

    final myData = {
      'type': 'list',
      'clients': clients,
      'selectedClient': selectedClient,
      // i més camps que vulguis guardar
    };
    
    await saveFile('myData.json', myData);

  */

  Future<void> saveFile(String fileName, Map<String, dynamic> data) async {
    file_saving = true;
    notifyListeners();

    try {
      final directory = await getApplicationDocumentsDirectory();
      final file = File('${directory.path}/$fileName');
      final jsonData = jsonEncode(data);
      await file.writeAsString(jsonData);
    } catch (e) {
      // ignore: avoid_print
      print("Error saving file: $e");
    } finally {
      file_saving = false;
      notifyListeners();
    }
  }

  /*
  * Read file example:
  
    final data = await readFile('myData.json');

  */

  Future<Map<String, dynamic>?> readFile(String fileName) async {
    file_loading = true;
    notifyListeners();

    try {
      final directory = await getApplicationDocumentsDirectory();
      final file = File('${directory.path}/$fileName');
      if (await file.exists()) {
        final jsonData = await file.readAsString();
        final data = jsonDecode(jsonData) as Map<String, dynamic>;
        return data;
      } else {
        // ignore: avoid_print
        print("File does not exist!");
        return null;
      }
    } catch (e) {
      // ignore: avoid_print
      print("Error reading file: $e");
      return null;
    } finally {
      file_loading = false;
      notifyListeners();
    }
  }

  // Brand new code

  List<List<List<Color>>> memoryBoard = List.generate(
      4, (i) => List.generate(4, (j) => List.generate(2, (k) => Colors.black)));

  bool areTheCellsSet = false;

  void setUpCells() {
    if (!areTheCellsSet) {
      List<int> colorIndices = generateColors(8);
      List<Color> colors = [
        Colors.blueAccent,
        Colors.green,
        Colors.orange,
        Colors.red,
        Colors.deepPurple,
        Colors.pinkAccent,
        Colors.yellow,
        Colors.brown
      ];

      int index = 0;
      for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
          memoryBoard[i][j][1] = colors[colorIndices[index]];
          index++;
        }
      }
      areTheCellsSet = true;
    }
  }

  List<int> generateColors(int numPairs) {
    List<int> colors = [];
    for (int i = 0; i < numPairs; i++) {
      colors.addAll([i, i]);
    }
    colors.shuffle();
    return colors;
  }

  void revealColor(int row, int col) {
    memoryBoard[row][col][0] = Colors.white;
  }
}
