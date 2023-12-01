import 'package:client_flutter/app.dart';
import 'package:flutter/material.dart'; // per a 'CustomPainter'
import 'app_data.dart';

class WidgetMemoryPainter extends CustomPainter {
  final AppData appData;

  WidgetMemoryPainter(this.appData);

  @override
  void paint(Canvas canvas, Size size) {
    drawBoard(canvas, size);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    // maybe it can be modified

    return true;
  }

  void drawBoard(Canvas canvas, Size size) {
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

    for (var i = 0; i < dimensions; i++) {
      for (var j = 0; j < dimensions; j++) {
        Color paintColor;

        if (appData.memoryBoard[i][j][0] == Colors.black) {
          paintColor = Colors.black26;
        } else {
          paintColor = appData.memoryBoard[i][j][1];
        }
        final paint = Paint()..color = paintColor;

        Rect cellRect = Rect.fromLTWH(
            offsetX + i * (cellDimension + separationOffset),
            offsetY + j * (cellDimension + separationOffset),
            cellDimension,
            cellDimension);
        canvas.drawRect(cellRect, paint);
      }
    }
  }
}
