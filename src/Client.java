import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket connection = new Socket("localhost", 1337);
        ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(connection.getInputStream());

        int id = in.read();
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        ClientFrame frame = new ClientFrame(id, Map.open("BASE"), out);
        manager.addKeyEventDispatcher(frame);
        while (true) {
            frame.repaint();
            while (in.available() > 0) {
                Event ev = (Event) in.readObject();
                frame.update_event(ev);
            }
        }
    }
}
