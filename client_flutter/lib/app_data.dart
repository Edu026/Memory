import 'dart:convert';
import 'dart:ffi';
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
  String username = 'Player';
  int encert = 0;
  String usernameRival = "";
  int encertsRival = 0;

  IOWebSocketChannel? _socketClient;
  ConnectionStatus connectionStatus = ConnectionStatus.disconnected;

  String? mySocketId;
  List<String> clients = [];
  String selectedClient = "";
  int? selectedClientIndex;
  String messages = "";

  bool file_saving = false;
  bool file_loading = false;

  String turno = 'Player';
  String enEspera = "";
  int flippedCards = 0;
  List pressedCards = [];
  String winner = '';

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

/*   selectClient(int index) {
    if (selectedClientIndex != index) {
      selectedClientIndex = index;
      selectedClient = clients[index];
    } else {
      selectedClientIndex = null;
      selectedClient = "";
    }
    notifyListeners();
  } */

/*   refreshClientsList() {
    final message = {
      'type': 'list',
    };
    _socketClient!.sink.add(jsonEncode(message));
  } */

/*   send(String msg) {
    if (selectedClientIndex == null) {
      broadcastMessage(msg);
    } else {
      privateMessage(msg);
    }
  } */

  broadcastMessage(String msg, String type) {
    final message = {
      'type': type,
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

/*   Future<void> saveFile(String fileName, Map<String, dynamic> data) async {
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
  } */

  /*
  * Read file example:
  
    final data = await readFile('myData.json');

  */
/* 
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
  } */

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

  bool compareCards(List pressedCards, AppData appData) {
    int lengthPressedCards = appData.pressedCards.length;
    print(appData.memoryBoard[pressedCards[lengthPressedCards - 1][0]]
        [pressedCards[lengthPressedCards - 1][1]][1]);

    print(appData.memoryBoard[pressedCards[lengthPressedCards - 2][0]]
        [pressedCards[lengthPressedCards - 2][1]][1]);

    if (appData.memoryBoard[pressedCards[lengthPressedCards - 1][0]]
            [pressedCards[lengthPressedCards - 1][1]][1] ==
        appData.memoryBoard[pressedCards[lengthPressedCards - 2][0]]
            [pressedCards[lengthPressedCards - 2][1]][1]) {
      encert++;
      return true;
    } else {
      return false;
    }
  }

  void resetColor(bool notReset, List pressedCards, AppData appData) {
    int lengthPressedCards = appData.pressedCards.length;
    if (notReset == false) {
      appData.memoryBoard[pressedCards[lengthPressedCards - 1][0]]
          [pressedCards[lengthPressedCards - 1][1]][0] = Colors.black;
      appData.memoryBoard[pressedCards[lengthPressedCards - 2][0]]
          [pressedCards[lengthPressedCards - 2][1]][0] = Colors.black;
    }
    appData.pressedCards.remove(lengthPressedCards - 1);
    appData.pressedCards.remove(lengthPressedCards - 2);
  }

  String checkWinner(List pressedCards) {
    if (pressedCards.length == 16) {
      if (encert > encertsRival) {
        broadcastMessage(username, 'winner');
        return username;
      } else if (encert < encertsRival) {
        broadcastMessage(usernameRival, 'winner');
        return usernameRival;
      } else if (encert == encertsRival) {
        broadcastMessage('empate', 'winner');
        return 'empate';
      }
    }
    return '';
  }

  void whoIsWaiting() {
    if (turno != username) {
      enEspera = username;
    } else {
      enEspera = usernameRival;
    }
  }
}
