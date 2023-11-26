import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static int MAX_PLAYERS_CNT = 4;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner sys_in = new Scanner(System.in);
        String ip;
        System.out.print("INPUT SERVER IP: ");
        ip = sys_in.next();
        int port;
        System.out.print("INPUT SERVER PORT: ");
        port = sys_in.nextInt();

        Socket connection = new Socket(ip, port);
        ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
        InputStream input = connection.getInputStream();
        ObjectInputStream in = new ObjectInputStream(input);

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        int id = in.read();
        if (id == MAX_PLAYERS_CNT) {
            connection.close();
            System.out.println("TOO MANY PLAYERS");
            return;
        }

        Thread ShutdownHook = new Thread(() -> {
            Event e = new Event();
            e.type = Event.TANK_DELETED;
            e.int_data.add(id);
            synchronized (out) {
                try {
                    out.writeObject(e);
                    out.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            System.out.println("CLOSED");
        });
        Runtime.getRuntime().addShutdownHook(ShutdownHook);

        ClientFrame frame = new ClientFrame(id, Map.open("BASE"), connection, out);

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
        while (!connection.isClosed()) {
            try {
                while (input.available() > 0) {
                    Event ev = (Event) in.readObject();
                    frame.update_event(ev);
                }
                frame.repaint();
            } catch (IOException exc) {
                break;
            }
        }
    }
}
