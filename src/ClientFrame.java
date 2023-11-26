import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientFrame extends JFrame implements KeyEventDispatcher {
    int MAX_PLAYER_CNT = 4;
    Socket connection;
    ObjectOutputStream out;

    int X_CNT = 20, Y_CNT = 10, WALL_SIZE = 90;
    int V = 2;
    double Va = 2 * Math.PI / 150;
    Map map;
    BufferedImage wall, tank_image;

    boolean isPressedW = false, isPressedA = false, isPressedS = false, isPressedD = false;

    int id;
    Tank[] tanks = new Tank[MAX_PLAYER_CNT];

    ClientFrame(int id, Map map, Socket connection, ObjectOutputStream out) throws IOException {
        this.id = id;
        this.out = out;
        this.connection = connection;
        this.map = map;
        wall = ImageIO.read(new File("data\\wall.jpg"));
        tank_image = ImageIO.read(new File("data\\tank.png"));

        this.setUndecorated(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }

    void update() throws IOException {
        Tank cur = tanks[id];
        if (isPressedA) {
            cur.alpha -= Va;
        }
        if (isPressedD) {
            cur.alpha += Va;
        }
        if (isPressedW) {
            cur.x += V * Math.cos(cur.alpha);
            cur.y += V * Math.sin(cur.alpha);
        }
        if (isPressedS) {
            cur.x -= V * Math.cos(cur.alpha);
            cur.y -= V * Math.sin(cur.alpha);
        }

        Event e = new Event();
        e.type = Event.TANK_POSITION;
        e.int_data.add(id);
        e.double_data.add(cur.x);
        e.double_data.add(cur.y);
        e.double_data.add(cur.alpha);
        synchronized (out) {
            try {
                out.writeObject(e);
                out.flush();
            } catch (SocketException se) {
                System.out.println("BAD");
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        BufferStrategy bufferStrategy = getBufferStrategy();
        if (bufferStrategy == null) {
            createBufferStrategy(2);
            bufferStrategy = getBufferStrategy();
        }
        g = bufferStrategy.getDrawGraphics();
        g.clearRect(0, 0, getWidth(), getHeight());

        map.paint(g, wall);
        for (Tank tank : tanks) {
            if (tank == null) continue;
            tank.paint(g, tank_image);
        }

        try {
            update();
        } catch (IOException e) {
            e.printStackTrace();
        }

        g.dispose();
        bufferStrategy.show();
    }

    void update_event(Event e) {
        if (e.type == Event.TANK_CREATED) {
            int t_id = e.int_data.get(0);
            tanks[t_id] = new Tank(e.double_data.get(0),  e.double_data.get(1),  e.double_data.get(2));
        }
        if (e.type == Event.TANK_POSITION) {
            int t_id = e.int_data.get(0);
            if (tanks[t_id] == null) tanks[t_id] = new Tank();
            tanks[t_id].x = e.double_data.get(0);
            tanks[t_id].y = e.double_data.get(1);
            tanks[t_id].alpha = e.double_data.get(2);
        }
        if (e.type == Event.TANK_DELETED) {
            int t_id = e.int_data.get(0);
            tanks[t_id] = null;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        if (e.getKeyCode() == 87) {    // W
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedW = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedW = false;
            }
        }
        if (e.getKeyCode() == 65) {    // A
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedA = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedA = false;
            }
        }
        if (e.getKeyCode() == 83) {    // S
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedS = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedS = false;
            }
        }
        if (e.getKeyCode() == 68) {    // D
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                isPressedD = true;
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                isPressedD = false;
            }
        }
        return false;
    }
}
