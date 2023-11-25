import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MapEditor {
    public static void main(String[] args) throws IOException, InterruptedException {
        MapEditorFrame frame = new MapEditorFrame();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            frame.repaint();
            Thread.sleep(10);
            if (in.ready()) {
                String[] input = in.readLine().split(" ");
                if (input.length < 2) continue;

                if (input[0].equals("SAVE")) {
                    Map.save(frame.map, input[1]);
                } else if (input[0].equals("OPEN")) {
                    frame.map = Map.open(input[1]);
                }
            }
        }
    }
}