import java.io.*;
import java.net.Socket;

public class Sender implements Runnable {
    Socket client;
    ObjectInputStream in;
    ObjectOutputStream out;
    InputStream input;
    Server server;
    int user_id;

    Sender(Socket socket, int user_id, Server server) throws IOException {
        client = socket;
        out = new ObjectOutputStream(client.getOutputStream());
        input = client.getInputStream();
        in = new ObjectInputStream(input);

        this.server = server;
        this.user_id = user_id;
    }

    @Override
    public void run() {
        try {
            synchronized (out) {
                out.write(user_id);
                out.flush();
            }

            server.users[user_id] = this;
            while (true) {
                if (input.available() > 0) {
                    Event ev = (Event) in.readObject();
                    send(ev);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        server.users_cnt--;
        server.users[user_id] = null;
    }

    void send(Event ev) throws IOException {
        for (Sender user : server.users) {
            if (user == null || user.equals(this)) continue;

            synchronized (user.out) {
                user.out.writeObject(ev);
                user.out.flush();
            }
        }
    }
}
