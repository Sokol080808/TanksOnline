import java.io.*;
import java.net.Socket;

public class Sender implements Runnable {
    Socket client;
    ObjectInputStream in;
    ObjectOutputStream out;
    Server server;
    int user_id;

    Sender(Socket socket, int user_id, Server server) throws IOException {
        client = socket;
        out = new ObjectOutputStream(client.getOutputStream());
        in = new ObjectInputStream(client.getInputStream());
        this.server = server;
        this.user_id = user_id;
        out.write(user_id);
        out.flush();
    }

    @Override
    public void run() {
        try {
            while (true) {


                if (in.available() > 0) {
                    Event ev = (Event) in.readObject();
                    send(ev);
                } else {

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
            if (user.equals(this)) continue;

            user.out.writeObject(ev);
        }
    }
}
