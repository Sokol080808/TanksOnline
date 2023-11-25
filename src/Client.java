import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Client {
    static int MAX_PLAYERS_CNT = 4;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket connection = new Socket("localhost", 1337);
        ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
        InputStream input = connection.getInputStream();
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        Thread ShutdownHook = new Thread(() -> {
           System.out.println("ABOBA");
        });
        Runtime.getRuntime().addShutdownHook(ShutdownHook);

        int id = in.read();
        if (id == MAX_PLAYERS_CNT) {
            connection.close();
            System.out.println("TOO MANY PLAYERS");
            return;
        }

        ClientFrame frame = new ClientFrame(id, Map.open("BASE"), out);

        frame.tanks[id] = new Tank();
        Event e = new Event();
        e.type = Event.TANK_CREATED;
        e.int_data.add(id);
        e.double_data.add(500.0);
        e.double_data.add(500.0);
        e.double_data.add(10.0);
        synchronized (out) {
            out.writeObject(e);
            out.flush();
        }

        manager.addKeyEventDispatcher(frame);
        while (true) {
            while (input.available() > 0) {
                Event ev = (Event) in.readObject();
                frame.update_event(ev);
            }
            frame.repaint();
        }
    }
}
