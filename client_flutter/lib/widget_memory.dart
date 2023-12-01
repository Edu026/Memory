import 'package:flutter/cupertino.dart';
import 'package:client_flutter/widget_memory_painter.dart';
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

    return GestureDetector(
      onTapUp: (TapUpDetails details) {
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
          setState(() {});

          // Add your logic for handling the tapped cell here
        } else {
          // The tap occurred outside the valid cell range
          print("Tapped outside valid cell range");
        }
      },
      child: SizedBox(
        width: MediaQuery.of(context)
            .size
            .width, // Ocupa tot l'ample de la pantalla
        height: MediaQuery.of(context).size.height -
            56.0, // Ocupa tota l'altura disponible menys l'altura de l'AppBar
        child: CustomPaint(
          painter: WidgetMemoryPainter(appData),
        ),
      ),
    );
  }
}
