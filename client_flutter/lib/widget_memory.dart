import 'package:flutter/cupertino.dart';
import 'package:client_flutter/widget_memory_painter.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'app_data.dart';

class WidgetMemory extends StatefulWidget {
  const WidgetMemory({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => WidgetMemoryState();
}

class WidgetMemoryState extends State<WidgetMemory> {
  @override
  Widget build(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);
    appData.setUpCells();
    appData.enEspera;

    return GestureDetector(
      onTapUp: (TapUpDetails details) {
        // TURN CONTROL
        String turno = appData.turno;

        if (turno == appData.username && appData.flippedCards < 2) {
          Size size = context.size!;
          final int dimensions = appData.memoryBoard.length;

          double smallerDimension =
              size.width < size.height ? size.width : size.height;
          double cellDimension = (smallerDimension / dimensions) / 1.5;
          double separationOffset = 50.0;
          double offsetX = (size.width -
                  (cellDimension * dimensions +
                      (dimensions - 1) * separationOffset)) /
              2;
          double offsetY = (size.height -
                  (cellDimension * dimensions +
                      (dimensions - 1) * separationOffset)) /
              2;

          final double tappedX = details.localPosition.dx - offsetX;
          final double tappedY = details.localPosition.dy - offsetY;
          double totalCellSize = cellDimension + separationOffset;

          // Calculate row and column considering separation offset
          final int col = (tappedX / totalCellSize).floor();
          final int row = (tappedY / totalCellSize).floor();

          // Check if the tap occurred within valid cell indices
          if (col >= 0 && col < dimensions && row >= 0 && row < dimensions) {
            // A valid cell was tapped, you can now handle the tap event for the cell at (col, row)
            appData.revealColor(col, row);

            List card = [col, row];
            bool already_pressed = false;

            for (int i = 0; i < appData.pressedCards.length; i++) {
              // Code to execute for each element
              if (appData.pressedCards[i][0] != card[0] ||
                  appData.pressedCards[i][1] != card[1]) {
                already_pressed = false;
              } else {
                already_pressed = true;
              }
            }

            if (already_pressed == false) {
              appData.pressedCards.add(card);
              appData.flippedCards++;
              print("Card Flipped");
            }

            // Add your logic for handling the tapped cell here
          } else {
            // The tap occurred outside the valid cell range
            print("Tapped outside valid cell range");
          }
        }
        // CAMBIO DE TURNO
        if (appData.flippedCards == 2) {
          // COMP COLORES
          Future.delayed(const Duration(seconds: 1), () {
            appData.resetColor(
                appData.compareCards(appData.pressedCards, appData),
                appData.pressedCards,appData);
                setState(() {});

            // COMP GANADOR
            appData.winner = appData.checkWinner(appData.pressedCards);
          });
        }
        setState(() {});
      },
      child: Stack(
        children: [
          CustomPaint(
            painter: WidgetMemoryPainter(appData),
            child: SizedBox(
              width: MediaQuery.of(context).size.width,
              height: MediaQuery.of(context).size.height - 56.0,
            ),
          ),
          Positioned(
            top: 10,
            left: 10,
            child: Text("Torn de \"${appData.turno}\", ${appData.encert}",
                style: TextStyle(fontSize: 20, color: Colors.black)),
          ),
          Positioned(
            top: 10,
            right: 10,
            child: Text(
                "En espera: ${appData.enEspera}\", ${appData.encertsRival}",
                style: TextStyle(fontSize: 20, color: Colors.black)),
          ),
        ],
      ),
    );
  }
}
